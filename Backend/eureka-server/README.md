# eureka-server — annuaire de services LearnHub

Serveur **Netflix Eureka** (Spring Cloud). Chaque microservice s'y enregistre au démarrage avec son nom, son adresse et son port, puis envoie un signal de vie (heartbeat) toutes les 10 secondes. Les autres services le consultent pour trouver une instance disponible, au lieu d'écrire des adresses en dur.

## Deux instances répliquées

| Instance | Profil | URL du dashboard |
|---|---|---|
| peer1 | `peer1` | http://localhost:8761 |
| peer2 | `peer2` | http://127.0.0.1:8762 |

- Chaque serveur s'enregistre chez l'autre et lui **réplique son registre** : un service enregistré sur l'un apparaît aussi sur l'autre.
- Les clients déclarent les deux adresses (`defaultZone: http://localhost:8761/eureka/,http://127.0.0.1:8762/eureka/`). Si un serveur tombe, ils continuent avec l'autre.
- Pourquoi `localhost` pour l'un et `127.0.0.1` pour l'autre ? Eureka ignore un pair qui a **le même nom d'hôte** que lui-même. Avec deux noms différents pour la même machine, la réplication marche sans modifier le fichier `hosts`, donc sur le PC de chaque membre.

## Lancer

```bash
mvn -DskipTests package
java -jar target/eureka-server-1.0.0.jar --spring.profiles.active=peer1
java -jar target/eureka-server-1.0.0.jar --spring.profiles.active=peer2
```

Ou tout d'un coup avec `Backend/start-all.ps1` (Eureka + marketplace).

## Rendre un microservice client Eureka

1. Dépendance `spring-cloud-starter-netflix-eureka-client` (+ `spring-cloud-dependencies` 2023.0.3 dans `dependencyManagement`).
2. Dans `application.yml` :

```yaml
spring:
  application:
    name: mon-service            # nom affiché dans le dashboard
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/,http://127.0.0.1:8762/eureka/
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}   # distingue 2 instances du même service
```

3. Deuxième instance : relancer le même JAR avec `--server.port=<autre port>`.

## Observabilité

Actuator est activé : `/actuator/health`, `/actuator/info`, `/actuator/metrics`, `/actuator/env`, `/actuator/beans`, `/actuator/mappings`, `/actuator/loggers`.
