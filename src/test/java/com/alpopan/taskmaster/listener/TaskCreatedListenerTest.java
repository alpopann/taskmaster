package com.alpopan.taskmaster.listener;

import com.alpopan.taskmaster.event.TaskCreatedEvent;
import com.alpopan.taskmaster.model.Task;
import com.alpopan.taskmaster.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskCreatedListenerTest {

    private TaskCreatedListener listener;

    @BeforeEach
    void setUp() {
        listener = new TaskCreatedListener();
    }

    @Test
    void onTaskCreated_DoesNotThrow() {
        Task t = new Task();
        t.setId(8L);

        // Устанавливаем assignee, чтобы не было NPE
        User u = new User();
        u.setEmail("a@b.com");
        t.setAssignee(u);

        TaskCreatedEvent ev = new TaskCreatedEvent(this, t);
        assertDoesNotThrow(() -> listener.onTaskCreated(ev));
    }
}
