package com.taskmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"owner", "tasks"})
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Numele proiectului este obligatoriu")
    @Size(min = 2, max = 100, message = "Numele trebuie sa aiba intre 2 si 100 de caractere")
    @Column(nullable = false)
    private String name;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.ACTIVE;

    private LocalDate deadline;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // @ManyToOne — mai multe proiecte apartin unui User (owner)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // @OneToMany — un proiect are mai multe task-uri
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ProjectStatus {
        ACTIVE, COMPLETED, ARCHIVED, ON_HOLD
    }

    public long getCompletedTasksCount() {
        if (tasks == null) return 0;
        return tasks.stream()
                .filter(t -> t.getStatus() == Task.TaskStatus.DONE)
                .count();
    }

    public int getProgressPercent() {
        if (tasks == null || tasks.isEmpty()) return 0;
        return (int) ((getCompletedTasksCount() * 100) / tasks.size());
    }

}
