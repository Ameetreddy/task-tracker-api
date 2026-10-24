package com.task.taskmanagement.service.impl;

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
import com.task.taskmanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Value("${task.default-priority:MEDIUM}")
    private TaskPriority defaultPriority;

    @Override
    public TaskResponse createTask(TaskRequest request) {

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: "
                                        + request.projectId()
                        ));

        User assignee = findAssignee(request.assigneeId());

        Task task = taskMapper.toEntity(request);

        task.setProject(project);
        task.setAssignee(assignee);

        if (request.priority() == null) {
            task.setPriority(defaultPriority);
        }

        Task savedTask = taskRepository.save(task);

        return taskMapper.toResponse(savedTask);
    }

    private User findAssignee(Long assigneeId) {

        if (assigneeId == null) {
            return null;
        }

        return userRepository.findById(assigneeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + assigneeId
                        ));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasks(
            TaskStatus status,
            Long projectId,
            Pageable pageable) {

        return taskRepository
                .findTasksWithFilters(status, projectId, pageable)
                .map(taskMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id
                        ));

        return taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse updateTask(Long id, TaskRequest request) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id
                        ));

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: "
                                        + request.projectId()
                        ));

        User assignee = findAssignee(request.assigneeId());

        taskMapper.updateEntity(request, task);

        task.setProject(project);
        task.setAssignee(assignee);

        if (request.priority() == null) {
            task.setPriority(defaultPriority);
        }
        Task updatedTask = taskRepository.save(task);

        return taskMapper.toResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id
                        ));



        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProjectId(Long projectId) {

        projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + projectId
                        ));

        List<Task> tasks =
                taskRepository.findTasksByProjectIdWithDetails(projectId);

        return taskMapper.toResponseList(tasks);
    }
}