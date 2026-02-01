#!/usr/bin/env bash
set -euo pipefail

# Render Web Service (Free): Run Spring Boot JAR on $PORT
# Spring Boot inclut Tomcat embarqué, pas besoin de télécharger Tomcat

# Render injecte PORT, l'app utilise ${PORT:8088} via application.properties
exec java -jar project/target/vroomer-1.0.0.jar
