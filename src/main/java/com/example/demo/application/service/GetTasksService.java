package com.example.demo.application.service;

import com.example.demo.application.port.in.GetTasksUseCase;
import com.example.demo.domain.model.Project;
import com.example.demo.domain.model.Task;
import com.example.demo.domain.port.out.ProjectRepositoryPort;
import com.example.demo.domain.port.out.TaskRepositoryPort;

import java.util.List;
import java.util.UUID;

public class GetTasksService implements GetTasksUseCase {

    private final TaskRepositoryPort taskRepository;
    private final ProjectRepositoryPort projectRepository;

    public GetTasksService(
            TaskRepositoryPort taskRepository,
            ProjectRepositoryPort projectRepository
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public List<Task> getTasks(UUID projectId, UUID userId) {
        // Validar que el proyecto existe y pertenece al usuario
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }

        return taskRepository.findByProjectId(projectId)
                .stream()
                .filter(task -> !task.isDeleted())
                .toList();
    }
}

