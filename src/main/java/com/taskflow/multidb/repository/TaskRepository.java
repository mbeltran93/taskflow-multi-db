package com.taskflow.multidb.repository;

import com.taskflow.multidb.entity.Task;
import com.taskflow.multidb.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findByAssigneeId(Long assigneeId);

    // Filtro compuesto projectId + status: el que se beneficia del indice
    // compuesto agregado en la seccion de tuning de performance del README.
    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    /**
     * Fallback generico (sin stored procedure nativa) para vendors sin un
     * procedimiento equivalente a close_project: hace el mismo UPDATE masivo
     * que las versiones de Oracle/SQL Server, pero via JPQL/Hibernate.
     * Usado por los perfiles postgres y mysql.
     */
    @Modifying
    @Query("UPDATE Task t SET t.status = com.taskflow.multidb.entity.TaskStatus.DONE " +
            "WHERE t.projectId = :projectId AND t.status <> com.taskflow.multidb.entity.TaskStatus.DONE")
    int closeAllTasksOfProject(@Param("projectId") Long projectId);
}
