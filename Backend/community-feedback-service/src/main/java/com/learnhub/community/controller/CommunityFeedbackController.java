package com.learnhub.community.controller;

import com.learnhub.community.exception.NotFoundException;
import com.learnhub.community.model.CourseReview;
import com.learnhub.community.model.ModerationStatus;
import com.learnhub.community.repository.CourseReviewRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

/** Contrôleur principal : /api/reviews (entité principale CourseReview). */
@RestController
@RequestMapping("/api/reviews")
public class CommunityFeedbackController extends BaseCrudController<CourseReview> {

    private final CourseReviewRepository repository;

    public CommunityFeedbackController(CourseReviewRepository repository) { this.repository = repository; }

    @Override protected JpaRepository<CourseReview, String> repo() { return repository; }
    @Override protected void setId(CourseReview e, String id) { e.setId(id); }
    @Override protected String label() { return "Avis"; }

    /** GET /api/reviews/search?q=docker&status=PENDING */
    @GetMapping("/search")
    public List<CourseReview> search(@RequestParam(required = false) String q,
                                     @RequestParam(required = false) ModerationStatus status) {
        String needle = q == null ? "" : q.toLowerCase(Locale.ROOT);
        return repository.findAll().stream()
                .filter(r -> status == null || r.getModerationStatus() == status)
                .filter(r -> needle.isEmpty()
                        || r.getAuthorName().toLowerCase(Locale.ROOT).contains(needle)
                        || r.getCourseTitle().toLowerCase(Locale.ROOT).contains(needle)
                        || r.getComment().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }

    @GetMapping("/course/{courseId}")
    public List<CourseReview> byCourse(@PathVariable String courseId) {
        return repository.findByCourseId(courseId);
    }

    /** PATCH /api/reviews/{id}/moderate?status=APPROVED */
    @PatchMapping("/{id}/moderate")
    public CourseReview moderate(@PathVariable String id, @RequestParam ModerationStatus status) {
        CourseReview r = repository.findById(id).orElseThrow(() -> new NotFoundException("Avis introuvable : " + id));
        r.setModerationStatus(status);
        return repository.save(r);
    }
}
