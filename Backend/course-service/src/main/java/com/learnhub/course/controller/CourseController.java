package com.learnhub.course.controller;

import com.learnhub.course.dto.*;
import com.learnhub.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour le catalogue de cours.
 * Base URL : /api/courses
 *
 * <p>Responsable : Nawres Soua - 4SSA13 G5 - LearnHub</p>
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Cours", description = "API de gestion du catalogue de cours LearnHub")
public class CourseController {

    private final CourseService courseService;

    // -----------------------------------------------------------------
    // GET /api/courses  -> Catalogue paginé et filtrable
    // -----------------------------------------------------------------
    @GetMapping
    @Operation(
        summary = "Catalogue des cours publiés",
        description = "Retourne la liste paginée des cours publiés. Filtres optionnels : categoryId, level, keyword."
    )
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    public ResponseEntity<Page<CourseResponseDTO>> getAllPublishedCourses(
            @Parameter(description = "ID de la catégorie") @RequestParam(required = false) Integer categoryId,
            @Parameter(description = "Niveau : BEGINNER, INTERMEDIATE, ADVANCED") @RequestParam(required = false) String level,
            @Parameter(description = "Mot-clé de recherche") @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        return ResponseEntity.ok(courseService.getAllPublishedCourses(categoryId, level, keyword, pageable));
    }

    // -----------------------------------------------------------------
    // POST /api/courses  -> Création d'un cours
    // -----------------------------------------------------------------
    @PostMapping
    @Operation(summary = "Créer un nouveau cours", description = "Crée un cours au statut DRAFT. Réservé aux instructeurs.")
    @ApiResponse(responseCode = "201", description = "Cours créé avec succès")
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request));
    }

    // -----------------------------------------------------------------
    // GET /api/courses/{id}  -> Détail d'un cours
    // -----------------------------------------------------------------
    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un cours", description = "Retourne les informations complètes d'un cours par son ID.")
    @ApiResponse(responseCode = "200", description = "Cours trouvé")
    @ApiResponse(responseCode = "404", description = "Cours introuvable")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // -----------------------------------------------------------------
    // PUT /api/courses/{id}  -> Mise à jour d'un cours
    // -----------------------------------------------------------------
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un cours")
    @ApiResponse(responseCode = "200", description = "Cours mis à jour avec succès")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Integer id,
            @Valid @RequestBody CourseRequestDTO request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    // -----------------------------------------------------------------
    // DELETE /api/courses/{id}  -> Suppression d'un cours
    // -----------------------------------------------------------------
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un cours")
    @ApiResponse(responseCode = "204", description = "Cours supprimé avec succès")
    public ResponseEntity<Void> deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    // -----------------------------------------------------------------
    // PUT /api/courses/{id}/publish  -> Publier un cours
    // -----------------------------------------------------------------
    @PutMapping("/{id}/publish")
    @Operation(summary = "Publier un cours", description = "Change le statut du cours de DRAFT à PUBLISHED et émet l'événement RabbitMQ.")
    @ApiResponse(responseCode = "200", description = "Cours publié avec succès")
    public ResponseEntity<CourseResponseDTO> publishCourse(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.publishCourse(id));
    }

    // -----------------------------------------------------------------
    // GET /api/courses/{id}/curriculum  -> Programme complet
    // -----------------------------------------------------------------
    @GetMapping("/{id}/curriculum")
    @Operation(summary = "Programme complet du cours", description = "Retourne la hiérarchie chapitres + leçons du cours.")
    @ApiResponse(responseCode = "200", description = "Curriculum récupéré avec succès")
    public ResponseEntity<CourseCurriculumDTO> getCourseCurriculum(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.getCourseCurriculum(id));
    }

    // -----------------------------------------------------------------
    // GET /api/courses/{id}/pricing  -> Pricing (OpenFeign Marketplace)
    // -----------------------------------------------------------------
    @GetMapping("/{id}/pricing")
    @Operation(summary = "Pricing d'un cours", description = "Endpoint consommé par Marketplace Service via OpenFeign pour vérifier l'existence et le prix réel du cours.")
    @ApiResponse(responseCode = "200", description = "Pricing récupéré")
    @ApiResponse(responseCode = "404", description = "Cours introuvable")
    public ResponseEntity<CoursePricingDTO> getCoursePricing(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.getCoursePricing(id));
    }

    // -----------------------------------------------------------------
    // GET /api/courses/instructor/{instructorId}
    // -----------------------------------------------------------------
    @GetMapping("/instructor/{instructorId}")
    @Operation(summary = "Cours d'un instructeur", description = "Retourne tous les cours créés par un instructeur (UUID Keycloak).")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByInstructor(@PathVariable String instructorId) {
        return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorId));
    }
}
