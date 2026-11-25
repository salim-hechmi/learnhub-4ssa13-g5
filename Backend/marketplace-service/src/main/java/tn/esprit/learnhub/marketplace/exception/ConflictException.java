package tn.esprit.learnhub.marketplace.exception;

/** Conflit avec l'état actuel (409), ex. cours déjà acheté. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
