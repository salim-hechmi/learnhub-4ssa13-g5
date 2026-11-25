package com.learnhub.course.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité JPA représentant un chapitre d'un cours.
 * Correspond à la table `chapter` dans learnhub_course_db.
 */
@Entity
@Table(name = "chapter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "position")
    private Integer position;

    /** FK intra-service -> table course (même base de données). ON DELETE CASCADE géré par JPA. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    /** Relation bidirectionnelle : un chapitre contient plusieurs leçons. */
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("position ASC")
    @Builder.Default
    private List<Lesson> lessons = new ArrayList<>();
}
