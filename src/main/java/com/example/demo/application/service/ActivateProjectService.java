package com.example.demo.application.service;

import com.example.demo.application.port.in.ActivateProjectUseCase;
import com.example.demo.domain.model.Project;
import com.example.demo.domain.model.Task;
import com.example.demo.domain.port.out.*;

import java.util.List;
import java.util.UUID;

public class ActivateProjectService implements ActivateProjectUseCase {

    private final ProjectRepositoryPort projectRepository;
    private final TaskRepositoryPort taskRepository;
    private final AuditLogPort auditLogPort;
    private final NotificationPort notificationPort;
    private final CurrentUserPort currentUserPort;

    public ActivateProjectService(
            ProjectRepositoryPort projectRepository,
            TaskRepositoryPort taskRepository,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort,
            CurrentUserPort currentUserPort
    ) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.auditLogPort = auditLogPort;
        this.notificationPort = notificationPort;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public void activate(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        UUID currentUserId = currentUserPort.getCurrentUserId();
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        project.activate(currentUserId, tasks);

        projectRepository.save(project);

        auditLogPort.log("PROJECT_ACTIVATED", projectId);

        notificationPort.notifyUser(currentUserId, "Project activated: " + project.getName());
    }
}
