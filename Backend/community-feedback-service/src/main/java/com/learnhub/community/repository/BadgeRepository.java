package com.learnhub.community.repository;

import com.learnhub.community.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, String> {
}
