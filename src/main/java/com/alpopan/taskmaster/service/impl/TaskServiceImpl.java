// src/main/java/com/alpopan/taskmaster/service/impl/TaskServiceImpl.java
package com.alpopan.taskmaster.service.impl;

import com.alpopan.taskmaster.dto.TaskDto;
import com.alpopan.taskmaster.event.TaskCreatedEvent;
import com.alpopan.taskmaster.model.Task;
import com.alpopan.taskmaster.model.User;
import com.alpopan.taskmaster.repository.TaskRepository;
import com.alpopan.taskmaster.repository.UserRepository;
import com.alpopan.taskmaster.service.TaskService;
import com.alpopan.taskmaster.task.StatusHandler;
import io.micrometer.core.annotation.Timed;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepo;
    private final UserRepository userRepo;
    private final Map<String, StatusHandler> handlers;
    private final ApplicationEventPublisher publisher;

    public TaskServiceImpl(TaskRepository taskRepo,
                           UserRepository userRepo,
                           List<StatusHandler> handlerList,
                           ApplicationEventPublisher publisher) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(StatusHandler::key, h -> h));
        this.publisher = publisher;
    }

    @Override
    public TaskDto create(TaskDto dto) {
        User assignee = userRepo.findById(dto.getAssigneeId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Assignee not found: " + dto.getAssigneeId())
                );

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setAssignee(assignee);
        if (dto.getStatus() != null) {
            task.setStatus(Task.Status.valueOf(dto.getStatus()));
        }

        Task saved = taskRepo.save(task);
        handlers.get(saved.getStatus().name()).handle(saved);
        publisher.publishEvent(new TaskCreatedEvent(this, saved));
        return toDto(saved);
    }

    @Override
    @Timed(value = "task.service.getById", description = "Time taken to fetch task by id")
    @Cacheable(cacheNames = "tasks", key = "#id")
    @Transactional(readOnly = true)
    public TaskDto getById(Long id) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Task not found: " + id)
                );
        return toDto(task);
    }

    @Override
    @Timed("task.service.getAll")
    @Cacheable(cacheNames = "tasksAll")
    @Transactional(readOnly = true)
    public List<TaskDto> getAll() {
        return taskRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getByAssignee(Long userId) {
        return taskRepo.findByAssigneeId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(cacheNames = "tasks", key = "#id")
    public TaskDto update(Long id, TaskDto dto) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Task not found: " + id)
                );
        User assignee = userRepo.findById(dto.getAssigneeId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Assignee not found: " + dto.getAssigneeId())
                );

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setStatus(Task.Status.valueOf(dto.getStatus()));
        task.setAssignee(assignee);

        Task updated = taskRepo.save(task);
        handlers.get(updated.getStatus().name()).handle(updated);
        return toDto(updated);
    }

    @Override
    @CacheEvict(cacheNames = {"tasks", "tasksAll"}, allEntries = true)
    public void delete(Long id) {
        if (!taskRepo.existsById(id)) {
            throw new EntityNotFoundException("Task not found: " + id);
        }
        taskRepo.deleteById(id);
    }

    private TaskDto toDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setDueDate(task.getDueDate());
        dto.setAssigneeId(task.getAssignee() != null
                ? task.getAssignee().getId()
                : null);
        dto.setStatus(task.getStatus() != null
                ? task.getStatus().name()
                : null);
        return dto;
    }
}
