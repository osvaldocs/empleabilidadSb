package com.example.demo.application.port.in;

import com.example.demo.domain.model.Project;

import java.util.List;
import java.util.UUID;

public interface GetProjectsUseCase {
    List<Project> getProjects(UUID userId);
}

