package tn.esprit.learnhub.marketplace.exception;

/** Service distant indisponible (503), ex. course-service en panne. */
public class ExternalServiceUnavailableException extends RuntimeException {
    public ExternalServiceUnavailableException(String message) {
        super(message);
    }
}
