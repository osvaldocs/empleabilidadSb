package com.example.demo.infrastructure.audit;

import com.example.demo.domain.port.out.AuditLogPort;
import java.util.UUID;

public class AuditAdapter implements AuditLogPort {

    @Override
    public void log(String action, UUID entityId) {
        System.out.println("[AUDIT] Action: " + action + " | Entity: " + entityId);
    }
}
