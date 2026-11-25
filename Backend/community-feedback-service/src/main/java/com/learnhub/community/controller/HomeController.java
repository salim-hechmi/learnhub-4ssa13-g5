package com.learnhub.community.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/api")
    public Map<String, Object> home() {
        return Map.of(
                "service", "LearnHub Community & Feedback API",
                "status", "UP",
                "endpoints", Map.of(
                        "health", "/actuator/health",
                        "reviews", "/api/reviews",
                        "threads", "/api/threads",
                        "badges", "/api/badges",
                        "userBadges", "/api/user-badges"
                )
        );
    }
}
