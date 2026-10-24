package com.task.taskmanagement.mapper;

import com.task.taskmanagement.dto.request.TaskRequest;
import com.task.taskmanagement.dto.response.TaskResponse;
import com.task.taskmanagement.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    // Convert request to a new entity
    Task toEntity(TaskRequest request);

    // Update an existing entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    void updateEntity(TaskRequest request, @MappingTarget Task task);

    // Convert entity to response
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "assigneeName", source = "assignee.name")
    TaskResponse toResponse(Task task);

    // Convert a list of entities to responses
    List<TaskResponse> toResponseList(List<Task> tasks);
}
