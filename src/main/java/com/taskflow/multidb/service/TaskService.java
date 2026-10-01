package com.taskflow.multidb.service;

import com.taskflow.multidb.entity.Task;
import com.taskflow.multidb.repository.ProjectRepository;
import com.taskflow.multidb.repository.TaskRepository;
import com.taskflow.multidb.repository.UserRepository;
import com.taskflow.multidb.web.dto.TaskDtos.CreateTaskRequest;
import com.taskflow.multidb.web.dto.TaskDtos.TaskResponse;
import com.taskflow.multidb.web.dto.TaskDtos.UpdateTaskRequest;
import com.taskflow.multidb.web.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskResponse create(CreateTaskRequest request) {
        if (!projectRepository.existsById(request.projectId())) {
            throw new IllegalArgumentException("El proyecto indicado no existe: " + request.projectId());
        }
        if (request.assigneeId() != null && !userRepository.existsById(request.assigneeId())) {
            throw new IllegalArgumentException("El assignee indicado no existe: " + request.assigneeId());
        }
        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .projectId(request.projectId())
                .assigneeId(request.assigneeId())
                .dueDate(request.dueDate())
                .build();
        return toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        return taskRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public TaskResponse update(Long id, UpdateTaskRequest request) {
        Task task = getOrThrow(id);
        if (request.assigneeId() != null && !userRepository.existsById(request.assigneeId())) {
            throw new IllegalArgumentException("El assignee indicado no existe: " + request.assigneeId());
        }
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setAssigneeId(request.assigneeId());
        task.setDueDate(request.dueDate());
        return toResponse(task);
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Tarea no encontrada: " + id);
        }
        taskRepository.deleteById(id);
    }

    private Task getOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tarea no encontrada: " + id));
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(),
                task.getProjectId(), task.getAssigneeId(), task.getDueDate(), task.getCreatedAt());
    }
}
