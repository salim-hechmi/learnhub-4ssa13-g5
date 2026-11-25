package com.learnhub.community.controller;

import com.learnhub.community.model.Badge;
import com.learnhub.community.repository.BadgeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/badges")
public class BadgeController extends BaseCrudController<Badge> {

    private final BadgeRepository repository;

    public BadgeController(BadgeRepository repository) { this.repository = repository; }

    @Override protected JpaRepository<Badge, String> repo() { return repository; }
    @Override protected void setId(Badge e, String id) { e.setId(id); }
    @Override protected String label() { return "Badge"; }
}
