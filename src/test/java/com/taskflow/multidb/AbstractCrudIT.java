package com.taskflow.multidb;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Suite de tests de integracion compartida: cada subclase arranca (via Testcontainers)
 * una base real de un vendor distinto y registra sus propiedades de datasource/liquibase
 * con @DynamicPropertySource. El mismo codigo de produccion (mismas entidades JPA) corre
 * sin cambios contra cualquiera de los 4 motores.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public abstract class AbstractCrudIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    void crudCompletoDeUsuarioProyectoYTarea() throws Exception {
        // Crear usuario
        Map<String, Object> userReq = Map.of(
                "name", "Ada Lovelace",
                "email", "ada@taskflow.dev",
                "password", "s3cret123"
        );
        String userJson = mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ada@taskflow.dev"))
                .andReturn().getResponse().getContentAsString();
        Long userId = objectMapper.readTree(userJson).get("id").asLong();

        // Crear proyecto para ese usuario
        Map<String, Object> projectReq = Map.of(
                "name", "Portafolio Multi-DB",
                "description", "Demo de portabilidad JPA",
                "ownerId", userId
        );
        String projectJson = mockMvc.perform(post("/api/projects")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(projectReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId").value(userId))
                .andReturn().getResponse().getContentAsString();
        Long projectId = objectMapper.readTree(projectJson).get("id").asLong();

        // Crear tarea dentro del proyecto
        Map<String, Object> taskReq = Map.of(
                "title", "Escribir README",
                "projectId", projectId,
                "assigneeId", userId
        );
        String taskJson = mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(taskReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("TODO"))
                .andReturn().getResponse().getContentAsString();
        Long taskId = objectMapper.readTree(taskJson).get("id").asLong();

        // Listar tareas del proyecto
        mockMvc.perform(get("/api/projects/{id}/tasks", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskId));

        // Actualizar el estado de la tarea
        Map<String, Object> updateReq = Map.of(
                "title", "Escribir README",
                "status", "IN_PROGRESS",
                "assigneeId", userId
        );
        mockMvc.perform(put("/api/tasks/{id}", taskId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // Borrar la tarea
        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void devuelve404SiElProyectoNoExiste() throws Exception {
        mockMvc.perform(get("/api/projects/{id}", 999_999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void devuelve400SiElEmailEsInvalido() throws Exception {
        Map<String, Object> invalidUser = Map.of(
                "name", "Sin Email Valido",
                "email", "no-es-un-email",
                "password", "s3cret123"
        );
        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }
}
