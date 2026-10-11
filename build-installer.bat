@echo off
setlocal
cd /d "%~dp0"

if not exist "dist\GestionEtudiants\GestionEtudiants.exe" goto :noexe

set ISCC=
if exist "%ProgramFiles(x86)%\Inno Setup 6\ISCC.exe" set ISCC=%ProgramFiles(x86)%\Inno Setup 6\ISCC.exe
if exist "%ProgramFiles%\Inno Setup 6\ISCC.exe" set ISCC=%ProgramFiles%\Inno Setup 6\ISCC.exe
if exist "%LOCALAPPDATA%\Programs\Inno Setup 6\ISCC.exe" set ISCC=%LOCALAPPDATA%\Programs\Inno Setup 6\ISCC.exe
if "%ISCC%"=="" goto :noiscc

"%ISCC%" installer.iss
if errorlevel 1 goto :err

echo.
echo TERMINE: dist\Setup-GestionEtudiants-1.0.0.exe
echo Cest ce fichier unique que tu donnes au client.
exit /b 0

:noexe
echo Lance dabord build-exe.bat pour creer dist\GestionEtudiants
exit /b 1

:noiscc
echo Inno Setup 6 introuvable. Installe-le depuis https://jrsoftware.org/isdl.php puis relance.
exit /b 1

:err
echo ECHEC de la creation de linstalleur.
exit /b 1
