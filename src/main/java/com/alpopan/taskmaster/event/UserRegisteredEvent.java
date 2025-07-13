// src/main/java/com/alpopan/taskmaster/event/UserRegisteredEvent.java
package com.alpopan.taskmaster.event;

import com.alpopan.taskmaster.model.User;
import org.springframework.context.ApplicationEvent;

public class UserRegisteredEvent extends ApplicationEvent {
    private final User user;

    public UserRegisteredEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
