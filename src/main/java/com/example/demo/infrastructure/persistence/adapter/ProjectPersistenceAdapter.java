package com.example.demo.infrastructure.persistence.adapter;

import com.example.demo.domain.model.Project;
import com.example.demo.domain.model.ProjectStatus;
import com.example.demo.domain.port.out.ProjectRepositoryPort;
import com.example.demo.infrastructure.persistence.entity.ProjectEntity;
import com.example.demo.infrastructure.persistence.repository.ProjectJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProjectPersistenceAdapter implements ProjectRepositoryPort {

    private final ProjectJpaRepository projectJpaRepository;

    public ProjectPersistenceAdapter(ProjectJpaRepository projectJpaRepository) {
        this.projectJpaRepository = projectJpaRepository;
    }

    @Override
    public void save(Project project) {
        projectJpaRepository.save(toEntity(project));
    }

    @Override
    public Optional<Project> findById(UUID projectId) {
        return projectJpaRepository.findById(projectId)
                .map(this::toDomain);
    }

    @Override
    public List<Project> findByOwnerId(UUID ownerId) {
        return projectJpaRepository.findByOwnerIdAndDeletedFalse(ownerId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private ProjectEntity toEntity(Project project) {
        return new ProjectEntity(
                project.getId(),
                project.getOwnerId(),
                project.getName(),
                project.getStatus().name(),
                project.isDeleted()
        );
    }

    private Project toDomain(ProjectEntity entity) {
        Project project = new Project(
                entity.getId(),
                entity.getOwnerId(),
                entity.getName()
        );
        if (entity.getStatus().equals(ProjectStatus.ACTIVE.name())) {
            project.activate(entity.getOwnerId(), List.of()); // tareas se cargarán desde TaskAdapter
        }
        if (entity.isDeleted()) {
            project.markAsDeleted(entity.getOwnerId());
        }
        return project;
    }
}
