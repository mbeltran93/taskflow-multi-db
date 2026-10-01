package com.taskflow.multidb.web.dto;

import com.taskflow.multidb.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

public class TaskDtos {

    public record CreateTaskRequest(
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String description,
            @NotNull Long projectId,
            Long assigneeId,
            LocalDate dueDate
    ) {}

    public record UpdateTaskRequest(
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String description,
            @NotNull TaskStatus status,
            Long assigneeId,
            LocalDate dueDate
    ) {}

    public record TaskResponse(
            Long id,
            String title,
            String description,
            TaskStatus status,
            Long projectId,
            Long assigneeId,
            LocalDate dueDate,
            Instant createdAt
    ) {}
}
