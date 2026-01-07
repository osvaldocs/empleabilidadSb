package com.example.demo.infrastructure.controller.dto;

import com.example.demo.domain.model.Task;

import java.util.UUID;

public record TaskResponse(UUID id, String title, boolean completed) {
    public TaskResponse(Task task) {
        this(task.getId(), task.getTitle(), task.isCompleted());
    }
}
