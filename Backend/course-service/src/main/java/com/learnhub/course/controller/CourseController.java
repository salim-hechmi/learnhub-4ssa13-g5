package com.learnhub.course.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "CourseController", description = "Contrats d'API REST pour course-service")
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CourseController {

    @Operation(summary = "Lister les cours publiés du catalogue")
    @ApiResponse(responseCode = "200", description = "Opération réussie")
    @GetMapping("")
    public ResponseEntity<List<Map<String, Object>>> getAll() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(200).body(Collections.emptyMap());
    }

    @Operation(summary = "Consulter les détails d un cours par son ID")
    @ApiResponse(responseCode = "200", description = "Opération réussie")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> id_() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(200).body(Collections.emptyMap());
    }

    @Operation(summary = "Créer un nouveau cours (Formateur)")
    @ApiResponse(responseCode = "201", description = "Opération réussie")
    @PostMapping("")
    public ResponseEntity<Map<String, Object>> getAll() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(201).body(Collections.emptyMap());
    }

    @Operation(summary = "Modifier les informations d un cours")
    @ApiResponse(responseCode = "200", description = "Opération réussie")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> id_() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(200).body(Collections.emptyMap());
    }

    @Operation(summary = "Publier officiellement un cours dans le catalogue")
    @ApiResponse(responseCode = "200", description = "Opération réussie")
    @PostMapping("/{id}/publish")
    public ResponseEntity<Map<String, Object>> id__publish() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(200).body(Collections.emptyMap());
    }

    @Operation(summary = "Ajouter un chapitre à un cours existant")
    @ApiResponse(responseCode = "201", description = "Opération réussie")
    @PostMapping("/{id}/chapters")
    public ResponseEntity<Map<String, Object>> id__chapters() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(201).body(Collections.emptyMap());
    }
}
