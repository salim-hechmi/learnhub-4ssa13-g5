package com.learnhub.aicoach.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier AIRecommendation propre au contexte borné de ai-learning-coach-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "airecommendations")
public class AIRecommendation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;
    private Long studyPlanId;
    private Long recommendedCourseId;
    private String justificationReason;
    private String priorityLevel = "HIGH";

    public AIRecommendation() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
