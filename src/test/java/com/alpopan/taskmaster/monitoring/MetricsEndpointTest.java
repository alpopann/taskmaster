// src/test/java/com/alpopan/taskmaster/monitoring/MetricsEndpointTest.java
package com.alpopan.taskmaster.monitoring;

import com.alpopan.taskmaster.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class MetricsEndpointTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void metricsEndpoint_returnsMetricNames() throws Exception {
        mvc.perform(get("/actuator/metrics/task.service.getById"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("task.service.getById"))
                .andExpect(jsonPath("$.measurements").exists());
    }

    @Test
    void prometheusEndpoint_returnsPrometheusFormat() throws Exception {
        mvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("http_server_requests_seconds_count")))
                .andExpect(content().string(containsString("task_service_getById_seconds_count")));
    }
}
