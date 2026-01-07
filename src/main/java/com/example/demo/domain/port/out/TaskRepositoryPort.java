package com.example.demo.domain.port.out;

import com.example.demo.domain.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepositoryPort {

    void save(Task task);

    Optional<Task> findById(UUID taskId);

    List<Task> findByProjectId(UUID projectId);
}
