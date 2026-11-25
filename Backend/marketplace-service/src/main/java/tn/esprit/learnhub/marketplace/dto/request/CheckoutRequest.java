package tn.esprit.learnhub.marketplace.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import tn.esprit.learnhub.marketplace.enums.PaymentMethod;

@Schema(description = "Paiement sandbox simulé d'une commande")
public record CheckoutRequest(

        @Schema(example = "1")
        @NotNull(message = "orderId est obligatoire")
        @Positive(message = "orderId doit être positif")
        Long orderId,

        @Schema(example = "STRIPE")
        @NotNull(message = "La méthode de paiement est obligatoire (STRIPE, PAYPAL, SIMULATOR)")
        PaymentMethod method,

        @Schema(description = "Carte de test (jamais stockée). 4242424242424242 = accepté, "
                + "4000000000000002 = refusé (cartes de test Stripe).", example = "4242424242424242")
        @Pattern(regexp = "^[0-9]{12,19}$", message = "Numéro de carte de test invalide (12 à 19 chiffres)")
        String cardNumber,

        @Schema(description = "Force un refus de paiement pour tester le scénario FAILED", example = "false")
        Boolean simulateFailure
) {
}
