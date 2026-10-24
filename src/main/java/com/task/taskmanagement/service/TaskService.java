package com.task.taskmanagement.service;

import com.task.taskmanagement.dto.request.TaskRequest;
import com.task.taskmanagement.dto.response.TaskResponse;
import com.task.taskmanagement.enums.TaskStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(@Valid TaskRequest request);
    Page<TaskResponse> getTasks(
            TaskStatus status,
            Long projectId,
            Pageable pageable
    );
    TaskResponse getTaskById(Long id);
    TaskResponse updateTask(Long id,TaskRequest request);
    void deleteTask(Long id);

    List<TaskResponse> getTasksByProjectId(Long projectId);
}
