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

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper om;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void init() {
        userRepo.deleteAll();
    }

    @Test
    void register_ValidUser_ReturnsOk() throws Exception {
        UserDto dto = new UserDto();
        dto.setUsername("john");
        dto.setEmail("john@example.com");
        dto.setPassword("secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void register_InvalidUser_Returns400() throws Exception {
        UserDto dto = new UserDto();
        dto.setUsername("");
        dto.setEmail("bad");
        dto.setPassword("123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username", is("Username is required")))
                .andExpect(jsonPath("$.email", is("Email must be valid")))
                .andExpect(jsonPath("$.password", is("Password must be at least 6 characters")));
    }

    @Test
    void afterRegister_CanAccessProtectedEndpoint() throws Exception {
        // 1) Зарегистрируем
        UserDto dto = new UserDto();
        dto.setUsername("bob");
        dto.setEmail("bob@example.com");
        dto.setPassword("bobpass");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // 2) Попробуем GET /api/tasks (должен вернуть 200 + пустой массив)
        mockMvc.perform(get("/api/tasks")
                        .with(httpBasic("bob","bobpass")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", empty()));
    }
}
