package tn.esprit.learnhub.marketplace.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import tn.esprit.learnhub.marketplace.enums.OrderStatus;

@Schema(description = "Changement manuel de statut (backoffice ADMIN)")
public record UpdateOrderStatusRequest(
        @Schema(example = "REFUNDED")
        @NotNull(message = "Le statut est obligatoire")
        OrderStatus status
) {
}
