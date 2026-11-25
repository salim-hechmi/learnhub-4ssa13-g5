export const environment = {
  production: false,
  /** Point d'entrée UNIQUE du front : l'API Gateway. */
  apiGatewayUrl: 'http://localhost:8080',
  /**
   * Test SANS Gateway ni Keycloak (marketplace-service lancé en profil standalone) :
   *   apiGatewayUrl: 'http://localhost:8085',
   *   devIdentity: { userId: 'admin-0000-4000-8000-000000000099', roles: 'ADMIN', email: 'admin@learnhub.tn' }
   */
  devIdentity: null as null | { userId: string; roles: string; email: string }
};
