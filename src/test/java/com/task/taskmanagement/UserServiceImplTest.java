package com.task.taskmanagement;

import com.task.taskmanagement.dto.request.UserRequest;
import com.task.taskmanagement.dto.response.UserResponse;
import com.task.taskmanagement.entity.User;
import com.task.taskmanagement.exception.BusinessException;
import com.task.taskmanagement.exception.DuplicateResourceException;
import com.task.taskmanagement.exception.ResourceNotFoundException;
import com.task.taskmanagement.mapper.UserMapper;
import com.task.taskmanagement.repository.TaskRepository;
import com.task.taskmanagement.repository.UserRepository;

import com.task.taskmanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequest request;
    private UserResponse response;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Rahul Sharma");
        user.setEmail("rahul@example.com");
        user.setRole("DEVELOPER");

        request = new UserRequest(
                "Rahul Sharma",
                "rahul@example.com",
                "DEVELOPER"
        );

        response = new UserResponse(
                1L,
                "Rahul Sharma",
                "rahul@example.com",
                "DEVELOPER"
        );
    }

    @Test
    void createUser_whenEmailIsUnique_returnsCreatedUser() {
        when(userRepository.existsByEmail("rahul@example.com"))
                .thenReturn(false);

        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.createUser(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Rahul Sharma", result.name());
        assertEquals("rahul@example.com", result.email());

        verify(userRepository).existsByEmail("rahul@example.com");
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void createUser_whenEmailAlreadyExists_throwsException() {
        when(userRepository.existsByEmail("rahul@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> userService.createUser(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toEntity(any(UserRequest.class));
    }

    @Test
    void getUserById_whenUserExists_returnsUser() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Rahul Sharma", result.name());

        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(user);
    }

    @Test
    void getUserById_whenUserDoesNotExist_throwsException() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(99L)
        );

        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    void updateUser_whenUserExists_updatesUser() {
        UserRequest updateRequest = new UserRequest(
                "Rahul Reddy",
                "rahul.reddy@example.com",
                "MANAGER"
        );

        UserResponse updatedResponse = new UserResponse(
                1L,
                "Rahul Reddy",
                "rahul.reddy@example.com",
                "MANAGER"
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmailAndIdNot(
                "rahul.reddy@example.com", 1L
        )).thenReturn(false);

        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUser(1L, updateRequest);

        assertEquals("Rahul Reddy", user.getName());
        assertEquals("rahul.reddy@example.com", user.getEmail());
        assertEquals("MANAGER", user.getRole());

        assertEquals("Rahul Reddy", result.name());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_whenEmailBelongsToAnotherUser_throwsException() {
        UserRequest updateRequest = new UserRequest(
                "Rahul Sharma",
                "priya@example.com",
                "DEVELOPER"
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmailAndIdNot(
                "priya@example.com", 1L
        )).thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> userService.updateUser(1L, updateRequest)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_whenUserDoesNotExist_throwsException() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUser(99L, request)
        );

        verify(userRepository, never())
                .existsByEmailAndIdNot(anyString(), anyLong());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsers_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toResponse(user)).thenReturn(response);

        Page<UserResponse> result = userService.getAllUsers(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Rahul Sharma", result.getContent().get(0).name());

        verify(userRepository).findAll(pageable);
        verify(userMapper).toResponse(user);
    }

    @Test
    void deleteUser_whenUserHasNoAssignedTasks_deletesUser() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(taskRepository.existsByAssigneeId(1L))
                .thenReturn(false);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_whenUserHasAssignedTasks_throwsException() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(taskRepository.existsByAssigneeId(1L))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> userService.deleteUser(1L)
        );

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void deleteUser_whenUserDoesNotExist_throwsException() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteUser(99L)
        );

        verify(taskRepository, never()).existsByAssigneeId(anyLong());
        verify(userRepository, never()).delete(any(User.class));
    }
}

