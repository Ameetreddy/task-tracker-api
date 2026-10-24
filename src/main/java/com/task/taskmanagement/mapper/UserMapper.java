package com.task.taskmanagement.mapper;

import com.task.taskmanagement.dto.request.UserRequest;
import com.task.taskmanagement.dto.response.UserResponse;
import com.task.taskmanagement.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequest request);

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);
}
