package com.taskmanager.service;

import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedAccessException;
import com.taskmanager.model.Comment;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.repository.CommentRepository;
import com.taskmanager.repository.TaskRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Comment create(Long taskId, String content, String username) {
        log.info("[CREATE] Comentariu nou la task id={} de catre {}", taskId, username);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User " + username));

        Comment comment = Comment.builder()
                .content(content)
                .task(task)
                .author(author)
                .build();

        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public Page<Comment> findByTask(Long taskId, Pageable pageable) {
        return commentRepository.findByTaskId(taskId, pageable);
    }

    public Comment update(Long id, String newContent, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentariu", id));

        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Nu puteti edita comentariul altcuiva.");
        }

        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    public void delete(Long id, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentariu", id));

        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Nu puteti sterge comentariul altcuiva.");
        }

        commentRepository.delete(comment);
    }
}
