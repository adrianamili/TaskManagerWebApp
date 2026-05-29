package com.taskmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "tasks")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Numele tag-ului este obligatoriu")
    @Size(min = 1, max = 30)
    @Column(unique = true, nullable = false)
    private String name;

    @Column(length = 7)
    @Builder.Default
    private String color = "#6c757d";  // Culoare hex implicita

    @Size(max = 200)
    private String description;

    // Partea inversa a relatiei @ManyToMany cu Task
    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    private Set<Task> tasks = new HashSet<>();
}
