package com.alpopan.taskmaster.task;

import com.alpopan.taskmaster.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InProgressStatusHandlerTest {

    private final StatusHandler handler = new InProgressStatusHandler();

    @Test
    void key_ShouldBeIN_PROGRESS() {
        assertEquals(Task.Status.IN_PROGRESS.name(), handler.key());
    }

    @Test
    void handle_DoesNotThrow() {
        Task t = new Task();
        t.setId(2L);
        assertDoesNotThrow(() -> handler.handle(t));
    }
}
