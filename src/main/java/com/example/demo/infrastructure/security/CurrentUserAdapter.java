package com.example.demo.infrastructure.security;

import com.example.demo.domain.port.out.CurrentUserPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUserAdapter implements CurrentUserPort {

    @Override
    public UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String userIdStr) {
            return UUID.fromString(userIdStr);
        }
        throw new RuntimeException("No authenticated user found");
    }
}
