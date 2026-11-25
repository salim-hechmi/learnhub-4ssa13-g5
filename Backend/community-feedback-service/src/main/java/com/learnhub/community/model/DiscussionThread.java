package com.learnhub.community.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Fil de discussion (table "discussion_threads") + réponses (table "thread_replies", relation 1-N). */
@Data
@Entity
@Table(name = "discussion_threads", indexes = @Index(name = "idx_thread_course", columnList = "course_id"))
public class DiscussionThread {
    @Id
    @UuidGenerator
    @Column(length = 36)
    private String id;
    @NotBlank @Column(name = "course_id", nullable = false) private String courseId;
    @NotBlank @Column(nullable = false) private String authorId;
    @NotBlank @Column(nullable = false) private String authorName;
    @NotBlank @Column(nullable = false) private String title;
    @NotBlank @Column(nullable = false, columnDefinition = "TEXT") private String content;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "thread_replies", joinColumns = @JoinColumn(name = "thread_id"))
    private List<Reply> replies = new ArrayList<>();

    @Column(nullable = false) private Instant createdAt = Instant.now();

    @Data
    @Embeddable
    public static class Reply {
        private String authorId;
        private String authorName;
        @Column(columnDefinition = "TEXT") private String content;
        private Instant createdAt = Instant.now();
    }
}
