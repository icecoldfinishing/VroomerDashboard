# Déploiement du projet Vroomer sur Render (Free)

Ce guide explique comment déployer ce projet Spring Boot sur [Render](https://render.com) gratuitement.

## Architecture du projet

- **Framework maison** (`framework/`) : ModelView, JsonResponse
- **Application Spring Boot** (`project/`) : JAR exécutable avec Tomcat embarqué
- **Base de données** : PostgreSQL (Render Managed PostgreSQL)

## 1. Prérequis

- Un compte Render (https://render.com)
- Un repository GitHub contenant ce projet
- Le projet génère un JAR Spring Boot via Maven

## 2. Préparer le projet

Vérifiez que le build Maven fonctionne localement :

```bash
cd D:\L3\GProjet\Vroomer
mvn clean install -DskipTests
java -jar project/target/vroomer-1.0.0.jar
```

L'application doit démarrer sur http://localhost:8088

## 3. Créer une base PostgreSQL sur Render

1. Aller sur https://dashboard.render.com
2. Cliquer **New** → **PostgreSQL**
3. Configurer :
   - **Name** : `vroomer-db`
   - **Database** : `vroomer_db`
   - **PostgreSQL Version** : 16 ou 17 (selon disponibilité)
   - **Plan** : Free
4. Après création, aller dans **Info** et copier :
   - **Hostname** (ex: `dpg-xxxxx.oregon-postgres.render.com`)
   - **Port** : `5432`
   - **Database** : `vroomer_db`
   - **Username** : *(généré par Render, ex: `vroomer_db_user`)*
   - **Password** : *(généré par Render)*

> ⚠️ **Note** : Render génère automatiquement le username et password. Ce n'est PAS `postgres/postgres` comme en local !

## 4. Créer le Web Service sur Render

1. Cliquer **New** → **Web Service**
2. Connecter votre repository GitHub
3. Configurer :

| Paramètre | Valeur |
|-----------|--------|
| **Name** | `vroomer` |
| **Root Directory** | `.` (racine) |
| **Environment** | `Java` |
| **Build Command** | `./render-build.sh` |
| **Start Command** | `./render-start.sh` |
| **Plan** | Free |

## 5. Variables d'environnement

Ajouter ces variables dans **Environment** :

| Variable | Valeur |
|----------|--------|
| `PGHOST` | *(depuis votre DB Render)* |
| `PGPORT` | `5432` |
| `PGDATABASE` | `vroomer_db` |
| `PGUSER` | *(depuis votre DB Render)* |
| `PGPASSWORD` | *(depuis votre DB Render)* |

> L'application utilise automatiquement ces variables grâce à `application.properties` :
> ```properties
> spring.datasource.url=jdbc:postgresql://${PGHOST:localhost}:${PGPORT:5432}/${PGDATABASE:vroomer_db}
> spring.datasource.username=${PGUSER:postgres}
> spring.datasource.password=${PGPASSWORD:postgres}
> server.port=${PORT:8088}
> ```

## 6. Initialiser la base de données

Après déploiement, exécuter le script SQL pour créer les tables :

1. Aller dans votre PostgreSQL sur Render
2. Cliquer **PSQL** ou utiliser un client externe
3. Exécuter le contenu de `bd/init_tables.sql`

## 7. Déployer

Cliquer **Deploy** sur Render. Le build va :

1. Installer Maven (si nécessaire)
2. Compiler le framework + project (`mvn install`)
3. Générer `project/target/vroomer-1.0.0.jar`
4. Lancer avec `java -jar` sur le port assigné par Render

## Scripts de déploiement

### render-build.sh
```bash
#!/usr/bin/env bash
set -euo pipefail
# Installe Maven si absent, puis build le projet
mvn -q -DskipTests -f ./pom.xml clean install
```

### render-start.sh
```bash
#!/usr/bin/env bash
set -euo pipefail
# Lance le JAR Spring Boot (Tomcat embarqué)
exec java -jar project/target/vroomer-1.0.0.jar
```

## Avantages de Spring Boot vs WAR/Tomcat

| Avant (WAR) | Maintenant (Spring Boot JAR) |
|-------------|------------------------------|
| Télécharger Tomcat | Tomcat embarqué dans le JAR |
| Configurer server.xml | Configuration via properties |
| Déployer WAR manuellement | `java -jar` suffit |
| ~200MB download | Rien à télécharger |

## Dépannage

### Build échoue "Could not find artifact framework"
Le script `render-build.sh` utilise `mvn install` qui compile et installe le framework avant le project.

### L'application ne démarre pas
Vérifier les logs Render pour les erreurs de connexion DB. Les variables `PG*` doivent être correctement configurées.

### Port incorrect
Render injecte la variable `PORT`. L'application l'utilise via `server.port=${PORT:8088}`.

## Liens utiles

- [Render Java Docs](https://render.com/docs/deploy-java)
- [Spring Boot on Render](https://render.com/docs/deploy-spring-boot)
