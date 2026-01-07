package com.example.demo.infrastructure.notification;

import com.example.demo.domain.port.out.NotificationPort;
import java.util.UUID;

public class NotificationAdapter implements NotificationPort {

    @Override
    public void notifyUser(UUID userId, String message) {
        // implementación simple: consola
        System.out.println("[NOTIFY] User: " + userId + " | Message: " + message);
    }
}
