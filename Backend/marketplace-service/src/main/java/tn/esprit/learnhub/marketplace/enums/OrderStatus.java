package tn.esprit.learnhub.marketplace.enums;

/** Cycle de vie d'une commande. */
public enum OrderStatus {
    PENDING("en attente"),   // créée, en attente de paiement
    PAID("payée"),           // paiement SUCCESS -> PaymentCompleted publié
    FAILED("échouée"),       // paiement refusé
    REFUNDED("remboursée");  // remboursée par l'administration

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    /** Libellé français utilisé dans les messages d'erreur renvoyés au front. */
    public String label() {
        return label;
    }
}
