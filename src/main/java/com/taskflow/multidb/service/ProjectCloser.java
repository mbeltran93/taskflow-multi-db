package com.taskflow.multidb.service;

/**
 * Cierra un proyecto: marca todas sus tareas que no estan DONE como DONE y
 * devuelve cuantas se actualizaron.
 *
 * Hay una implementacion por vendor porque el mecanismo real es distinto:
 * Oracle y SQL Server lo hacen llamando a un stored procedure nativo
 * (close_project, agregado via Liquibase en db/changelog/<vendor>/), mientras
 * que Postgres y MySQL usan un UPDATE masivo equivalente via JPQL (no se
 * agrego una stored procedure propia para esos dos motores porque no era
 * parte del alcance pedido, pero el resultado observable desde la API es
 * idéntico en los 4 motores).
 */
public interface ProjectCloser {
    int closeProject(Long projectId);
}
