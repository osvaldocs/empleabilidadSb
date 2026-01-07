package com.example.demo.application.port.in;

import java.util.UUID;

public interface CreateTaskUseCase {
    UUID create(String title, UUID projectId, UUID userId);
}
