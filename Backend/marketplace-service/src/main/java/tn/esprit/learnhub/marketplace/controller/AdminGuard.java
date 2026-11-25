package tn.esprit.learnhub.marketplace.controller;

import tn.esprit.learnhub.marketplace.exception.AccessDeniedBusinessException;
import tn.esprit.learnhub.marketplace.util.CurrentUser;

/** Défense en profondeur : la Gateway filtre déjà, le service revérifie le rôle ADMIN. */
final class AdminGuard {

    private AdminGuard() {
    }

    static void requireAdmin(CurrentUser user) {
        if (!user.isAdmin()) {
            throw new AccessDeniedBusinessException("Opération réservée au rôle ADMIN (backoffice)");
        }
    }
}
