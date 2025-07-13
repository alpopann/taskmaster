// src/main/java/com/alpopan/taskmaster/listener/TaskCreatedListener.java
package com.alpopan.taskmaster.listener;

import com.alpopan.taskmaster.event.TaskCreatedEvent;
import org.slf4j.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TaskCreatedListener {
    private static final Logger log = LoggerFactory.getLogger(TaskCreatedListener.class);

    @EventListener
    public void onTaskCreated(TaskCreatedEvent ev) {
        log.info("Notified assignee {} about new task #{}",
                ev.getTask().getAssignee().getEmail(),
                ev.getTask().getId());
        // Здесь можно вызывать NotificationService или отправлять email
    }
}
