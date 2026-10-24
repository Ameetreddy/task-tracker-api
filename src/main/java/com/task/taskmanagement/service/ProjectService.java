package com.task.taskmanagement.service;

import com.task.taskmanagement.dto.request.ProjectRequest;
import com.task.taskmanagement.dto.response.ProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProjectService {
    ProjectResponse createProject(ProjectRequest projectRequest);

    Page<ProjectResponse> getAllProjects(Pageable pageable);

    ProjectResponse getProjectById(Long id);

    ProjectResponse updateProject(Long id,ProjectRequest request);

    void deleteProject(Long id);
}
