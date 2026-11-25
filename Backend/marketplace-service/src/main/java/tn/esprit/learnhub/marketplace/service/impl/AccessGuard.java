package tn.esprit.learnhub.marketplace.service.impl;

import tn.esprit.learnhub.marketplace.entity.Order;
import tn.esprit.learnhub.marketplace.exception.AccessDeniedBusinessException;
import tn.esprit.learnhub.marketplace.util.CurrentUser;

/** Un étudiant ne voit que ses propres commandes ; l'ADMIN voit tout (backoffice). */
final class AccessGuard {

    private AccessGuard() {
    }

    static void checkOwnerOrAdmin(Order order, CurrentUser user) {
        if (user == null || user.isAdmin()) return;
        if (!order.getStudentId().equals(user.id())) {
            throw new AccessDeniedBusinessException(
                    "Accès refusé : la commande #" + order.getId() + " appartient à un autre étudiant");
        }
    }
}
