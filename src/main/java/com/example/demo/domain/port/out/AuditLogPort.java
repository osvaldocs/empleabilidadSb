package com.example.demo.domain.port.out;

import java.util.UUID;

public interface AuditLogPort {
    void log(String action, UUID entityId);
}
