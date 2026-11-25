package tn.esprit.learnhub.marketplace.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Création d'une commande contenant un ou plusieurs cours")
public record CreateOrderRequest(

        @Schema(description = "Identifiants des cours du catalogue (course-service)", example = "[1, 2]")
        @NotEmpty(message = "La commande doit contenir au moins un cours")
        @Size(max = 20, message = "Une commande ne peut pas contenir plus de 20 cours")
        List<@NotNull(message = "courseId ne peut pas être nul") @Positive(message = "courseId doit être positif") Long> courseIds,

        @Schema(description = "Devise ISO 4217 (défaut : TND)", example = "TND")
        @Pattern(regexp = "^[A-Z]{3}$", message = "La devise doit être un code ISO de 3 lettres majuscules")
        String currency,

        @Schema(description = "Réservé ADMIN (backoffice) : créer une commande pour un étudiant donné. "
                + "Ignoré pour un STUDENT : l'identité vient du header X-User-Id.",
                example = "a1b2c3d4-0000-4000-8000-000000000001")
        String studentId
) {
}
