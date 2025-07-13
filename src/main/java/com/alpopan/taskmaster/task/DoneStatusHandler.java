// src/main/java/com/alpopan/taskmaster/task/DoneStatusHandler.java
package com.alpopan.taskmaster.task;

import com.alpopan.taskmaster.model.Task;
import com.alpopan.taskmaster.task.StatusHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DoneStatusHandler implements StatusHandler {
    private static final Logger log = LoggerFactory.getLogger(DoneStatusHandler.class);

    @Override
    public void handle(Task task) {
        // Например: зафиксировать дату закрытия, отправить уведомление об окончании
        log.info("Task {} завершена", task.getId());
    }

    @Override
    public String key() {
        return Task.Status.DONE.name();
    }
}
