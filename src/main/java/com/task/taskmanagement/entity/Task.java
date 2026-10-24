package com.task.taskmanagement.entity;

import com.task.taskmanagement.enums.TaskPriority;
import com.task.taskmanagement.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "task")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "assignee_id")
    private User assignee;

    @Column(name = "due_date")
    private LocalDate dueDate;
}
