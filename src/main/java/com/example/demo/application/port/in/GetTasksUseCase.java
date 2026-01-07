package com.example.demo.application.port.in;

import com.example.demo.domain.model.Task;

import java.util.List;
import java.util.UUID;

public interface GetTasksUseCase {
    List<Task> getTasks(UUID projectId, UUID userId);
}

