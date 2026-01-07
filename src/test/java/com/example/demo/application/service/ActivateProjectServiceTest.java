package com.example.demo.application.service;

import com.example.demo.domain.exception.InvalidProjectStateException;
import com.example.demo.domain.exception.UnauthorizedOperationException;
import com.example.demo.domain.model.Project;
import com.example.demo.domain.model.ProjectStatus;
import com.example.demo.domain.model.Task;
import com.example.demo.domain.port.out.AuditLogPort;
import com.example.demo.domain.port.out.CurrentUserPort;
import com.example.demo.domain.port.out.NotificationPort;
import com.example.demo.domain.port.out.ProjectRepositoryPort;
import com.example.demo.domain.port.out.TaskRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActivateProjectService Tests")
class ActivateProjectServiceTest {

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private TaskRepositoryPort taskRepository;

    @Mock
    private AuditLogPort auditLogPort;

    @Mock
    private NotificationPort notificationPort;

    @Mock
    private CurrentUserPort currentUserPort;

    @InjectMocks
    private ActivateProjectService activateProjectService;

    private UUID projectId;
    private UUID ownerId;
    private UUID nonOwnerId;
    private Project project;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        nonOwnerId = UUID.randomUUID();
        project = new Project(projectId, ownerId, "Test Project");
    }

    @Test
    @DisplayName("ActivateProject_WithTasks_ShouldSucceed")
    void activateProject_WithTasks_ShouldSucceed() {
        // Given
        Task activeTask = new Task(UUID.randomUUID(), projectId, "Active Task");
        List<Task> tasks = List.of(activeTask);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(currentUserPort.getCurrentUserId()).thenReturn(ownerId);
        when(taskRepository.findByProjectId(projectId)).thenReturn(tasks);

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);

        // When
        activateProjectService.activate(projectId);

        // Then
        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project savedProject = projectCaptor.getValue();
        assertEquals(ProjectStatus.ACTIVE, savedProject.getStatus());

        verify(auditLogPort, times(1)).log(eq("PROJECT_ACTIVATED"), eq(projectId));
        verify(notificationPort, times(1)).notifyUser(eq(ownerId), anyString());
    }

    @Test
    @DisplayName("ActivateProject_WithoutTasks_ShouldFail")
    void activateProject_WithoutTasks_ShouldFail() {
        // Given
        List<Task> emptyTasks = new ArrayList<>();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(currentUserPort.getCurrentUserId()).thenReturn(ownerId);
        when(taskRepository.findByProjectId(projectId)).thenReturn(emptyTasks);

        // When & Then
        InvalidProjectStateException exception = assertThrows(
                InvalidProjectStateException.class,
                () -> activateProjectService.activate(projectId)
        );

        assertTrue(exception.getMessage().contains("at least one active task"));

        verify(projectRepository, never()).save(any());
        verify(auditLogPort, never()).log(anyString(), any());
        verify(notificationPort, never()).notifyUser(any(), anyString());
    }

    @Test
    @DisplayName("ActivateProject_ByNonOwner_ShouldFail")
    void activateProject_ByNonOwner_ShouldFail() {
        // Given
        Task activeTask = new Task(UUID.randomUUID(), projectId, "Active Task");
        List<Task> tasks = List.of(activeTask);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(currentUserPort.getCurrentUserId()).thenReturn(nonOwnerId);
        when(taskRepository.findByProjectId(projectId)).thenReturn(tasks);

        // When & Then
        UnauthorizedOperationException exception = assertThrows(
                UnauthorizedOperationException.class,
                () -> activateProjectService.activate(projectId)
        );

        assertTrue(exception.getMessage().contains("Only the project owner"));

        verify(projectRepository, never()).save(any());
        verify(auditLogPort, never()).log(anyString(), any());
        verify(notificationPort, never()).notifyUser(any(), anyString());
    }
}
