package com.taskmanager.dto;

import com.taskmanager.model.Task.Priority;
import com.taskmanager.model.Task.TaskStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {

    private Long id;

    @NotBlank(message = "Titlul este obligatoriu")
    @Size(min = 2, max = 200, message = "Titlul trebuie sa aiba intre 2 si 200 de caractere")
    private String title;

    @Size(max = 2000)
    private String description;

    private Priority priority = Priority.MEDIUM;
    private TaskStatus status = TaskStatus.TODO;
    private LocalDate dueDate;
    private Long projectId;
    private Long assigneeId;
}
