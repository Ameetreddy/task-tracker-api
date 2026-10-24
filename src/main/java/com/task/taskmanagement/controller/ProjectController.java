package com.task.taskmanagement.controller;

import com.task.taskmanagement.dto.request.ProjectRequest;
import com.task.taskmanagement.dto.response.ProjectResponse;
import com.task.taskmanagement.dto.response.TaskResponse;
import com.task.taskmanagement.service.ProjectService;
import com.task.taskmanagement.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;

    @Value("${pagination.default-page-size:10}")
    private int defaultPageSize;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest projectRequest)
    {

        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectRequest));
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {

        Pageable pageable = PageRequest.of(
                page,
                size != null ? size : defaultPageSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(projectService.getAllProjects(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long id) {

        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {

        return ResponseEntity.ok(
                projectService.updateProject(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasksByProjectId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                taskService.getTasksByProjectId(id)
        );
    }
}
