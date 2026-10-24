package com.task.taskmanagement.mapper;

import com.task.taskmanagement.dto.request.ProjectRequest;
import com.task.taskmanagement.dto.response.ProjectResponse;
import com.task.taskmanagement.entity.Project;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project toEntity(ProjectRequest request);

    ProjectResponse toResponse(Project project);

    List<ProjectResponse> toResponseList(List<Project> projects);

}
