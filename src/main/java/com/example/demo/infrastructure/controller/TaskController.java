package com.example.demo.infrastructure.controller;

import com.example.demo.application.port.in.CreateTaskUseCase;
import com.example.demo.application.port.in.CompleteTaskUseCase;
import com.example.demo.application.port.in.GetTasksUseCase;
import com.example.demo.infrastructure.controller.dto.CreateTaskRequest;
import com.example.demo.infrastructure.controller.dto.TaskResponse;
import com.example.demo.domain.port.out.CurrentUserPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@Tag(name = "Tasks", description = "API para gestión de tareas")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final CompleteTaskUseCase completeTaskUseCase;
    private final GetTasksUseCase getTasksUseCase;
    private final CurrentUserPort currentUserPort;

    public TaskController(
            CreateTaskUseCase createTaskUseCase,
            CompleteTaskUseCase completeTaskUseCase,
            GetTasksUseCase getTasksUseCase,
            CurrentUserPort currentUserPort
    ) {
        this.createTaskUseCase = createTaskUseCase;
        this.completeTaskUseCase = completeTaskUseCase;
        this.getTasksUseCase = getTasksUseCase;
        this.currentUserPort = currentUserPort;
    }

    @GetMapping
    @Operation(summary = "Listar tareas", description = "Obtiene todas las tareas de un proyecto")
    public ResponseEntity<List<TaskResponse>> getTasks(@PathVariable UUID projectId) {
        UUID userId = currentUserPort.getCurrentUserId();
        List<TaskResponse> tasks = getTasksUseCase.getTasks(projectId, userId)
                .stream()
                .map(task -> new TaskResponse(task.getId(), task.getTitle(), task.isCompleted()))
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    @Operation(summary = "Crear tarea", description = "Crea una nueva tarea en un proyecto")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @RequestBody CreateTaskRequest request
    ) {
        UUID userId = currentUserPort.getCurrentUserId();
        // create devuelve el UUID de la task creada
        UUID taskId = createTaskUseCase.create(request.title(), projectId, userId);
        return ResponseEntity.ok(new TaskResponse(taskId, request.title(), false));
    }

    @PatchMapping("/{taskId}/complete")
    @Operation(summary = "Completar tarea", description = "Marca una tarea como completada")
    public ResponseEntity<TaskResponse> completeTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId
    ) {
        UUID userId = currentUserPort.getCurrentUserId();
        completeTaskUseCase.complete(taskId, projectId, userId);
        return ResponseEntity.ok().build(); // opcional: podrías devolver TaskResponse actualizada
    }
}
