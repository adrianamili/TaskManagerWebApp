package com.taskmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "task")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Numele fisierului este obligatoriu")
    @Column(nullable = false)
    private String fileName;

    @NotBlank
    @Column(nullable = false)
    private String fileUrl;

    private String fileType;
    private Long fileSize;

    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    private String uploadedBy;

    // @ManyToOne — mai multe atasamente apartin unui task
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    public String getFileSizeFormatted() {
        if (fileSize == null) return "N/A";
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return (fileSize / 1024) + " KB";
        return (fileSize / (1024 * 1024)) + " MB";
    }
}
