package com.alpopan.taskmaster.service.impl;

import com.alpopan.taskmaster.dto.TaskDto;
import com.alpopan.taskmaster.event.TaskCreatedEvent;
import com.alpopan.taskmaster.model.Task;
import com.alpopan.taskmaster.model.User;
import com.alpopan.taskmaster.repository.TaskRepository;
import com.alpopan.taskmaster.repository.UserRepository;
import com.alpopan.taskmaster.task.StatusHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    @Mock private TaskRepository taskRepo;
    @Mock private UserRepository userRepo;
    @Mock private ApplicationEventPublisher publisher;
    @Mock private StatusHandler openHandler;

    private TaskServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(openHandler.key()).thenReturn(Task.Status.OPEN.name());
        service = new TaskServiceImpl(
                taskRepo,
                userRepo,
                Collections.singletonList(openHandler),
                publisher
        );
    }

    @Test
    void create_ValidDto_SavesAndPublishesAndHandles() {
        TaskDto dto = new TaskDto();
        dto.setTitle("T1");
        dto.setDueDate(LocalDateTime.now().plusDays(1));
        dto.setAssigneeId(5L);

        User user = new User();
        user.setId(5L);
        when(userRepo.findById(5L)).thenReturn(Optional.of(user));

        Task saved = new Task();
        saved.setId(10L);
        saved.setTitle("T1");
        saved.setDueDate(dto.getDueDate());
        saved.setAssignee(user);
        when(taskRepo.save(any(Task.class))).thenReturn(saved);

        TaskDto out = service.create(dto);

        verify(openHandler).handle(saved);

        ArgumentCaptor<TaskCreatedEvent> cap =
                ArgumentCaptor.forClass(TaskCreatedEvent.class);
        verify(publisher).publishEvent(cap.capture());
        assertEquals(saved, cap.getValue().getTask());

        assertEquals(10L, out.getId());
        assertEquals("T1", out.getTitle());
        assertEquals(5L, out.getAssigneeId());
    }

    @Test
    void getById_Found() {
        Task t = new Task();
        t.setId(3L);
        // Устанавливаем assignee, чтобы toDto не упал
        User user = new User();
        user.setId(2L);
        t.setAssignee(user);

        when(taskRepo.findById(3L)).thenReturn(Optional.of(t));

        TaskDto dto = service.getById(3L);
        assertEquals(3L, dto.getId());
        assertEquals(2L, dto.getAssigneeId());
    }

    @Test
    void getById_NotFound() {
        when(taskRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    void getAll_ReturnsList() {
        Task t1 = new Task();
        t1.setId(1L);
        t1.setAssignee(new User()); t1.getAssignee().setId(100L);
        Task t2 = new Task();
        t2.setId(2L);
        t2.setAssignee(new User()); t2.getAssignee().setId(200L);
        when(taskRepo.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<TaskDto> all = service.getAll();
        assertEquals(2, all.size());
        assertEquals(100L, all.get(0).getAssigneeId());
        assertEquals(200L, all.get(1).getAssigneeId());
    }

    @Test
    void getByAssignee_ReturnsFiltered() {
        Task t = new Task();
        t.setId(7L);
        User u = new User();
        u.setId(5L);
        t.setAssignee(u);
        when(taskRepo.findByAssigneeId(5L))
                .thenReturn(Collections.singletonList(t));

        List<TaskDto> list = service.getByAssignee(5L);
        assertEquals(1, list.size());
        assertEquals(7L, list.get(0).getId());
        assertEquals(5L, list.get(0).getAssigneeId());
    }

    @Test
    void update_ValidDto_UpdatesAndHandles() {
        Task existing = new Task();
        existing.setId(8L);
        existing.setAssignee(new User());
        existing.getAssignee().setId(9L);
        when(taskRepo.findById(8L)).thenReturn(Optional.of(existing));

        User user = new User();
        user.setId(9L);
        when(userRepo.findById(9L)).thenReturn(Optional.of(user));
        when(openHandler.key()).thenReturn(Task.Status.OPEN.name());

        TaskDto dto = new TaskDto();
        dto.setTitle("New");
        dto.setDueDate(LocalDateTime.now().plusDays(2));
        dto.setAssigneeId(9L);
        dto.setStatus(Task.Status.OPEN.name());

        Task updatedEntity = new Task();
        updatedEntity.setId(8L);
        updatedEntity.setTitle("New");
        updatedEntity.setDueDate(dto.getDueDate());
        updatedEntity.setAssignee(user);
        when(taskRepo.save(existing)).thenReturn(updatedEntity);

        TaskDto out = service.update(8L, dto);

        verify(openHandler).handle(existing);
        assertEquals("New", out.getTitle());
        assertEquals(9L, out.getAssigneeId());
    }

    @Test
    void delete_Existing_Deletes() {
        when(taskRepo.existsById(7L)).thenReturn(true);
        service.delete(7L);
        verify(taskRepo).deleteById(7L);
    }

    @Test
    void delete_NotExisting_Throws() {
        when(taskRepo.existsById(99L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> service.delete(99L));
    }
}
