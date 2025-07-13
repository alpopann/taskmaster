// src/main/java/com/alpopan/taskmaster/controller/TaskController.java
package com.alpopan.taskmaster.controller;

import com.alpopan.taskmaster.dto.TaskDto;
import com.alpopan.taskmaster.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tasks", description = "Управление задачами")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @Operation(summary = "Получить все задачи")
    @GetMapping
    public List<TaskDto> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Получить задачи пользователя")
    @GetMapping("/user/{userId}")
    public List<TaskDto> getByUser(
            @Parameter(description = "ID пользователя") @PathVariable Long userId
    ) {
        return service.getByAssignee(userId);
    }

    @Operation(summary = "Получить задачу по ID")
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getById(@PathVariable Long id) {
        TaskDto dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Создать новую задачу")
    @PostMapping
    public ResponseEntity<TaskDto> create(
            @RequestBody @Valid TaskDto dto
    ) {
        TaskDto created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Обновить задачу")
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> update(
            @PathVariable Long id,
            @RequestBody @Valid TaskDto dto
    ) {
        TaskDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить задачу")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
