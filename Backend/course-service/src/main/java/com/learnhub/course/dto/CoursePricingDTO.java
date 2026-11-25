package com.learnhub.course.dto;

import java.math.BigDecimal;

/**
 * DTO de pricing exposé à Marketplace Service via OpenFeign.
 * Endpoint : GET /api/courses/{id}/pricing
 */
public record CoursePricingDTO(
        Integer id,
        String title,
        BigDecimal price
) {}
