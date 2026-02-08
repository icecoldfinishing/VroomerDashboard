#!/usr/bin/env bash
set -euo pipefail

# Render Web Service (Free): Run Spring Boot JAR on $PORT
# Spring Boot inclut Tomcat embarqué, pas besoin de télécharger Tomcat

# Render injecte PORT, l'app utilise ${PORT:8089} via application.properties
exec java -jar project/target/andco-0.0.1-SNAPSHOT.jar
