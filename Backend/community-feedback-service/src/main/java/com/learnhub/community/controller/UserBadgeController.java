package com.learnhub.community.controller;

import com.learnhub.community.model.UserBadge;
import com.learnhub.community.repository.UserBadgeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Table d'association utilisateur <-> badge. */
@RestController
@RequestMapping("/api/user-badges")
public class UserBadgeController extends BaseCrudController<UserBadge> {

    private final UserBadgeRepository repository;

    public UserBadgeController(UserBadgeRepository repository) { this.repository = repository; }

    @Override protected JpaRepository<UserBadge, String> repo() { return repository; }
    @Override protected void setId(UserBadge e, String id) { e.setId(id); }
    @Override protected String label() { return "Attribution de badge"; }

    @GetMapping("/user/{userId}")
    public List<UserBadge> byUser(@PathVariable String userId) { return repository.findByUserId(userId); }
}
