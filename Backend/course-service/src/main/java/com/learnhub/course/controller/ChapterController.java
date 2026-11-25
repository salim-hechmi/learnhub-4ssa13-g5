package com.learnhub.course.controller;

import com.learnhub.course.dto.*;
import com.learnhub.course.service.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
@Tag(name = "Chapitres", description = "API de gestion des chapitres de cours")
public class ChapterController {

    private final ChapterService chapterService;

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Chapitres d'un cours")
    public ResponseEntity<List<ChapterResponseDTO>> getChaptersByCourse(@PathVariable Integer courseId) {
        return ResponseEntity.ok(chapterService.getChaptersByCourse(courseId));
    }

    @PostMapping
    @Operation(summary = "Créer un chapitre")
    public ResponseEntity<ChapterResponseDTO> createChapter(@Valid @RequestBody ChapterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chapterService.createChapter(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un chapitre")
    public ResponseEntity<ChapterResponseDTO> updateChapter(
            @PathVariable Integer id,
            @Valid @RequestBody ChapterRequestDTO request) {
        return ResponseEntity.ok(chapterService.updateChapter(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un chapitre")
    public ResponseEntity<Void> deleteChapter(@PathVariable Integer id) {
        chapterService.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }
}
