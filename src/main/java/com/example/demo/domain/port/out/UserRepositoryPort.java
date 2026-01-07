package com.example.demo.domain.port.out;

import com.example.demo.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    void save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
}
