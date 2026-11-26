package com.example.demo.dto;

import com.example.demo.entity.Enrollment;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
        @NotNull Long studentId,
        @NotNull Long courseId,
        @Min(0) int totalLessons,
        Enrollment.Status status) {
}