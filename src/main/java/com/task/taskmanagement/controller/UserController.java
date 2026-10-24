package com.task.taskmanagement.controller;

import com.task.taskmanagement.dto.request.UserRequest;
import com.task.taskmanagement.dto.response.UserResponse;
import com.task.taskmanagement.service.UserService;
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
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Value("${pagination.default-page-size}")
    private int defaultPageSize;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {

        Pageable pageable = PageRequest.of(
                page,
                size != null ? size : defaultPageSize,
                Sort.by(Sort.Direction.ASC, "name")
        );

        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
