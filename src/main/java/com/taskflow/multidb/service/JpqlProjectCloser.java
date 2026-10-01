package com.taskflow.multidb.service;

import com.taskflow.multidb.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion de ProjectCloser para los vendors sin stored procedure
 * propia en este repo (Postgres y MySQL): un UPDATE masivo via JPQL que
 * produce el mismo resultado que close_project en Oracle/SQL Server.
 */
@Service
@Profile({"postgres", "mysql"})
@RequiredArgsConstructor
public class JpqlProjectCloser implements ProjectCloser {

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public int closeProject(Long projectId) {
        return taskRepository.closeAllTasksOfProject(projectId);
    }
}
