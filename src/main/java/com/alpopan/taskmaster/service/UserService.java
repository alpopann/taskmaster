package com.alpopan.taskmaster.service;

import com.alpopan.taskmaster.dto.UserDto;
import java.util.List;

public interface UserService {
    UserDto create(UserDto dto);
    UserDto getById(Long id);
    List<UserDto> getAll();
}