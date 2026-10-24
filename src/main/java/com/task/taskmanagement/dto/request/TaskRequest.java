package com.task.taskmanagement.dto.request;

import com.task.taskmanagement.enums.TaskPriority;
import com.task.taskmanagement.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "Task title is required")
        @Size(max = 200, message = "Task title must not exceed 200 characters")
        String title,

        @NotNull(message = "Task status is required")
        TaskStatus status,

        TaskPriority priority,

        @NotNull(message = "Project ID is required")
        Long projectId,

        Long assigneeId,

        LocalDate dueDate
) {
}
