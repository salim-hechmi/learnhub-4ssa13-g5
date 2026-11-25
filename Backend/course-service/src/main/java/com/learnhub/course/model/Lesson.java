package com.learnhub.course.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité JPA représentant une leçon (ressource d'apprentissage).
 * Correspond à la table `lesson` dans learnhub_course_db.
 *
 * Note : anciennement appelée LearningResource dans le template d'origine.
 */
@Entity
@Table(name = "lesson")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    @Builder.Default
    private LessonType type = LessonType.VIDEO;

    @Column(name = "content_url", length = 500)
    private String contentUrl;

    @Column(name = "duration_min")
    @Builder.Default
    private Integer durationMin = 0;

    @Column(name = "position")
    private Integer position;

    @Column(name = "is_free_preview")
    @Builder.Default
    private Boolean isFreePreview = false;

    /** FK intra-service -> table chapter (même base de données). ON DELETE CASCADE. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;
}
