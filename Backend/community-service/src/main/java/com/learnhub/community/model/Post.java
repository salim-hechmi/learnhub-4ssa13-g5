package com.learnhub.community.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier Post propre au contexte borné de community-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "posts")
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long threadId;
    private Long authorId;
    private String content;
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    public Post() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
