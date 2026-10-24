package com.task.taskmanagement.repository;

import com.task.taskmanagement.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project,Long> {

    boolean existsByNameIgnoreCase(String name);


}
