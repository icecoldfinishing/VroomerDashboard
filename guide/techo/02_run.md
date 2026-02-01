# Guide de lancement du projet Vroomer

## Prérequis

- **Java 17+** (JDK installé)
- **Maven 3.9+**
- **PostgreSQL** avec une base `vroomer_db` créée

## 1. Créer la base de données

```bash
# Créer la base (si pas encore fait)
psql -U postgres -c "CREATE DATABASE vroomer_db;"

# Initialiser les tables et données
psql -U postgres -d vroomer_db -f bd/init_tables.sql
```

## 2. Build du projet

Le projet utilise un framework maison (`framework/`) + Spring Boot (`project/`).

**Depuis la racine du projet :**

```bash
cd D:\L3\GProjet\Vroomer

# Installer le framework dans le repo Maven local + build du projet
mvn install -DskipTests
```

## 3. Lancer l'application

**Option A : Avec Maven (développement)**

```bash
cd project
mvn spring-boot:run
```

**Option B : Avec le JAR (production)**

```bash
java -jar project/target/vroomer-1.0.0.jar
```

## 4. Accéder à l'application

Ouvrir dans le navigateur : **http://localhost:8088**

## Configuration

La configuration se trouve dans `project/src/main/resources/application.properties` :

| Propriété | Valeur par défaut | Description |
|-----------|-------------------|-------------|
| `server.port` | 8088 | Port du serveur |
| `spring.datasource.url` | localhost:5432/vroomer_db | URL PostgreSQL |
| `spring.datasource.username` | postgres | Utilisateur DB |
| `spring.datasource.password` | postgres | Mot de passe DB |

## Structure

```
Vroomer/
├── framework/          # Framework maison (ModelView, JsonResponse)
├── project/            # Application Spring Boot
│   ├── src/main/java/  # Code Java (controllers, entities, services)
│   └── src/main/resources/
│       ├── static/     # Fichiers HTML/CSS/JS
│       └── application.properties
├── bd/                 # Scripts SQL
└── pom.xml             # POM parent (aggregator)
```

## Dépannage

### Erreur "Could not find artifact framework"

Le framework n'est pas installé. Exécuter depuis la racine :

```bash
mvn install -DskipTests
```

### Erreur "Port 8088 already in use"

Arrêter le processus qui utilise le port :

```powershell
Get-NetTCPConnection -LocalPort 8088 | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```

### Erreur de connexion à la base de données

1. Vérifier que PostgreSQL est démarré
2. Vérifier que la base `vroomer_db` existe
3. Vérifier les credentials dans `application.properties`
