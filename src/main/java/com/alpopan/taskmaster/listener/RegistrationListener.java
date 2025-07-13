// src/main/java/com/alpopan/taskmaster/listener/RegistrationListener.java
package com.alpopan.taskmaster.listener;

import com.alpopan.taskmaster.event.UserRegisteredEvent;
import org.slf4j.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener {
    private static final Logger log = LoggerFactory.getLogger(RegistrationListener.class);

    @EventListener
    public void onUserRegistered(UserRegisteredEvent ev) {
        // Здесь может уйти email/SMS через соответствующий сервис
        log.info("Welcome email sent to {}", ev.getUser().getEmail());
    }
}
