package com.alpopan.taskmaster.controller;

import com.alpopan.taskmaster.dto.TaskDto;
import com.alpopan.taskmaster.model.Task;
import com.alpopan.taskmaster.model.User;
import com.alpopan.taskmaster.repository.TaskRepository;
import com.alpopan.taskmaster.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Base64;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper om;
    @Autowired private TaskRepository taskRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    private String basicAuth;
    private User testUser;

    @BeforeEach
    void setUp() {
        taskRepo.deleteAll();
        userRepo.deleteAll();

        testUser = new User();
        testUser.setUsername("tester");
        testUser.setEmail("tester@example.com");
        testUser.setPassword(passwordEncoder.encode("pass1234"));
        testUser = userRepo.save(testUser);

        String token = Base64.getEncoder()
                .encodeToString("tester:pass1234".getBytes());
        basicAuth = "Basic " + token;
    }

    @Test
    void getAllTasks_Unauthorized_WhenNoAuth() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createTask_BadRequest_WhenTitleBlank() throws Exception {
        TaskDto dto = new TaskDto();
        dto.setTitle("");
        dto.setDescription("no title");
        dto.setDueDate(LocalDateTime.now().plusDays(1));
        dto.setAssigneeId(testUser.getId());

        mockMvc.perform(post("/api/tasks")
                        .with(httpBasic("tester","pass1234"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title is required"));
    }

    @Test
    void createTask_Ok_WhenValid() throws Exception {
        TaskDto dto = new TaskDto();
        dto.setTitle("My Task");
        dto.setDescription("valid");
        dto.setDueDate(LocalDateTime.now().plusDays(2));
        dto.setAssigneeId(testUser.getId());

        mockMvc.perform(post("/api/tasks")
                        .with(httpBasic("tester","pass1234"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("My Task"))
                .andExpect(jsonPath("$.assigneeId").value(testUser.getId()));
    }

    @Test
    void getAllTasks_Ok_AfterCreate() throws Exception {
        // Сохраним через JPA, чтобы проверить GET /api/tasks
        Task saved = new Task();
        saved.setTitle("Another");
        saved.setDueDate(LocalDateTime.now().plusDays(3));
        saved.setAssignee(testUser);
        taskRepo.save(saved);

        mockMvc.perform(get("/api/tasks")
                        .with(httpBasic("tester","pass1234")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getByUser_Ok() throws Exception {
        Task t = new Task();
        t.setTitle("Utask");
        t.setDueDate(LocalDateTime.now().plusDays(1));
        t.setAssignee(testUser);
        taskRepo.save(t);

        mockMvc.perform(get("/api/tasks/user/{userId}", testUser.getId())
                        .with(httpBasic("tester","pass1234")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assigneeId").value(testUser.getId()));
    }

    @Test
    void updateTask_Ok_WhenValid() throws Exception {
        Task saved = new Task();
        saved.setTitle("Old");
        saved.setDueDate(LocalDateTime.now().plusDays(1));
        saved.setAssignee(testUser);
        saved = taskRepo.save(saved);

        TaskDto dto = new TaskDto();
        dto.setTitle("New");
        dto.setDescription("up");
        dto.setDueDate(LocalDateTime.now().plusDays(5));
        dto.setAssigneeId(testUser.getId());
        dto.setStatus("DONE");

        mockMvc.perform(put("/api/tasks/{id}", saved.getId())
                        .with(httpBasic("tester","pass1234"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void deleteTask_NoContent_WhenExists() throws Exception {
        Task toDelete = new Task();
        toDelete.setTitle("Del");
        toDelete.setDueDate(LocalDateTime.now().plusDays(1));
        toDelete.setAssignee(testUser);
        toDelete = taskRepo.save(toDelete);

        mockMvc.perform(delete("/api/tasks/{id}", toDelete.getId())
                        .with(httpBasic("tester","pass1234")))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(taskRepo.findById(toDelete.getId()).isPresent());
    }
}
