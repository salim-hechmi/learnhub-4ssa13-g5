package com.learnhub.marketplace.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier Payment propre au contexte borné de marketplace-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "payments")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Double amount;
    private String paymentMethod = "SIMULATED_GATEWAY";
    private String transactionId;
    private String status = "SUCCESS";
    private java.time.LocalDateTime paymentDate = java.time.LocalDateTime.now();

    public Payment() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
