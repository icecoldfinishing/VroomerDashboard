# Vroomer
Application de réservation de voitures. Front statique (Carbook), backend Spring Boot + PostgreSQL.

## Déploiement sur Render (Free Web Service)
- Type: Web Service (runtime natif, pas Blueprint)
- Build command:

```bash
mvn -q -DskipTests -f ./pom.xml clean package
```

- Start command:

```bash
java -jar project/target/vroomer-1.0.0.jar
```

- Base URL: Render attribue `PORT`; l'app écoute `server.port=${PORT:8088}`.
- Base de données: Lier un Managed PostgreSQL. L'app utilisera automatiquement `PGHOST`, `PGPORT`, `PGUSER`, `PGPASSWORD`, `PGDATABASE`.

### Variables d'environnement (optionnel en local)
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` (fallbacks locaux si PG* absents).

### Alternative (Docker)
- Un `Dockerfile` est fourni pour un déploiement Docker. Sur Render, vous pouvez créer un Web Service Docker qui build et lance le jar.

### Structure
- Module `framework`: code historique (non utilisé au runtime Spring Boot).
- Module `project`: application Spring Boot (jar) avec ressources statiques.
