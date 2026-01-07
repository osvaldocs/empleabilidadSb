package com.example.demo.infrastructure.controller.dto;

import java.util.UUID;

public record CreateTaskRequest(String title, UUID projectId) {}
