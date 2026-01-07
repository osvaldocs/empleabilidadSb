package com.example.demo.infrastructure.controller;

import com.example.demo.application.port.in.CreateProjectUseCase;
import com.example.demo.application.port.in.ActivateProjectUseCase;
import com.example.demo.application.port.in.GetProjectsUseCase;
import com.example.demo.domain.port.out.CurrentUserPort;
import com.example.demo.infrastructure.controller.dto.CreateProjectRequest;
import com.example.demo.infrastructure.controller.dto.ProjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "API para gestión de proyectos")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final CreateProjectUseCase createProjectUseCase;
    private final ActivateProjectUseCase activateProjectUseCase;
    private final GetProjectsUseCase getProjectsUseCase;
    private final CurrentUserPort currentUserPort;

    public ProjectController(
            CreateProjectUseCase createProjectUseCase,
            ActivateProjectUseCase activateProjectUseCase,
            GetProjectsUseCase getProjectsUseCase,
            CurrentUserPort currentUserPort
    ) {
        this.createProjectUseCase = createProjectUseCase;
        this.activateProjectUseCase = activateProjectUseCase;
        this.getProjectsUseCase = getProjectsUseCase;
        this.currentUserPort = currentUserPort;
    }

    @GetMapping
    @Operation(summary = "Listar proyectos", description = "Obtiene todos los proyectos del usuario autenticado")
    public ResponseEntity<List<ProjectResponse>> getProjects() {
        UUID userId = currentUserPort.getCurrentUserId();
        List<ProjectResponse> projects = getProjectsUseCase.getProjects(userId)
                .stream()
                .map(ProjectResponse::new)
                .toList();
        return ResponseEntity.ok(projects);
    }

    @PostMapping
    @Operation(summary = "Crear proyecto", description = "Crea un nuevo proyecto para el usuario autenticado")
    public ResponseEntity<UUID> createProject(@RequestBody CreateProjectRequest request) {
        UUID userId = currentUserPort.getCurrentUserId();
        UUID projectId = createProjectUseCase.create(request.name(), userId);
        return ResponseEntity.ok(projectId);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activar proyecto", description = "Activa un proyecto (requiere al menos una tarea activa)")
    public ResponseEntity<Void> activateProject(@PathVariable UUID id) {
        UUID userId = currentUserPort.getCurrentUserId();
        activateProjectUseCase.activate(id);
        return ResponseEntity.ok().build();
    }


}
