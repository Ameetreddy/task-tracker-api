package com.task.taskmanagement.dto.response;

import com.task.taskmanagement.enums.TaskPriority;
import com.task.taskmanagement.enums.TaskStatus;

import java.time.LocalDate;

public record TaskResponse(
        Long id,
        String title,
        TaskStatus status,
        TaskPriority priority,
        Long projectId,
        String projectName,
        Long assigneeId,
        String assigneeName,
        LocalDate dueDate
) {
}
