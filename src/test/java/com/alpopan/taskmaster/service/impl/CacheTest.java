// src/test/java/com/alpopan/taskmaster/service/CacheTest.java
package com.alpopan.taskmaster.service;

import com.alpopan.taskmaster.dto.TaskDto;
import com.alpopan.taskmaster.model.Task;
import com.alpopan.taskmaster.model.User;
import com.alpopan.taskmaster.repository.TaskRepository;
import com.alpopan.taskmaster.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.cache.CacheManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class CacheTest {

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskRepository taskRepo;

    @MockBean
    private UserRepository userRepo;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testGetById_Cacheable() {
        Task t = new Task();
        t.setId(1L);
        t.setAssignee(new User()); t.getAssignee().setId(2L);
        when(taskRepo.findById(1L)).thenReturn(Optional.of(t));

        // первый раз — репозиторий вызывается
        TaskDto dto1 = taskService.getById(1L);
        assertThat(dto1.getId()).isEqualTo(1L);
        verify(taskRepo, times(1)).findById(1L);

        // второй раз — из кеша, репозиторий не вызывается
        TaskDto dto2 = taskService.getById(1L);
        assertThat(dto2.getId()).isEqualTo(1L);
        verifyNoMoreInteractions(taskRepo);
    }

    @Test
    void testGetAll_Cacheable() {
        when(taskRepo.findAll()).thenReturn(List.of(new Task()));
        taskService.getAll();
        taskService.getAll();
        verify(taskRepo, times(1)).findAll();
    }
}
