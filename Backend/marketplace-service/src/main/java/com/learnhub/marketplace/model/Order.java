package com.learnhub.marketplace.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier Order propre au contexte borné de marketplace-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "orders")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;
    private Double totalAmount;
    private String currency = "EUR";
    private String status = "PENDING";
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    public Order() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
