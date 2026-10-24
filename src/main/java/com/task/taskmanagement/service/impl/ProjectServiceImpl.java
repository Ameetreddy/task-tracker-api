package com.task.taskmanagement.service.impl;

import com.task.taskmanagement.dto.request.ProjectRequest;
import com.task.taskmanagement.dto.response.ProjectResponse;
import com.task.taskmanagement.entity.Project;
import com.task.taskmanagement.exception.BusinessException;
import com.task.taskmanagement.exception.DuplicateResourceException;
import com.task.taskmanagement.exception.ResourceNotFoundException;
import com.task.taskmanagement.mapper.ProjectMapper;
import com.task.taskmanagement.repository.ProjectRepository;
import com.task.taskmanagement.repository.TaskRepository;
import com.task.taskmanagement.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final TaskRepository taskRepository;

    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest) {
        String name=projectRequest.name().trim();

        if (projectRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException(
                    "A project with this name already exists"
            );
        }

        Project project = projectMapper.toEntity(projectRequest);

        Project savedProject = projectRepository.save(project);

        return projectMapper.toResponse(savedProject);

    }

    @Override
    public Page<ProjectResponse> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(projectMapper::toResponse);
    }

    @Override
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        )
                );

        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long id, ProjectRequest request) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        ));

        String updatedName = request.name().trim();

        boolean duplicateName =
                projectRepository.existsByNameIgnoreCase(updatedName)
                        && !project.getName().equalsIgnoreCase(updatedName);

        if (duplicateName) {
            throw new DuplicateResourceException(
                    "A project with this name already exists"
            );
        }

        project.setName(updatedName);
        project.setDescription(request.description());

        Project savedProject = projectRepository.save(project);

        return projectMapper.toResponse(savedProject);
    }

    @Override
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        ));

        if (taskRepository.existsByProjectId(id)) {
            throw new BusinessException(
                    "Cannot delete project because it contains tasks"
            );
        }

        projectRepository.delete(project);
    }
}
