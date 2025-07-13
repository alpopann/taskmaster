package com.alpopan.taskmaster.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Test
    void loginAndAccessHealth() {
        AuthRequest req = new AuthRequest();
        req.setUsername("user");
        req.setPassword("password");

        // 1) Логинимся
        ResponseEntity<Map> login = rest.postForEntity(
                "http://localhost:" + port + "/auth/login",
                req, Map.class
        );
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = (String) login.getBody().get("token");
        assertThat(token).isNotEmpty();

        // 2) Достаем /actuator/health
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> ent = new HttpEntity<>(headers);

        ResponseEntity<String> health = rest.exchange(
                "http://localhost:" + port + "/actuator/health",
                HttpMethod.GET, ent, String.class
        );
        assertThat(health.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
