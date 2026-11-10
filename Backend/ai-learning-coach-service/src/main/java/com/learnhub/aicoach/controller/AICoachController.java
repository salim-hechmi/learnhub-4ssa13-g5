package com.learnhub.aicoach.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "AICoachController", description = "Contrats d'API REST pour ai-learning-coach-service")
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AICoachController {

    @Operation(summary = "Générer un plan d étude personnalisé selon l objectif et la disponibilité")
    @ApiResponse(responseCode = "201", description = "Opération réussie")
    @PostMapping("/study-plans")
    public ResponseEntity<Map<String, Object>> study_plans() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(201).body(Collections.emptyMap());
    }

    @Operation(summary = "Obtenir les recommandations de cours personnalisées par l IA")
    @ApiResponse(responseCode = "200", description = "Opération réussie")
    @GetMapping("/students/{id}/recommendations")
    public ResponseEntity<List<Map<String, Object>>> students__id__recommendations() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(200).body(Collections.emptyMap());
    }

    @Operation(summary = "Démarrer une session de conversation avec le coach IA")
    @ApiResponse(responseCode = "201", description = "Opération réussie")
    @PostMapping("/chat/sessions")
    public ResponseEntity<Map<String, Object>> chat_sessions() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(201).body(Collections.emptyMap());
    }

    @Operation(summary = "Envoyer une question au coach IA et recevoir une réponse adaptée")
    @ApiResponse(responseCode = "201", description = "Opération réussie")
    @PostMapping("/chat/sessions/{id}/messages")
    public ResponseEntity<Map<String, Object>> chat_sessions__id__messages() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(201).body(Collections.emptyMap());
    }
}
