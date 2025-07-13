package com.alpopan.taskmaster.controller;

import com.alpopan.taskmaster.dto.UserDto;
import com.alpopan.taskmaster.model.User;
import com.alpopan.taskmaster.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper om;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    private String basicAuth;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();

        // Seed admin user directly as entity для Basic Auth
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        userRepo.save(admin);

        // Basic token
        String token = Base64.getEncoder()
                .encodeToString("admin:admin123".getBytes());
        basicAuth = "Basic " + token;
    }

    @Test
    void getAllUsers_Unauthorized_WhenNoAuth() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllUsers_Ok() throws Exception {
        // Добавим ещё одного пользователя в БД
        User another = new User();
        another.setUsername("alex");
        another.setEmail("alex@example.com");
        another.setPassword(passwordEncoder.encode("pass456"));
        userRepo.save(another);

        mockMvc.perform(get("/api/users")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("admin")))
                .andExpect(jsonPath("$[0].email",    is("admin@example.com")))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[1].username", is("alex")))
                .andExpect(jsonPath("$[1].email",    is("alex@example.com")))
                .andExpect(jsonPath("$[1].password").doesNotExist());
    }

    @Test
    void getUserById_Found() throws Exception {
        Long adminId = userRepo.findAll().get(0).getId();

        mockMvc.perform(get("/api/users/{id}", adminId)
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("admin")))
                .andExpect(jsonPath("$.email",    is("admin@example.com")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void getUserById_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 999L)
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_Ok() throws Exception {
        // Формируем DTO для POST
        UserDto dto = new UserDto();
        dto.setUsername("bob");
        dto.setEmail("bob@example.com");
        dto.setPassword("bobpass");

        mockMvc.perform(post("/api/users")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username", is("bob")))
                .andExpect(jsonPath("$.email",    is("bob@example.com")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void createUser_Invalid_Returns400() throws Exception {
        UserDto dto = new UserDto();
        dto.setUsername("");
        dto.setEmail("bad");
        dto.setPassword("");

        mockMvc.perform(post("/api/users")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username", is("Username is required")))
                .andExpect(jsonPath("$.email",    is("Email must be valid")))
                .andExpect(jsonPath("$.password", is("Password must be at least 6 characters")));
    }
}
