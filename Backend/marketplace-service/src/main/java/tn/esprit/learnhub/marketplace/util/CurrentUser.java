package tn.esprit.learnhub.marketplace.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Identité de l'appelant, reconstruite à partir des en-têtes posés par l'API Gateway
 * après validation du JWT Keycloak : X-User-Id, X-User-Roles (ex. "STUDENT,ADMIN"), X-User-Email.
 */
public record CurrentUser(String id, Set<String> roles, String email) {

    public static final String HEADER_ID = "X-User-Id";
    public static final String HEADER_ROLES = "X-User-Roles";
    public static final String HEADER_EMAIL = "X-User-Email";

    public static CurrentUser of(String id, String rolesHeader, String email) {
        Set<String> roles = rolesHeader == null ? Set.of()
                : Arrays.stream(rolesHeader.split("[,\\s]+"))
                .map(r -> r.replace("ROLE_", "").trim().toUpperCase())
                .filter(r -> !r.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
        return new CurrentUser(id, roles, email);
    }

    public boolean isAdmin() {
        return roles.contains("ADMIN");
    }
}
