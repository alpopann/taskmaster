package com.alpopan.taskmaster.service.mapper;

import com.alpopan.taskmaster.dto.UserDto;
import com.alpopan.taskmaster.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // DTO → Entity (используется в createUser)
    User toEntity(UserDto dto);

    // Entity → DTO, игнорируем поле password
    @Mapping(target = "password", ignore = true)
    UserDto toDto(User user);
}
