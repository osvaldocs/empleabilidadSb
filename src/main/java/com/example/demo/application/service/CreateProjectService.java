package com.example.demo.application.service;

import com.example.demo.application.port.in.CreateProjectUseCase;
import com.example.demo.domain.model.Project;
import com.example.demo.domain.port.out.ProjectRepositoryPort;

import java.util.UUID;

public class CreateProjectService implements CreateProjectUseCase {

    private final ProjectRepositoryPort projectRepository;

    public CreateProjectService(ProjectRepositoryPort projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public UUID create(String name, UUID ownerId) {
        Project project = new Project(
                UUID.randomUUID(),
                ownerId,
                name
        );

        projectRepository.save(project);

        return project.getId();
    }
}
