package com.learnhub.community.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier DiscussionThread propre au contexte borné de community-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "discussionthreads")
public class DiscussionThread implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long courseId;
    private Long authorId;
    private String title;
    private String content;
    private Boolean isClosed = false;
    private Boolean isPinned = false;
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    public DiscussionThread() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
