package com.task.taskmanagement.repository;

import com.task.taskmanagement.entity.Task;
import com.task.taskmanagement.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
        SELECT t
        FROM Task t
        JOIN FETCH t.project
        LEFT JOIN FETCH t.assignee
        WHERE t.project.id = :projectId
        """)
    List<Task> findTasksByProjectIdWithDetails(
            @Param("projectId") Long projectId
    );

    @Query(
            value = """
            SELECT t
            FROM Task t
            WHERE (:status IS NULL OR t.status = :status)
              AND (:projectId IS NULL OR t.project.id = :projectId)
            """,
            countQuery = """
            SELECT COUNT(t)
            FROM Task t
            WHERE (:status IS NULL OR t.status = :status)
              AND (:projectId IS NULL OR t.project.id = :projectId)
            """
    )
    Page<Task> findTasksWithFilters(
            @Param("status") TaskStatus status,
            @Param("projectId") Long projectId,
            Pageable pageable
    );

    boolean existsByProjectId(Long id);
    boolean existsByAssigneeId(Long userId);
}