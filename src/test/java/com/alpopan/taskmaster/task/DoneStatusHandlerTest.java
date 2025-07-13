package com.alpopan.taskmaster.task;

import com.alpopan.taskmaster.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoneStatusHandlerTest {

    private final StatusHandler handler = new DoneStatusHandler();

    @Test
    void key_ShouldBeDONE() {
        assertEquals(Task.Status.DONE.name(), handler.key());
    }

    @Test
    void handle_DoesNotThrow() {
        Task t = new Task();
        t.setId(3L);
        assertDoesNotThrow(() -> handler.handle(t));
    }
}
