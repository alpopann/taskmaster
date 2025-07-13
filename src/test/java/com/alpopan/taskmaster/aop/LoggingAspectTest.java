// src/test/java/com/alpopan/taskmaster/aop/LoggingAspectTest.java
package com.alpopan.taskmaster.aop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Service;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = LoggingAspectTest.TestConfig.class)
@ExtendWith(OutputCaptureExtension.class)
class LoggingAspectTest {

    @Autowired
    private DummyService dummyService;

    @Test
    void aspectLogsEntryAndExit(CapturedOutput output) {
        String greeting = dummyService.greet("World");

        assertThat(greeting).isEqualTo("Hello World");
        assertThat(output)
                .contains("Entering greet with args [World]")
                .containsPattern("Exiting greet; result = Hello World; took \\d+ ms");
    }

    @Configuration
    @EnableAspectJAutoProxy
    static class TestConfig {
        @Bean
        public LoggingAspect loggingAspect() {
            return new LoggingAspect();
        }

        @Bean
        public DummyService dummyService() {
            return new DummyService();
        }
    }

    @Service
    static class DummyService {
        public String greet(String name) {
            return "Hello " + name;
        }
    }
}
