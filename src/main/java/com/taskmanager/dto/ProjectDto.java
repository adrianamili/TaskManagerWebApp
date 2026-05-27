package com.taskmanager.dto;

import com.taskmanager.model.Project.ProjectStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {

    private Long id;

    @NotBlank(message = "Numele proiectului este obligatoriu")
    @Size(min = 2, max = 100, message = "Numele trebuie sa aiba intre 2 si 100 de caractere")
    private String name;

    @Size(max = 1000)
    private String description;

    private ProjectStatus status = ProjectStatus.ACTIVE;
    private LocalDate deadline;
}
