// src/main/java/com/alpopan/taskmaster/task/InProgressStatusHandler.java
package com.alpopan.taskmaster.task;

import com.alpopan.taskmaster.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InProgressStatusHandler implements StatusHandler {
    private static final Logger log = LoggerFactory.getLogger(InProgressStatusHandler.class);

    @Override
    public void handle(Task task) {
        // Например: отправить сообщение assignee о старте работы
        log.info("Task {} в работе", task.getId());
    }

    @Override
    public String key() {
        return Task.Status.IN_PROGRESS.name();
    }
}
