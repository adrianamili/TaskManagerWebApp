package com.taskmanager.service;

import com.taskmanager.dto.TaskDto;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedAccessException;
import com.taskmanager.model.*;
import com.taskmanager.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public Task create(TaskDto dto, String username) {
        log.info("[CREATE] Creare task '{}' in proiect id={}", dto.getTitle(), dto.getProjectId());

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Proiect", dto.getProjectId()));

        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority() != null ? dto.getPriority() : Task.Priority.MEDIUM)
                .status(Task.TaskStatus.TODO)
                .dueDate(dto.getDueDate())
                .project(project)
                .build();

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", dto.getAssigneeId()));
            task.setAssignee(assignee);
        }

        Task saved = taskRepository.save(task);
        log.info("[CREATE] Task creat cu id={}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Task findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        // Forteaza incarcarea tuturor relatiilor lazy
        task.getProject().getName();
        task.getProject().getOwner().getUsername();
        task.getTags().size();
        task.getComments().forEach(c -> c.getAuthor().getUsername());
        task.getAttachments().size();
        if (task.getAssignee() != null) {
            task.getAssignee().getUsername();
        }
        return task;
    }

    @Transactional(readOnly = true)
    public Page<Task> findByProject(Long projectId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByProjectId(projectId, pageable);
        tasks.getContent().forEach(t -> {
            t.getProject().getName();
            if (t.getAssignee() != null) t.getAssignee().getUsername();
            t.getTags().size();
        });
        return tasks;
    }

    @Transactional(readOnly = true)
    public Page<Task> findByAssignee(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User " + username + " nu a fost gasit"));
        Page<Task> tasks = taskRepository.findByAssigneeId(user.getId(), pageable);
        // Forteaza incarcarea relatiilor lazy
        tasks.getContent().forEach(t -> {
            t.getProject().getName();
            if (t.getAssignee() != null) t.getAssignee().getUsername();
        });
        return tasks;
    }

    @Transactional(readOnly = true)
    public Page<Task> findAll(Pageable pageable) {
        Page<Task> tasks = taskRepository.findAll(pageable);
        tasks.getContent().forEach(t -> {
            t.getProject().getName();
            if (t.getAssignee() != null) t.getAssignee().getUsername();
        });
        return tasks;
    }

    @Transactional(readOnly = true)
    public List<Task> findOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDate.now());
    }

    public Task update(Long id, TaskDto dto, String username) {
        log.info("[UPDATE] Actualizare task id={}", id);
        Task task = findById(id);

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());
        task.setDueDate(dto.getDueDate());

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", dto.getAssigneeId()));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(null);
        }

        return taskRepository.save(task);
    }

    public Task updateStatus(Long id, Task.TaskStatus newStatus) {
        log.info("[STATUS] Task id={} -> {}", id, newStatus);
        Task task = findById(id);
        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    public void delete(Long id, String username) {
        log.info("[DELETE] Stergere task id={}", id);
        Task task = findById(id);

        boolean isOwner = task.getProject().getOwner().getUsername().equals(username);
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getUsername().equals(username);

        if (!isOwner && !isAssignee) {
            throw new UnauthorizedAccessException("Nu aveti permisiunea de a sterge acest task.");
        }

        taskRepository.delete(task);
        log.info("[DELETE] Task id={} sters cu succes", id);
    }

    public Task addTag(Long taskId, Long tagId) {
        Task task = findById(taskId);
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", tagId));
        task.getTags().add(tag);
        return taskRepository.save(task);
    }

    public Task removeTag(Long taskId, Long tagId) {
        Task task = findById(taskId);
        task.getTags().removeIf(t -> t.getId().equals(tagId));
        return taskRepository.save(task);
    }
}
