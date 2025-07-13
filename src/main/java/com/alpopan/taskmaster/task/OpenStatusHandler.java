// src/main/java/com/alpopan/taskmaster/task/OpenStatusHandler.java
package com.alpopan.taskmaster.task;

import com.alpopan.taskmaster.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OpenStatusHandler implements StatusHandler {
    private static final Logger log = LoggerFactory.getLogger(OpenStatusHandler.class);

    @Override
    public void handle(Task task) {
        // Например: сбросить дату завершения, подготовить уведомление
        log.info("Task {} открыт", task.getId());
    }

    @Override
    public String key() {
        return Task.Status.OPEN.name();
    }
}
