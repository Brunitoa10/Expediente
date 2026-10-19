#!/bin/sh
# Inicia el juego (Linux / macOS). Requiere Java 17 o superior.
cd "$(dirname "$0")/dist" && exec java -jar ExpedientesDetective.jar
