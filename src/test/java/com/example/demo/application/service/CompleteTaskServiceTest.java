package com.example.demo.application.service;

import com.example.demo.domain.exception.TaskAlreadyCompletedException;
import com.example.demo.domain.model.Project;
import com.example.demo.domain.model.Task;
import com.example.demo.domain.port.out.AuditLogPort;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompleteTaskService Tests")
class CompleteTaskServiceTest {

    @Mock
    private TaskRepositoryPort taskRepository;

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private AuditLogPort auditLogPort;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private CompleteTaskService completeTaskService;

    private UUID taskId;
    private UUID projectId;
    private UUID userId;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        userId = UUID.randomUUID();
        project = new Project(projectId, userId, "Test Project");
        task = new Task(taskId, projectId, "Test Task");
    }

    @Test
    @DisplayName("CompleteTask_AlreadyCompleted_ShouldFail")
    void completeTask_AlreadyCompleted_ShouldFail() {
        // Given
        Task completedTask = new Task(taskId, projectId, "Completed Task", true, false);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(completedTask));

        // When & Then
        TaskAlreadyCompletedException exception = assertThrows(
                TaskAlreadyCompletedException.class,
                () -> completeTaskService.complete(taskId, projectId, userId)
        );

        assertTrue(exception.getMessage().contains("already completed"));

        verify(taskRepository, never()).save(any());
        verify(auditLogPort, never()).log(anyString(), any());
        verify(notificationPort, never()).notifyUser(any(), anyString());
    }

    @Test
    @DisplayName("CompleteTask_ShouldGenerateAuditAndNotification")
    void completeTask_ShouldGenerateAuditAndNotification() {
        // Given
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);

        // When
        completeTaskService.complete(taskId, projectId, userId);

        // Then
        verify(taskRepository, times(1)).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();
        assertTrue(savedTask.isCompleted());

        verify(auditLogPort, times(1)).log(eq("TASK_COMPLETED"), eq(taskId));
        verify(notificationPort, times(1)).notifyUser(eq(userId), contains("Task completed"));
    }
}
