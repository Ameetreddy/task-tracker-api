package com.task.taskmanagement;

import com.task.taskmanagement.dto.request.ProjectRequest;
import com.task.taskmanagement.dto.response.ProjectResponse;
import com.task.taskmanagement.entity.Project;
import com.task.taskmanagement.exception.BusinessException;
import com.task.taskmanagement.exception.DuplicateResourceException;
import com.task.taskmanagement.exception.ResourceNotFoundException;
import com.task.taskmanagement.mapper.ProjectMapper;
import com.task.taskmanagement.repository.ProjectRepository;
import com.task.taskmanagement.repository.TaskRepository;

import com.task.taskmanagement.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private ProjectRequest request;
    private ProjectResponse response;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("E-Commerce Platform");
        project.setDescription("E-commerce application");

        request = new ProjectRequest(
                "E-Commerce Platform",
                "E-commerce application"
        );

        response = new ProjectResponse(
                1L,
                "E-Commerce Platform",
                "E-commerce application",
                LocalDateTime.now()
        );
    }

    @Test
    void createProject_whenNameIsUnique_returnsCreatedProject() {
        when(projectRepository.existsByNameIgnoreCase(
                "E-Commerce Platform"
        )).thenReturn(false);

        when(projectMapper.toEntity(request)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(response);

        ProjectResponse result = projectService.createProject(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("E-Commerce Platform", result.name());

        verify(projectRepository)
                .existsByNameIgnoreCase("E-Commerce Platform");
        verify(projectRepository).save(project);
        verify(projectMapper).toResponse(project);
    }

    @Test
    void createProject_whenNameAlreadyExists_throwsException() {
        when(projectRepository.existsByNameIgnoreCase(
                "E-Commerce Platform"
        )).thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> projectService.createProject(request)
        );

        verify(projectRepository, never()).save(any(Project.class));
        verify(projectMapper, never()).toEntity(any(ProjectRequest.class));
    }

    @Test
    void getProjectById_whenProjectExists_returnsProject() {
        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(projectMapper.toResponse(project)).thenReturn(response);

        ProjectResponse result = projectService.getProjectById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("E-Commerce Platform", result.name());

        verify(projectRepository).findById(1L);
        verify(projectMapper).toResponse(project);
    }

    @Test
    void getProjectById_whenProjectDoesNotExist_throwsException() {
        when(projectRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> projectService.getProjectById(99L)
        );

        verify(projectMapper, never()).toResponse(any(Project.class));
    }

    @Test
    void updateProject_whenProjectExists_updatesProject() {
        ProjectRequest updateRequest = new ProjectRequest(
                "Updated Platform",
                "Updated description"
        );

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(projectRepository.existsByNameIgnoreCase("Updated Platform"))
                .thenReturn(false);

        when(projectRepository.save(project)).thenReturn(project);

        ProjectResponse updatedResponse = new ProjectResponse(
                1L,
                "Updated Platform",
                "Updated description",
                LocalDateTime.now()
        );

        when(projectMapper.toResponse(project))
                .thenReturn(updatedResponse);

        ProjectResponse result =
                projectService.updateProject(1L, updateRequest);

        assertEquals("Updated Platform", project.getName());
        assertEquals("Updated description", project.getDescription());
        assertEquals("Updated Platform", result.name());

        verify(projectRepository).save(project);
    }

    @Test
    void deleteProject_whenProjectHasNoTasks_deletesProject() {
        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(taskRepository.existsByProjectId(1L))
                .thenReturn(false);

        assertDoesNotThrow(() -> projectService.deleteProject(1L));

        verify(projectRepository).delete(project);
    }

    @Test
    void deleteProject_whenProjectHasTasks_throwsException() {
        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(taskRepository.existsByProjectId(1L))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> projectService.deleteProject(1L)
        );

        verify(projectRepository, never()).delete(any(Project.class));
    }
}
