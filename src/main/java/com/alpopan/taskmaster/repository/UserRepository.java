package com.alpopan.taskmaster.repository;

import com.alpopan.taskmaster.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // можно добавлять методы поиска, например:
    User findByEmail(String email);
    Optional<User> findByUsername(String username);
}

