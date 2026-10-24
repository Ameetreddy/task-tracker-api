package com.task.taskmanagement.controller;

import com.task.taskmanagement.dto.request.TaskRequest;
import com.task.taskmanagement.dto.response.TaskResponse;
import com.task.taskmanagement.enums.TaskStatus;
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


@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    @Value("${pagination.default-page-size}")
    private int defaultPageSize;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.createTask(request));
    }


    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {

        Pageable pageable = PageRequest.of(
                page,
                size != null ? size : defaultPageSize,
                Sort.by(Sort.Direction.ASC, "title")
        );

        return ResponseEntity.ok(
                taskService.getTasks(status, projectId, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request
    ) {
        return ResponseEntity.ok(
                taskService.updateTask(id, request)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }


}
