#!/bin/sh
set -e
for s in usuarios equipos prestamos reportes; do
  echo "=== pruebas de $s-service ==="
  docker run --rm -v "$(pwd)/$s-service":/app -w /app maven:3.9-eclipse-temurin-21 mvn -B -q test
done
echo "Todas las pruebas pasaron"
