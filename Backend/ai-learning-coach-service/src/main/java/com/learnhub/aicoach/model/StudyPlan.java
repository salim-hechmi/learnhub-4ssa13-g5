package com.learnhub.aicoach.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier StudyPlan propre au contexte borné de ai-learning-coach-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "studyplans")
public class StudyPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;
    private String targetObjective;
    private Integer weeklyAvailabilityHours;
    private String recommendedScheduleJson;
    private java.time.LocalDateTime generatedAt = java.time.LocalDateTime.now();

    public StudyPlan() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
