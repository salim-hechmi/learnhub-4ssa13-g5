package com.learnhub.community.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

/** Avis noté sur un cours (table "course_reviews"). */
@Data
@Entity
@Table(name = "course_reviews", indexes = @Index(name = "idx_review_course", columnList = "course_id"))
public class CourseReview {
    @Id
    @UuidGenerator
    @Column(length = 36)
    private String id;
    @NotBlank @Column(nullable = false) private String userId;
    @NotBlank @Column(nullable = false) private String authorName;
    @NotBlank @Column(name = "course_id", nullable = false) private String courseId;
    @NotBlank @Column(nullable = false) private String courseTitle;
    @Min(1) @Max(5) @Column(nullable = false) private int rating;
    @NotBlank @Size(max = 1000) @Column(nullable = false, length = 1000) private String comment;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModerationStatus moderationStatus = ModerationStatus.PENDING;
    @Column(nullable = false) private boolean verifiedEnrollment = false; // sera alimenté par OpenFeign (journey-service)
    @Column(nullable = false) private Instant createdAt = Instant.now();
}
