@echo off
setlocal
cd /d "%~dp0"

echo ==== 1/3  Build Maven (jar) ====
if exist mvnw.cmd (
  call mvnw.cmd clean package -DskipTests
) else (
  call mvn clean package -DskipTests
)
if errorlevel 1 goto :err

set JAR=
for %%f in (target\*.jar) do set JAR=%%~nxf
if "%JAR%"=="" (
  echo Aucun jar trouve dans target\
  goto :err
)
echo Jar: %JAR%

where jpackage >nul 2>nul
if errorlevel 1 (
  echo jpackage introuvable: installe un JDK 21 et verifie JAVA_HOME\bin dans le PATH.
  goto :err
)

echo ==== 2/3  jpackage (GestionEtudiants.exe + Java embarque) ====
rmdir /s /q target\jpackage-input 2>nul
mkdir target\jpackage-input
copy "target\%JAR%" target\jpackage-input\ >nul
rmdir /s /q dist 2>nul

set ICON=
if exist app.ico set ICON=--icon app.ico

jpackage --type app-image ^
  --name GestionEtudiants ^
  --app-version 1.0.0 ^
  --vendor "Association El Kheir" ^
  --input target\jpackage-input ^
  --main-jar %JAR% ^
  --main-class org.springframework.boot.loader.launch.JarLauncher ^
  --dest dist ^
  --java-options "-Xmx512m" ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --add-modules java.base,java.desktop,java.instrument,java.logging,java.management,java.naming,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.security.sasl,java.sql,java.transaction.xa,java.xml,jdk.crypto.ec,jdk.localedata,jdk.unsupported ^
  %ICON%
if errorlevel 1 goto :err

echo ==== 3/3  Archive ZIP ====
powershell -NoProfile -Command "Compress-Archive -Path 'dist\GestionEtudiants' -DestinationPath 'dist\GestionEtudiants.zip' -Force"

echo.
echo TERMINE:
echo   Dossier : dist\GestionEtudiants\GestionEtudiants.exe
echo   Archive : dist\GestionEtudiants.zip  (a copier chez le client)
exit /b 0

:err
echo.
echo ECHEC - lis le message ci-dessus.
exit /b 1
