package com.example.demo.domain.port.out;

import java.util.UUID;

public interface NotificationPort {
    void notifyUser(UUID userId, String message);
}
