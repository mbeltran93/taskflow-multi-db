package com.taskflow.multidb.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class ProjectDtos {

    public record CreateProjectRequest(
            @NotBlank @Size(max = 160) String name,
            @Size(max = 2000) String description,
            @NotNull Long ownerId
    ) {}

    public record UpdateProjectRequest(
            @NotBlank @Size(max = 160) String name,
            @Size(max = 2000) String description
    ) {}

    public record ProjectResponse(
            Long id,
            String name,
            String description,
            Long ownerId,
            Instant createdAt
    ) {}
}
