package com.task.taskmanagement.service.impl;

import com.task.taskmanagement.dto.request.UserRequest;
import com.task.taskmanagement.dto.response.UserResponse;
import com.task.taskmanagement.entity.User;
import com.task.taskmanagement.exception.BusinessException;
import com.task.taskmanagement.exception.DuplicateResourceException;
import com.task.taskmanagement.exception.ResourceNotFoundException;
import com.task.taskmanagement.mapper.UserMapper;
import com.task.taskmanagement.repository.TaskRepository;
import com.task.taskmanagement.repository.UserRepository;
import com.task.taskmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TaskRepository taskRepository;

    @Override
    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "A user with this email already exists"
            );
        }

        User user = userMapper.toEntity(request);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        ));

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(
            Long id,
            UserRequest request
    ) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        ));
        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException(
                    "A user with this email already exists"
            );
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }



    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        ));
        if (taskRepository.existsByAssigneeId(id)) {
            throw new BusinessException(
                    "Cannot delete user because they are assigned to tasks"
            );
        }

        userRepository.delete(user);
    }
}
