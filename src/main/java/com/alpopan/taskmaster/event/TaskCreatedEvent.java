// src/main/java/com/alpopan/taskmaster/event/TaskCreatedEvent.java
package com.alpopan.taskmaster.event;

import com.alpopan.taskmaster.model.Task;
import org.springframework.context.ApplicationEvent;

public class TaskCreatedEvent extends ApplicationEvent {
    private final Task task;

    public TaskCreatedEvent(Object source, Task task) {
        super(source);
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
