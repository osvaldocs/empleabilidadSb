package com.example.demo.infrastructure.controller;

import com.example.demo.application.port.in.AuthUseCase;
import com.example.demo.infrastructure.controller.dto.AuthRequest;
import com.example.demo.infrastructure.controller.dto.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API para autenticación y registro de usuarios")
public class AuthController {

    private final AuthUseCase authUseCase; // u

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario y retorna un token JWT")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        AuthResponse response = authUseCase.register(request.username(), request.password());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT. Use este token en el botón 'Authorize' de Swagger")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authUseCase.login(request.username(), request.password());
        return ResponseEntity.ok(response);
    }
}
