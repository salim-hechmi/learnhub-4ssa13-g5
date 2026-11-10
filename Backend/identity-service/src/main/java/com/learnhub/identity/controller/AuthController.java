package com.learnhub.identity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "AuthController", description = "Contrats d'API REST pour identity-service")
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    @Operation(summary = "Inscription d un nouvel utilisateur")
    @ApiResponse(responseCode = "201", description = "Opération réussie")
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(201).body(Collections.emptyMap());
    }

    @Operation(summary = "Connexion utilisateur et émission du jeton JWT")
    @ApiResponse(responseCode = "200", description = "Opération réussie")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(200).body(Collections.emptyMap());
    }

    @Operation(summary = "Récupérer le profil de l utilisateur connecté")
    @ApiResponse(responseCode = "200", description = "Opération réussie")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(200).body(Collections.emptyMap());
    }

    @Operation(summary = "Mettre à jour le profil utilisateur")
    @ApiResponse(responseCode = "200", description = "Opération réussie")
    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> me() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(200).body(Collections.emptyMap());
    }

    @Operation(summary = "Consulter les rôles d un utilisateur")
    @ApiResponse(responseCode = "200", description = "Opération réussie")
    @GetMapping("/{id}/roles")
    public ResponseEntity<List<String>> id__roles() {
        // Stub d'implémentation pour la validation 1 AWD (Conception validée)
        return ResponseEntity.status(200).body(Collections.emptyMap());
    }
}
