@echo off
rem Inicia el juego. Requiere Java 17 o superior (o usá la versión portable sin Java).
cd /d "%~dp0dist"
start "" javaw -jar ExpedientesDetective.jar
