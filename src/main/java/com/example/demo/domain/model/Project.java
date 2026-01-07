package com.example.demo.domain.model;



import com.example.demo.domain.exception.InvalidProjectStateException;
import com.example.demo.domain.exception.UnauthorizedOperationException;

import java.util.List;
import java.util.UUID;

public class Project {

    private final UUID id;
    private final UUID ownerId;
    private String name;
    private ProjectStatus status;
    private boolean deleted;

    public Project(UUID id, UUID ownerId, String name) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.status = ProjectStatus.DRAFT;
        this.deleted = false;
    }

    public void activate(UUID currentUserId, List<Task> tasks) {
        validateOwner(currentUserId);

        boolean hasActiveTasks = tasks.stream()
                .anyMatch(task -> !task.isDeleted() && !task.isCompleted());

        if (!hasActiveTasks) {
            throw new InvalidProjectStateException(
                    "Project must have at least one active task to be activated"
            );
        }

        this.status = ProjectStatus.ACTIVE;
    }

    private void validateOwner(UUID userId) {
        if (!this.ownerId.equals(userId)) {
            throw new UnauthorizedOperationException(
                    "Only the project owner can modify the project"
            );
        }
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public ProjectStatus getStatus() { return status; }
    public boolean isDeleted() { return deleted; }

    public void markAsDeleted(UUID currentUserId) {
        validateOwner(currentUserId);
        this.deleted = true;
    }
}
