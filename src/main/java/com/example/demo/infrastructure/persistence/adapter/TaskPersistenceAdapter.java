package com.example.demo.infrastructure.persistence.adapter;

import com.example.demo.domain.model.Task;
import com.example.demo.domain.port.out.TaskRepositoryPort;
import com.example.demo.infrastructure.persistence.entity.TaskEntity;
import com.example.demo.infrastructure.persistence.repository.TaskJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskPersistenceAdapter implements TaskRepositoryPort {

    private final TaskJpaRepository taskJpaRepository;

    public TaskPersistenceAdapter(TaskJpaRepository taskJpaRepository) {
        this.taskJpaRepository = taskJpaRepository;
    }

    @Override
    public void save(Task task) {
        taskJpaRepository.save(toEntity(task));
    }

    @Override
    public Optional<Task> findById(UUID taskId) {
        return taskJpaRepository.findById(taskId)
                .map(this::toDomain);
    }

    @Override
    public List<Task> findByProjectId(UUID projectId) {
        return taskJpaRepository.findByProjectId(projectId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private TaskEntity toEntity(Task task) {
        return new TaskEntity(
                task.getId(),
                task.getProjectId(),
                task.getTitle(),
                task.isCompleted(),
                task.isDeleted()
        );
    }

    private Task toDomain(TaskEntity entity) {
        // usamos el constructor completo para reconstruir desde DB
        return new Task(
                entity.getId(),
                entity.getProjectId(),
                entity.getTitle(),
                entity.isCompleted(),
                entity.isDeleted()
        );
    }
}
