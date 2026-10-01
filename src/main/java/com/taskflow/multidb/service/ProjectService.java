package com.taskflow.multidb.service;

import com.taskflow.multidb.entity.Project;
import com.taskflow.multidb.repository.ProjectRepository;
import com.taskflow.multidb.repository.UserRepository;
import com.taskflow.multidb.web.dto.ProjectDtos.CreateProjectRequest;
import com.taskflow.multidb.web.dto.ProjectDtos.ProjectResponse;
import com.taskflow.multidb.web.dto.ProjectDtos.UpdateProjectRequest;
import com.taskflow.multidb.web.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponse create(CreateProjectRequest request) {
        if (!userRepository.existsById(request.ownerId())) {
            throw new IllegalArgumentException("El owner indicado no existe: " + request.ownerId());
        }
        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .ownerId(request.ownerId())
                .build();
        return toResponse(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public ProjectResponse update(Long id, UpdateProjectRequest request) {
        Project project = getOrThrow(id);
        project.setName(request.name());
        project.setDescription(request.description());
        return toResponse(project);
    }

    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new NotFoundException("Proyecto no encontrado: " + id);
        }
        projectRepository.deleteById(id);
    }

    private Project getOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proyecto no encontrado: " + id));
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(),
                project.getOwnerId(), project.getCreatedAt());
    }
}
