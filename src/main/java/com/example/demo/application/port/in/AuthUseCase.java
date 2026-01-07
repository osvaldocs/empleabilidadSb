package com.example.demo.application.port.in;

import com.example.demo.infrastructure.controller.dto.AuthResponse;

public interface AuthUseCase {
    AuthResponse register(String username, String password);
    AuthResponse login(String username, String password);
}
