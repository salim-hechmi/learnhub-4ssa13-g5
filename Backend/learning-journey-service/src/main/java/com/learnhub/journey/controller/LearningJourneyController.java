package com.learnhub.journey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "LearningJourneyController", description = "Contrats d'API REST pour learning-journey-service")
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LearningJourneyController {

    @Operation(summary = "Créer une nouvelle inscription à un cours")
    @ApiResponse(responseCode = "201", description = "Opération réussie")
    @PostMapping("/enrollments")
    public ResponseEntity<Map<String, Object>> enrollments() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(201).body(Collections.emptyMap());
    }

    @Operation(summary = "Consulter toutes les inscriptions d un étudiant")
    @ApiResponse(responseCode = "200", description = "Opération réussie")
    @GetMapping("/students/{id}/enrollments")
    public ResponseEntity<List<Map<String, Object>>> students__id__enrollments() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(200).body(Collections.emptyMap());
    }

    @Operation(summary = "Consulter la progression détaillée d une inscription")
    @ApiResponse(responseCode = "200", description = "Opération réussie")
    @GetMapping("/enrollments/{id}/progress")
    public ResponseEntity<Map<String, Object>> enrollments__id__progress() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(200).body(Collections.emptyMap());
    }

    @Operation(summary = "Marquer une ressource/leçon comme terminée")
    @ApiResponse(responseCode = "200", description = "Opération réussie")
    @PutMapping("/lessons/{id}/completion")
    public ResponseEntity<Map<String, Object>> lessons__id__completion() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(200).body(Collections.emptyMap());
    }

    @Operation(summary = "Définir un nouvel objectif d apprentissage")
    @ApiResponse(responseCode = "201", description = "Opération réussie")
    @PostMapping("/learning-goals")
    public ResponseEntity<Map<String, Object>> learning_goals() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(201).body(Collections.emptyMap());
    }
}
