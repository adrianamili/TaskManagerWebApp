package com.taskmanager.repository;

import com.taskmanager.model.Project;
import com.taskmanager.model.Project.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findByOwnerId(Long ownerId, Pageable pageable);
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);
    Page<Project> findByOwnerIdAndStatus(Long ownerId, ProjectStatus status, Pageable pageable);
    Optional<Project> findByIdAndOwnerId(Long id, Long ownerId);
    long countByOwnerId(Long ownerId);
}
