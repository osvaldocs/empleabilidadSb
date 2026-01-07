package com.example.demo.application.service;

import com.example.demo.application.port.in.CreateTaskUseCase;
import com.example.demo.domain.model.Task;
import com.example.demo.domain.port.out.*;

import java.util.UUID;

public class CreateTaskService implements CreateTaskUseCase {

    private final TaskRepositoryPort taskRepository;
    private final ProjectRepositoryPort projectRepository;
    private final CurrentUserPort currentUserPort;

    public CreateTaskService(
            TaskRepositoryPort taskRepository,
            ProjectRepositoryPort projectRepository,
            CurrentUserPort currentUserPort
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public UUID create(String title, UUID projectId, UUID userId) {
        // Validar que el proyecto exista y sea del usuario
        projectRepository.findById(projectId)
                .filter(p -> p.getOwnerId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Unauthorized or project not found"));

        Task task = new Task(UUID.randomUUID(), projectId, title);
        taskRepository.save(task);

        return task.getId();
    }
}
