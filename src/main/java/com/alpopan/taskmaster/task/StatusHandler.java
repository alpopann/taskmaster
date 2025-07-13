// src/main/java/com/alpopan/taskmaster/task/StatusHandler.java
package com.alpopan.taskmaster.task;

import com.alpopan.taskmaster.model.Task;

public interface StatusHandler {
    /**
     * Вызывается при смене статуса задачи на key().
     */
    void handle(Task task);

    /**
     * Ключ, по которому бин регистрируется: OPEN, IN_PROGRESS или DONE
     */
    String key();
}
