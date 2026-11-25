package com.learnhub.course.controller;

import com.learnhub.course.dto.*;
import com.learnhub.course.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Tag(name = "Leçons", description = "API de gestion des leçons")
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/chapter/{chapterId}")
    @Operation(summary = "Leçons d'un chapitre")
    public ResponseEntity<List<LessonResponseDTO>> getLessonsByChapter(@PathVariable Integer chapterId) {
        return ResponseEntity.ok(lessonService.getLessonsByChapter(chapterId));
    }

    @PostMapping
    @Operation(summary = "Créer une leçon")
    public ResponseEntity<LessonResponseDTO> createLesson(@Valid @RequestBody LessonRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.createLesson(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une leçon")
    public ResponseEntity<LessonResponseDTO> updateLesson(
            @PathVariable Integer id,
            @Valid @RequestBody LessonRequestDTO request) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une leçon")
    public ResponseEntity<Void> deleteLesson(@PathVariable Integer id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
}
