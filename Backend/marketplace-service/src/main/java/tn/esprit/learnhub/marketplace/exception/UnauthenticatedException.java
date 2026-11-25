package tn.esprit.learnhub.marketplace.exception;

/** Identité absente : l'appel n'est pas passé par l'API Gateway (401). */
public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException(String message) {
        super(message);
    }
}
