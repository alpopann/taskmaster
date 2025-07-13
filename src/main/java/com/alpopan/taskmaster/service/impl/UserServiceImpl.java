// src/main/java/com/alpopan/taskmaster/service/impl/UserServiceImpl.java
package com.alpopan.taskmaster.service.impl;

import com.alpopan.taskmaster.dto.UserDto;
import com.alpopan.taskmaster.event.UserRegisteredEvent;
import com.alpopan.taskmaster.model.User;
import com.alpopan.taskmaster.repository.UserRepository;
import com.alpopan.taskmaster.service.UserService;
import com.alpopan.taskmaster.service.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher publisher;

    public UserServiceImpl(UserRepository repo,
                           UserMapper mapper,
                           PasswordEncoder encoder,
                           ApplicationEventPublisher publisher) {
        this.repo      = repo;
        this.mapper    = mapper;
        this.encoder   = encoder;
        this.publisher = publisher;
    }

    @Override
    public UserDto create(UserDto dto) {
        // 1) хешируем пароль
        dto.setPassword(encoder.encode(dto.getPassword()));
        // 2) сохраняем
        User entity = mapper.toEntity(dto);
        User saved  = repo.save(entity);
        // 3) бросаем событие для Listener-ов
        publisher.publishEvent(new UserRegisteredEvent(this, saved));
        // 4) возвращаем DTO без пароля
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        User user = repo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found: " + id)
                );
        return mapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return repo.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
