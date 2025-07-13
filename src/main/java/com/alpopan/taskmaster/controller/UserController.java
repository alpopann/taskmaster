package com.alpopan.taskmaster.controller;

import com.alpopan.taskmaster.dto.UserDto;
import com.alpopan.taskmaster.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "Управление пользователями")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(summary = "Получить всех пользователей")
    @GetMapping
    public List<UserDto> getAllUsers() {
        return service.getAll();
    }

    @Operation(summary = "Получить пользователя по ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.of(
                service.getAll().stream().filter(u -> u.getId().equals(id)).findFirst()
        );
    }

    @Operation(summary = "Создать нового пользователя")
    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @RequestBody @Valid UserDto dto
    ) {
        UserDto created = service.create(dto);
        return ResponseEntity.ok(created);
    }
}
