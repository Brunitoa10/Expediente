@echo off
rem Compila y empaqueta SIN Maven (sólo necesita un JDK 17+). Resultado: dist\ExpedientesDetective.jar
setlocal
cd /d "%~dp0"
if exist build rmdir /s /q build
mkdir build\classes build\lib
dir /s /b src\main\java\*.java > build\fuentes.txt
javac -encoding UTF-8 --release 17 -d build\classes @build\fuentes.txt || goto :error
xcopy /e /i /q /y src\main\resources build\classes >nul
cd build\lib
jar xf ..\..\lib\sqlite-jdbc-3.46.1.3.jar || goto :error
if exist META-INF\MANIFEST.MF del /q META-INF\MANIFEST.MF
xcopy /e /i /q /y . ..\classes >nul
cd ..\..
if not exist dist mkdir dist
echo Main-Class: com.detective.app.Main> build\manifest.txt
jar cfm dist\ExpedientesDetective.jar build\manifest.txt -C build\classes . || goto :error
echo.
echo Listo: dist\ExpedientesDetective.jar
echo Verificando consistencia de los casos...
java -cp dist\ExpedientesDetective.jar com.detective.herramientas.VerificadorCasos
goto :eof
:error
echo Hubo un error de compilacion.
exit /b 1
