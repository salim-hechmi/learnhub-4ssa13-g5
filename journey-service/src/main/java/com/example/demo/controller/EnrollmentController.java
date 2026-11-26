package com.example.demo.controller;

import com.example.demo.dto.EnrollmentRequest;
import com.example.demo.dto.EnrollmentResponse;
import com.example.demo.dto.ProgressResponse;
import com.example.demo.entity.Enrollment;
import com.example.demo.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService service;

    public EnrollmentController(EnrollmentService service) {
        this.service = service;
    }

    // ---------- CRUD back office ----------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse create(@Valid @RequestBody EnrollmentRequest request) {
        return EnrollmentResponse.from(service.create(request));
    }

    @GetMapping
    public List<EnrollmentResponse> all(@RequestParam(required = false) Enrollment.Status status) {
        return service.findAll(status).stream().map(EnrollmentResponse::from).toList();
    }

    @GetMapping("/{id}")
    public EnrollmentResponse one(@PathVariable Long id) {
        return EnrollmentResponse.from(service.findById(id));
    }

    @PutMapping("/{id}")
    public EnrollmentResponse update(@PathVariable Long id, @Valid @RequestBody EnrollmentRequest request) {
        return EnrollmentResponse.from(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ---------- Endpoints de la slide ----------

    // Temporaire : plus tard, l'identité viendra du JWT via le Gateway
    @GetMapping("/me")
    public List<EnrollmentResponse> mine(@RequestHeader("X-Student-Id") Long studentId) {
        return service.findByStudent(studentId).stream().map(EnrollmentResponse::from).toList();
    }

    @GetMapping("/{id}/progress")
    public ProgressResponse progress(@PathVariable Long id) {
        return service.progress(id);
    }

    @PutMapping("/{id}/lessons/{lessonId}/complete")
    public EnrollmentResponse complete(@PathVariable Long id, @PathVariable Long lessonId) {
        return EnrollmentResponse.from(service.completeLesson(id, lessonId));
    }

    @GetMapping("/check")
    public Map<String, Boolean> check(@RequestParam Long studentId, @RequestParam Long courseId) {
        service.check(studentId, courseId);
        return Map.of("enrolled", true);
    }
}