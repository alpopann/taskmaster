// src/test/java/com/alpopan/taskmaster/security/JwtTokenProviderTest.java
package com.alpopan.taskmaster.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        // создаём простой InMemoryUserDetailsService с одним юзером
        var uds = new InMemoryUserDetailsManager(
                User.withUsername("user")
                        .password("ignored")    // пароль здесь не важен
                        .roles("USER")
                        .build()
        );

        // инжектим секрет и время жизни прямо в конструктор
        provider = new JwtTokenProvider(
                "testSecretThatIsLongEnoughToEncode",
                3600_000,             // 1 час
                uds
        );
    }

    @Test
    void createAndValidateToken() {
        String token = provider.createToken("user", List.of("ROLE_USER"));

        assertThat(token).isNotBlank();
        assertThat(provider.validateToken(token)).isTrue();

        var auth = provider.getAuthentication(token);
        assertThat(auth.getName()).isEqualTo("user");
        assertThat(auth.getAuthorities())
                .extracting(a -> a.getAuthority())
                .containsExactly("ROLE_USER");
    }
}
