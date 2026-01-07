package com.example.demo.application.service;

import com.example.demo.application.port.in.CompleteTaskUseCase;
import com.example.demo.domain.model.Project;
import com.example.demo.domain.model.Task;
import com.example.demo.domain.port.out.AuditLogPort;
import com.example.demo.domain.port.out.NotificationPort;
import com.example.demo.domain.port.out.ProjectRepositoryPort;
import com.example.demo.domain.port.out.TaskRepositoryPort;

import java.util.UUID;

public class CompleteTaskService implements CompleteTaskUseCase {

    private final TaskRepositoryPort taskRepository;
    private final ProjectRepositoryPort projectRepository;
    private final AuditLogPort auditLogPort;
    private final NotificationPort notificationPort;

    public CompleteTaskService(
            TaskRepositoryPort taskRepository,
            ProjectRepositoryPort projectRepository,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.auditLogPort = auditLogPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void complete(UUID taskId, UUID projectId, UUID userId) {
        // Validar que el proyecto existe y pertenece al usuario
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }

        // Completar la task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getProjectId().equals(projectId)) {
            throw new RuntimeException("Task does not belong to this project");
        }

        task.complete(userId); // lógica de dominio

        taskRepository.save(task);

        auditLogPort.log("TASK_COMPLETED", taskId);

        notificationPort.notifyUser(userId, "Task completed: " + task.getTitle());
    }
}
