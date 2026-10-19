@echo off
rem Crea una carpeta portable con Java incluido (no requiere Java instalado en la PC destino).
rem Necesita un JDK 17+ en ESTA PC (jpackage viene incluido en el JDK).
cd /d "%~dp0"
if not exist dist\ExpedientesDetective.jar call compilar.bat
if exist portable rmdir /s /q portable
jpackage --type app-image --name ExpedientesDetective --input dist --main-jar ExpedientesDetective.jar ^
  --main-class com.detective.app.Main --dest portable --app-version 1.0.0 ^
  --vendor "Expedientes" --java-options "-Dfile.encoding=UTF-8"
echo.
echo Listo: portable\ExpedientesDetective\ExpedientesDetective.exe
echo Copia la carpeta completa a un pendrive: los datos se guardan junto al programa.
