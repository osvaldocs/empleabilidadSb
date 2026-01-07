package com.example.demo.domain.port.out;

import com.example.demo.domain.model.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepositoryPort {

    void save(Project project);

    Optional<Project> findById(UUID projectId);

    List<Project> findByOwnerId(UUID ownerId);
}
