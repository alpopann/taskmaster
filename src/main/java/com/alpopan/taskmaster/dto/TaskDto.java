// src/main/java/com/alpopan/taskmaster/dto/TaskDto.java
package com.alpopan.taskmaster.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class TaskDto {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private LocalDateTime createdAt;

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDateTime dueDate;

    @NotNull(message = "Assignee is required")
    private Long assigneeId;

    private String status;

    // ======== Геттеры и сеттеры ========
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // ======== equals + hashCode ========
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskDto)) return false;
        TaskDto that = (TaskDto) o;
        return Objects.equals(id, that.id)
                && Objects.equals(title, that.title)
                && Objects.equals(dueDate, that.dueDate)
                && Objects.equals(assigneeId, that.assigneeId)
                && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, dueDate, assigneeId, status);
    }

    // ======== toString ========
    @Override
    public String toString() {
        return "TaskDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", dueDate=" + dueDate +
                ", assigneeId=" + assigneeId +
                ", status='" + status + '\'' +
                '}';
    }
}
