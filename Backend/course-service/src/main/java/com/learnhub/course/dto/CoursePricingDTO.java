package com.learnhub.course.dto;

import java.math.BigDecimal;

/**
 * DTO de pricing exposé à Marketplace Service via OpenFeign.
 * Endpoint : GET /api/courses/{id}/pricing
 * <p>
 * status (DRAFT / PUBLISHED / ARCHIVED) : le Marketplace refuse l'achat d'un cours non publié.
 */
public record CoursePricingDTO(
        Integer id,
        String title,
        BigDecimal price,
        String status
) {}
