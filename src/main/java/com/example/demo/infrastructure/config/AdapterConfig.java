package com.example.demo.infrastructure.config;

import com.example.demo.domain.port.out.*;
import com.example.demo.infrastructure.audit.AuditAdapter;
import com.example.demo.infrastructure.notification.NotificationAdapter;
import com.example.demo.infrastructure.persistence.adapter.ProjectPersistenceAdapter;
import com.example.demo.infrastructure.persistence.adapter.TaskPersistenceAdapter;
import com.example.demo.infrastructure.persistence.repository.ProjectJpaRepository;
import com.example.demo.infrastructure.persistence.repository.TaskJpaRepository;
import com.example.demo.infrastructure.security.CurrentUserAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdapterConfig {

    @Bean
    public ProjectRepositoryPort projectRepositoryPort(ProjectJpaRepository repo) {
        return new ProjectPersistenceAdapter(repo);
    }

    @Bean
    public TaskRepositoryPort taskRepositoryPort(TaskJpaRepository repo) {
        return new TaskPersistenceAdapter(repo);
    }

    @Bean
    public CurrentUserPort currentUserPort() {
        return new CurrentUserAdapter();
    }

    @Bean
    public AuditLogPort auditLogPort() {
        return new AuditAdapter();
    }

    @Bean
    public NotificationPort notificationPort() {
        return new NotificationAdapter();
    }
}
