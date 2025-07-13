package com.alpopan.taskmaster.listener;

import com.alpopan.taskmaster.event.UserRegisteredEvent;
import com.alpopan.taskmaster.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationListenerTest {

    private RegistrationListener listener;

    @BeforeEach
    void setUp() {
        listener = new RegistrationListener();
    }

    @Test
    void onUserRegistered_DoesNotThrow() {
        User u = new User();
        u.setId(5L);
        u.setEmail("a@b.com");
        UserRegisteredEvent ev = new UserRegisteredEvent(this, u);
        assertDoesNotThrow(() -> listener.onUserRegistered(ev));
    }
}
