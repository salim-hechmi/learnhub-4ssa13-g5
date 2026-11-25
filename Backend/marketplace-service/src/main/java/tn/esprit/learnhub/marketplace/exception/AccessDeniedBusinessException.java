package tn.esprit.learnhub.marketplace.exception;

/** Accès refusé à la ressource d'un autre étudiant (403). */
public class AccessDeniedBusinessException extends RuntimeException {
    public AccessDeniedBusinessException(String message) {
        super(message);
    }
}
