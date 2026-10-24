package com.task.taskmanagement.service;

import com.task.taskmanagement.dto.request.UserRequest;
import com.task.taskmanagement.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    void deleteUser(Long id);

    UserResponse updateUser(Long id,UserRequest request);

    UserResponse getUserById(Long id);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse createUser(UserRequest request);
}
