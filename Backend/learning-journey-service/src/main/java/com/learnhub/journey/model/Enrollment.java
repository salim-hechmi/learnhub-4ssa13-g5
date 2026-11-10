package com.learnhub.journey.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier Enrollment propre au contexte borné de learning-journey-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "enrollments")
public class Enrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;
    private Long courseId;
    private String status = "ACTIVE";
    private java.time.LocalDateTime enrolledAt = java.time.LocalDateTime.now();

    public Enrollment() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
