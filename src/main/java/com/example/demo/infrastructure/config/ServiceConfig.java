package com.example.demo.infrastructure.config;

import com.example.demo.application.port.in.*;
import com.example.demo.application.service.*;
import com.example.demo.domain.port.out.*;
import com.example.demo.infrastructure.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfig {

    private final ProjectRepositoryPort projectRepositoryPort;
    private final TaskRepositoryPort taskRepositoryPort;
    private final AuditLogPort auditLogPort;
    private final NotificationPort notificationPort;
    private final CurrentUserPort currentUserPort;

    public ServiceConfig(ProjectRepositoryPort projectRepositoryPort,
                         TaskRepositoryPort taskRepositoryPort,
                         AuditLogPort auditLogPort,
                         NotificationPort notificationPort,
                         CurrentUserPort currentUserPort) {
        this.projectRepositoryPort = projectRepositoryPort;
        this.taskRepositoryPort = taskRepositoryPort;
        this.auditLogPort = auditLogPort;
        this.notificationPort = notificationPort;
        this.currentUserPort = currentUserPort;
    }

    @Bean
    public AuthUseCase authUseCase(UserRepositoryPort userRepositoryPort,
                                   PasswordEncoder passwordEncoder,
                                   JwtService jwtService) {
        return new AuthService(userRepositoryPort, passwordEncoder, jwtService);
    }

    @Bean
    public CreateProjectUseCase createProjectUseCase() {
        return new CreateProjectService(projectRepositoryPort);
    }

    @Bean
    public ActivateProjectUseCase activateProjectUseCase() {
        return new ActivateProjectService(
                projectRepositoryPort,
                taskRepositoryPort,
                auditLogPort,
                notificationPort,
                currentUserPort
        );
    }

    @Bean
    public GetProjectsUseCase getProjectsUseCase() {
        return new GetProjectsService(projectRepositoryPort);
    }

    @Bean
    public CreateTaskUseCase createTaskUseCase(
            TaskRepositoryPort taskRepositoryPort,
            ProjectRepositoryPort projectRepositoryPort,
            CurrentUserPort currentUserPort
    ) {
        return new CreateTaskService(taskRepositoryPort, projectRepositoryPort, currentUserPort);
    }


    @Bean
    public CompleteTaskUseCase completeTaskUseCase() {
        return new CompleteTaskService(
                taskRepositoryPort,
                projectRepositoryPort,
                auditLogPort,
                notificationPort
        );
    }

    @Bean
    public GetTasksUseCase getTasksUseCase() {
        return new GetTasksService(taskRepositoryPort, projectRepositoryPort);
    }
}
