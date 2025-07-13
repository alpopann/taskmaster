package com.alpopan.taskmaster.task;

import com.alpopan.taskmaster.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenStatusHandlerTest {

    private final StatusHandler handler = new OpenStatusHandler();

    @Test
    void key_ShouldBeOPEN() {
        assertEquals(Task.Status.OPEN.name(), handler.key());
    }

    @Test
    void handle_DoesNotThrow() {
        Task t = new Task();
        t.setId(1L);
        assertDoesNotThrow(() -> handler.handle(t));
    }
}
