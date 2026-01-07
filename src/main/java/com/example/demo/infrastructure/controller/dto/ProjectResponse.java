package com.example.demo.infrastructure.controller.dto;

import com.example.demo.domain.model.Project;
import com.example.demo.domain.model.ProjectStatus;

import java.util.UUID;

public record ProjectResponse(UUID id, String name, ProjectStatus status) {
    public ProjectResponse(Project project) {
        this(project.getId(), project.getName(), project.getStatus());
    }
}
