package com.alpopan.taskmaster.service.impl;

import com.alpopan.taskmaster.dto.UserDto;
import com.alpopan.taskmaster.event.UserRegisteredEvent;
import com.alpopan.taskmaster.model.User;
import com.alpopan.taskmaster.repository.UserRepository;
import com.alpopan.taskmaster.service.impl.UserServiceImpl;
import com.alpopan.taskmaster.service.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock private UserRepository repo;
    @Mock private UserMapper mapper;
    @Mock private PasswordEncoder encoder;
    @Mock private ApplicationEventPublisher publisher;

    @InjectMocks private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldHashSaveAndPublish() {
        // подготовка DTO
        UserDto dto = new UserDto();
        dto.setUsername("john");
        dto.setEmail("john@example.com");
        dto.setPassword("plain");

        // маппинг → сущность
        User entity = new User();
        when(mapper.toEntity(any())).thenReturn(entity);

        // сохранение
        User savedEntity = new User();
        savedEntity.setId(42L);
        when(repo.save(entity)).thenReturn(savedEntity);

        // обратный маппинг → DTO
        UserDto outDto = new UserDto();
        outDto.setId(42L);
        outDto.setUsername("john");
        outDto.setEmail("john@example.com");
        when(mapper.toDto(savedEntity)).thenReturn(outDto);

        // эмуляция хеширования
        when(encoder.encode("plain")).thenReturn("hashed");

        // вызов
        UserDto result = service.create(dto);

        // верификация хеширования и сохранения
        verify(encoder).encode("plain");
        verify(mapper).toEntity(argThat(d -> "hashed".equals(d.getPassword())));
        verify(repo).save(entity);
        verify(mapper).toDto(savedEntity);

        // проверяем публикацию события
        ArgumentCaptor<UserRegisteredEvent> cap =
                ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(publisher).publishEvent(cap.capture());
        assertEquals(savedEntity, cap.getValue().getUser());

        // результат
        assertEquals(42L, result.getId());
        assertEquals("john", result.getUsername());
    }

    @Test
    void getById_Found() {
        User u = new User();
        u.setId(7L);
        when(repo.findById(7L)).thenReturn(Optional.of(u));
        UserDto dto = new UserDto();
        dto.setId(7L);
        when(mapper.toDto(u)).thenReturn(dto);

        UserDto result = service.getById(7L);
        assertEquals(7L, result.getId());
    }

    @Test
    void getById_NotFound() {
        when(repo.findById(9L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getById(9L));
    }

    @Test
    void getAll_ShouldReturnList() {
        User u1 = new User(); u1.setId(1L);
        User u2 = new User(); u2.setId(2L);
        when(repo.findAll()).thenReturn(Arrays.asList(u1, u2));
        UserDto d1 = new UserDto(); d1.setId(1L);
        UserDto d2 = new UserDto(); d2.setId(2L);
        when(mapper.toDto(u1)).thenReturn(d1);
        when(mapper.toDto(u2)).thenReturn(d2);

        List<UserDto> all = service.getAll();
        assertEquals(2, all.size());
        assertEquals(1L, all.get(0).getId());
        assertEquals(2L, all.get(1).getId());
    }
}
