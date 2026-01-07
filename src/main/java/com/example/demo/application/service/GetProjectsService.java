package com.example.demo.application.service;

import com.example.demo.application.port.in.GetProjectsUseCase;
import com.example.demo.domain.model.Project;
import com.example.demo.domain.port.out.ProjectRepositoryPort;

import java.util.List;
import java.util.UUID;

public class GetProjectsService implements GetProjectsUseCase {

    private final ProjectRepositoryPort projectRepository;

    public GetProjectsService(ProjectRepositoryPort projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public List<Project> getProjects(UUID userId) {
        return projectRepository.findByOwnerId(userId);
    }
}

