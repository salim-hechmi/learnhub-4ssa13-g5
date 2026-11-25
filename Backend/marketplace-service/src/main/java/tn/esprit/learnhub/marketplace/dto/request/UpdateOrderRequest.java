package tn.esprit.learnhub.marketplace.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Modification du contenu d'une commande (uniquement si PENDING ou FAILED)")
public record UpdateOrderRequest(

        @NotEmpty(message = "La commande doit contenir au moins un cours")
        @Size(max = 20, message = "Une commande ne peut pas contenir plus de 20 cours")
        List<@NotNull @Positive Long> courseIds,

        @Pattern(regexp = "^[A-Z]{3}$", message = "La devise doit être un code ISO de 3 lettres majuscules")
        String currency
) {
}
