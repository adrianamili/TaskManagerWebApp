package com.taskmanager.service;

import com.taskmanager.dto.ProjectDto;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedAccessException;
import com.taskmanager.model.Project;
import com.taskmanager.model.User;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public Project create(ProjectDto dto, String username) {
        log.info("[CREATE] Creare proiect '{}' de catre {}", dto.getName(), username);

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User " + username + " nu a fost gasit"));

        Project project = Project.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : Project.ProjectStatus.ACTIVE)
                .deadline(dto.getDeadline())
                .owner(owner)
                .build();

        Project saved = projectRepository.save(project);
        log.info("[CREATE] Proiect creat cu id={}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Project findById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proiect", id));
        // Forteaza incarcarea tuturor relatiilor lazy
        project.getOwner().getUsername();
        project.getTasks().forEach(t -> {
            t.getProject().getName();
            t.getTags().size();
            t.getComments().size();
            if (t.getAssignee() != null) {
                t.getAssignee().getUsername();
            }
        });
        return project;
    }

    @Transactional(readOnly = true)
    public Page<Project> findByOwner(String username, Pageable pageable) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User " + username + " nu a fost gasit"));
        Page<Project> projects = projectRepository.findByOwnerId(owner.getId(), pageable);
        // Forteaza incarcarea task-urilor pentru fiecare proiect
        projects.getContent().forEach(p -> p.getTasks().size());
        return projects;
    }

    @Transactional(readOnly = true)
    public Page<Project> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    public Project update(Long id, ProjectDto dto, String username) {
        log.info("[UPDATE] Actualizare proiect id={}", id);
        Project project = findById(id);

        if (!project.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Nu aveti permisiunea de a modifica acest proiect.");
        }

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus());
        project.setDeadline(dto.getDeadline());

        return projectRepository.save(project);
    }

    public void delete(Long id, String username) {
        log.info("[DELETE] Stergere proiect id={}", id);
        Project project = findById(id);

        if (!project.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Nu aveti permisiunea de a sterge acest proiect.");
        }

        projectRepository.delete(project);
        log.info("[DELETE] Proiect id={} sters cu succes", id);
    }
}
