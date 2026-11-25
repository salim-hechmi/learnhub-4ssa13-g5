package com.learnhub.community.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

/** TABLE D'ASSOCIATION utilisateur <-> badge (un badge n'est attribué qu'une fois par utilisateur). */
@Data
@Entity
@Table(name = "user_badges",
       uniqueConstraints = @UniqueConstraint(name = "uniq_user_badge", columnNames = {"user_id", "badge_id"}))
public class UserBadge {
    @Id
    @UuidGenerator
    @Column(length = 36)
    private String id;
    @NotBlank @Column(name = "user_id", nullable = false) private String userId;
    @NotBlank @Column(name = "badge_id", nullable = false) private String badgeId;
    private String source = "MANUAL"; // MANUAL | RABBITMQ
    @Column(nullable = false) private Instant awardedAt = Instant.now();
}
