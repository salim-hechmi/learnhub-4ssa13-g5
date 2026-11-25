package com.learnhub.course.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité JPA représentant un cours.
 * Correspond à la table `course` dans learnhub_course_db.
 *
 * <p>RÈGLE ARCHITECTURE : instructor_id est un UUID String de Keycloak (référence logique).
 * Aucune FK inter-services n'est utilisée.</p>
 */
@Entity
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", length = 20)
    @Builder.Default
    private CourseLevel level = CourseLevel.BEGINNER;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private CourseStatus status = CourseStatus.DRAFT;

    /**
     * UUID Keycloak de l'instructeur propriétaire du cours.
     * Référence logique : PAS de FK SQL inter-services.
     */
    @Column(name = "instructor_id", length = 64)
    private String instructorId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ===== Relations INTRA-service (même base learnhub_course_db) =====

    /** FK intra-service -> table category (même base de données). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /** Relation bidirectionnelle : un cours contient plusieurs chapitres. */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("position ASC")
    @Builder.Default
    private List<Chapter> chapters = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
