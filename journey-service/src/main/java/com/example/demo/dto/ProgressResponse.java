package com.example.demo.dto;

import com.example.demo.entity.Enrollment;

public record ProgressResponse(
        Long enrollmentId,
        long completedLessons,
        int totalLessons,
        int progressPercent,
        Enrollment.Status status) {
}