package com.alpopan.taskmaster.service;

import com.alpopan.taskmaster.dto.TaskDto;
import java.util.List;

public interface TaskService {
    TaskDto create(TaskDto dto);
    TaskDto getById(Long id);
    List<TaskDto> getAll();
    List<TaskDto> getByAssignee(Long userId);
    TaskDto update(Long id, TaskDto dto);
    void delete(Long id);
}
