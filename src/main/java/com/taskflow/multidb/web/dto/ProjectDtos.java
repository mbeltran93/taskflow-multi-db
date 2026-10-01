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

    /**
     * Resultado de cerrar un proyecto: cuantas tareas se marcaron DONE.
     * En Oracle y SQL Server ese numero sale del OUT param de un stored
     * procedure nativo (close_project); en Postgres/MySQL, de un UPDATE
     * masivo equivalente via JPQL. Ver ProjectCloser y sus implementaciones.
     */
    public record ProjectCloseResult(
            Long projectId,
            int updatedTasks
    ) {}
}
