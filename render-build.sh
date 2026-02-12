#!/usr/bin/env bash
set -euo pipefail

# Render Web Service (Free): Build Spring Boot JAR via Maven
# Installs Maven if not present, then builds the aggregator (framework + project)

# Ensure Maven is available
if ! command -v mvn >/dev/null 2>&1; then
  MAVEN_VERSION=3.9.6
  echo "Maven not found. Installing Maven ${MAVEN_VERSION}..."
  curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" -o /tmp/maven.tar.gz
  mkdir -p "$HOME/.maven"
  tar -xzf /tmp/maven.tar.gz -C "$HOME/.maven"
  export MAVEN_HOME="$HOME/.maven/apache-maven-${MAVEN_VERSION}"
  export PATH="$MAVEN_HOME/bin:$PATH"
fi

# Build aggregator at repo root to compile all modules (framework + project)
mvn -q -DskipTests -f ./pom.xml clean install

echo "Build completed. JAR at project/target/vroomer-1.0.0.jar"
