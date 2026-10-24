package com.task.taskmanagement;

import com.task.taskmanagement.dto.request.TaskRequest;
import com.task.taskmanagement.dto.response.TaskResponse;
import com.task.taskmanagement.entity.Project;
import com.task.taskmanagement.entity.Task;
import com.task.taskmanagement.entity.User;
import com.task.taskmanagement.enums.TaskPriority;
import com.task.taskmanagement.enums.TaskStatus;
import com.task.taskmanagement.exception.ResourceNotFoundException;
import com.task.taskmanagement.mapper.TaskMapper;
import com.task.taskmanagement.repository.ProjectRepository;
import com.task.taskmanagement.repository.TaskRepository;
import com.task.taskmanagement.repository.UserRepository;

import com.task.taskmanagement.service.impl.TaskServiceImpl;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Project project;
    private User user;
    private Task task;
    private TaskRequest request;
    private TaskResponse response;

    @BeforeEach
    void setUp() {
        // Inject the value normally supplied by application.yml.
        ReflectionTestUtils.setField(
                taskService, "defaultPriority", TaskPriority.MEDIUM
        );

        project = new Project();
        project.setId(1L);
        project.setName("E-Commerce Platform");

        user = new User();
        user.setId(1L);
        user.setName("Rahul Sharma");
        user.setEmail("rahul@example.com");
        user.setRole("DEVELOPER");

        task = new Task();
        task.setId(1L);
        task.setTitle("Implement user authentication");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(TaskPriority.HIGH);
        task.setProject(project);
        task.setAssignee(user);
        task.setDueDate(LocalDate.of(2026, 11, 15));

        request = new TaskRequest(
                "Implement user authentication",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                1L,
                1L,
                LocalDate.of(2026, 11, 15)
        );

        response = new TaskResponse(
                1L,
                "Implement user authentication",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                1L,
                "E-Commerce Platform",
                1L,
                "Rahul Sharma",
                LocalDate.of(2026, 11, 15)
        );
    }

    @Test
    void createTask_whenProjectAndAssigneeExist_returnsCreatedTask() {
        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(taskMapper.toEntity(request)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.createTask(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Implement user authentication", result.title());
        assertEquals(TaskPriority.HIGH, result.priority());

        verify(taskRepository).save(task);
        verify(taskMapper).toResponse(task);
    }

    @Test
    void createTask_whenProjectDoesNotExist_throwsException() {
        when(projectRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.createTask(request)
        );

        verifyNoInteractions(taskMapper);
        verify(taskRepository, never()).save(any(Task.class));
        verifyNoInteractions(userRepository);
    }

    @Test
    void createTask_whenAssigneeDoesNotExist_throwsException() {
        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.createTask(request)
        );

        verifyNoInteractions(taskMapper);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void createTask_whenAssigneeIsNull_createsUnassignedTask() {
        TaskRequest unassignedRequest = new TaskRequest(
                "Prepare API documentation",
                TaskStatus.TODO,
                TaskPriority.LOW,
                1L,
                null,
                LocalDate.of(2026, 11, 25)
        );

        Task unassignedTask = new Task();
        unassignedTask.setId(2L);
        unassignedTask.setTitle("Prepare API documentation");
        unassignedTask.setStatus(TaskStatus.TODO);
        unassignedTask.setPriority(TaskPriority.LOW);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(taskMapper.toEntity(unassignedRequest))
                .thenReturn(unassignedTask);
        when(taskRepository.save(unassignedTask))
                .thenReturn(unassignedTask);
        when(taskMapper.toResponse(unassignedTask))
                .thenReturn(new TaskResponse(
                        2L,
                        "Prepare API documentation",
                        TaskStatus.TODO,
                        TaskPriority.LOW,
                        1L,
                        "E-Commerce Platform",
                        null,
                        null,
                        LocalDate.of(2026, 11, 25)
                ));

        TaskResponse result = taskService.createTask(unassignedRequest);

        assertNull(result.assigneeId());
        assertNull(result.assigneeName());
        assertEquals(project, unassignedTask.getProject());
        assertNull(unassignedTask.getAssignee());

        verifyNoInteractions(userRepository);
        verify(taskRepository).save(unassignedTask);
    }

    @Test
    void createTask_whenPriorityIsNull_appliesDefaultPriority() {
        TaskRequest requestWithoutPriority = new TaskRequest(
                "Prepare API documentation",
                TaskStatus.TODO,
                null,
                1L,
                null,
                null
        );

        Task taskWithoutPriority = new Task();
        taskWithoutPriority.setTitle("Prepare API documentation");
        taskWithoutPriority.setStatus(TaskStatus.TODO);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(taskMapper.toEntity(requestWithoutPriority))
                .thenReturn(taskWithoutPriority);
        when(taskRepository.save(taskWithoutPriority))
                .thenReturn(taskWithoutPriority);
        when(taskMapper.toResponse(taskWithoutPriority))
                .thenReturn(new TaskResponse(
                        2L,
                        "Prepare API documentation",
                        TaskStatus.TODO,
                        TaskPriority.MEDIUM,
                        1L,
                        "E-Commerce Platform",
                        null,
                        null,
                        null
                ));

        TaskResponse result =
                taskService.createTask(requestWithoutPriority);

        assertEquals(TaskPriority.MEDIUM, taskWithoutPriority.getPriority());
        assertEquals(TaskPriority.MEDIUM, result.priority());
        verify(taskRepository).save(taskWithoutPriority);
    }

    @Test
    void getTaskById_whenTaskExists_returnsTask() {
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.getTaskById(1L);

        assertEquals(1L, result.id());
        assertEquals("Implement user authentication", result.title());

        verify(taskMapper).toResponse(task);
    }

    @Test
    void getTaskById_whenTaskDoesNotExist_throwsException() {
        when(taskRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.getTaskById(99L)
        );

        verifyNoInteractions(taskMapper);
    }

    @Test
    void getTasks_returnsFilteredPageOfTasks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(task));

        when(taskRepository.findTasksWithFilters(
                TaskStatus.IN_PROGRESS, 1L, pageable
        )).thenReturn(taskPage);
        when(taskMapper.toResponse(task)).thenReturn(response);

        Page<TaskResponse> result = taskService.getTasks(
                TaskStatus.IN_PROGRESS, 1L, pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(
                "Implement user authentication",
                result.getContent().get(0).title()
        );

        verify(taskRepository).findTasksWithFilters(
                TaskStatus.IN_PROGRESS, 1L, pageable
        );
    }

    @Test
    void updateTask_whenTaskExists_updatesTask() {
        TaskRequest updateRequest = new TaskRequest(
                "Implement login API",
                TaskStatus.DONE,
                TaskPriority.HIGH,
                1L,
                1L,
                LocalDate.of(2026, 11, 20)
        );

        TaskResponse updatedResponse = new TaskResponse(
                1L,
                "Implement login API",
                TaskStatus.DONE,
                TaskPriority.HIGH,
                1L,
                "E-Commerce Platform",
                1L,
                "Rahul Sharma",
                LocalDate.of(2026, 11, 20)
        );

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));
        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(updatedResponse);

        // Simulate the behavior of MapStruct's update method.
        doAnswer(invocation -> {
            TaskRequest update = invocation.getArgument(0);
            Task target = invocation.getArgument(1);

            target.setTitle(update.title());
            target.setStatus(update.status());
            target.setPriority(update.priority());
            target.setDueDate(update.dueDate());

            return null;
        }).when(taskMapper).updateEntity(updateRequest, task);

        TaskResponse result = taskService.updateTask(1L, updateRequest);

        assertEquals("Implement login API", task.getTitle());
        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals("Implement login API", result.title());

        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_whenTaskDoesNotExist_throwsException() {
        when(taskRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.updateTask(99L, request)
        );

        verifyNoInteractions(projectRepository);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(taskMapper);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_whenProjectDoesNotExist_throwsException() {
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));
        when(projectRepository.findById(99L))
                .thenReturn(Optional.empty());

        TaskRequest invalidProjectRequest = new TaskRequest(
                "Updated task",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                99L,
                null,
                null
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.updateTask(1L, invalidProjectRequest)
        );

        verify(taskRepository, never()).save(any(Task.class));
        verifyNoInteractions(taskMapper);
    }

    @Test
    void deleteTask_whenTaskExists_deletesTask() {
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        assertDoesNotThrow(() -> taskService.deleteTask(1L));

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_whenTaskDoesNotExist_throwsException() {
        when(taskRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.deleteTask(99L)
        );

        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void getTasksByProjectId_whenProjectExists_returnsTasks() {
        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(taskRepository.findTasksByProjectIdWithDetails(1L))
                .thenReturn(List.of(task));
        when(taskMapper.toResponseList(List.of(task)))
                .thenReturn(List.of(response));

        List<TaskResponse> result =
                taskService.getTasksByProjectId(1L);

        assertEquals(1, result.size());
        assertEquals("Implement user authentication", result.get(0).title());

        verify(taskRepository).findTasksByProjectIdWithDetails(1L);
        verify(taskMapper).toResponseList(List.of(task));
    }

    @Test
    void getTasksByProjectId_whenProjectDoesNotExist_throwsException() {
        when(projectRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.getTasksByProjectId(99L)
        );

        verifyNoInteractions(taskRepository);
        verifyNoInteractions(taskMapper);
    }
}

