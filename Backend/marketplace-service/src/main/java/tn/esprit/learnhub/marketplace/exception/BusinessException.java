package tn.esprit.learnhub.marketplace.exception;

/** Règle métier violée (400). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
