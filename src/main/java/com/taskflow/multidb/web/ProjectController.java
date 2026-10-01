package com.taskflow.multidb.web;

import com.taskflow.multidb.service.ProjectService;
import com.taskflow.multidb.service.TaskService;
import com.taskflow.multidb.web.dto.ProjectDtos.CreateProjectRequest;
import com.taskflow.multidb.web.dto.ProjectDtos.ProjectResponse;
import com.taskflow.multidb.web.dto.ProjectDtos.UpdateProjectRequest;
import com.taskflow.multidb.web.dto.TaskDtos.TaskResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse created = projectService.create(request);
        return ResponseEntity.created(URI.create("/api/projects/" + created.id())).body(created);
    }

    @GetMapping
    public List<ProjectResponse> findAll() {
        return projectService.findAll();
    }

    @GetMapping("/{id}")
    public ProjectResponse findById(@PathVariable Long id) {
        return projectService.findById(id);
    }

    @GetMapping("/{id}/tasks")
    public List<TaskResponse> findTasks(@PathVariable Long id) {
        return taskService.findByProjectId(id);
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProjectRequest request) {
        return projectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        projectService.delete(id);
    }
}
