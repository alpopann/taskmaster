// src/main/java/com/alpopan/taskmaster/config/MetricsConfig.java
package com.alpopan.taskmaster.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.*;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> commonTags() {
        return registry ->
                registry.config().commonTags("application", "task-master");
    }
}
