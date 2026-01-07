package com.example.demo.domain.model;

import com.example.demo.domain.exception.TaskAlreadyCompletedException;

import java.util.UUID;

public class Task {

    private final UUID id;
    private final UUID projectId;
    private String title;
    private boolean completed;
    private boolean deleted;

    // Constructor para crear nueva task desde casos de uso
    public Task(UUID id, UUID projectId, String title) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.completed = false;
        this.deleted = false;
    }

    // Constructor para reconstruir desde DB (adapter)
    public Task(UUID id, UUID projectId, String title, boolean completed, boolean deleted) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.completed = completed;
        this.deleted = deleted;
    }

    // Métodos de negocio: requieren currentUserId en casos de uso reales
    public void complete(UUID currentUserId) {
        if (this.completed) {
            throw new TaskAlreadyCompletedException("Task is already completed");
        }
        this.completed = true;
    }

    public void markAsDeleted(UUID currentUserId) {
        if (this.completed) {
            throw new TaskAlreadyCompletedException("Completed task cannot be deleted");
        }
        this.deleted = true;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
