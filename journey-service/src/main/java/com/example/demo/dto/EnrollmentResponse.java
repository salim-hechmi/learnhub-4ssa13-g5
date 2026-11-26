package com.example.demo.dto;

import com.example.demo.entity.Enrollment;
import java.time.LocalDateTime;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        Long courseId,
        int totalLessons,
        int progressPercent,
        Enrollment.Status status,
        LocalDateTime enrolledAt) {

    public static EnrollmentResponse from(Enrollment e) {
        return new EnrollmentResponse(e.getId(), e.getStudentId(), e.getCourseId(),
                e.getTotalLessons(), e.getProgressPercent(), e.getStatus(), e.getEnrolledAt());
    }
}