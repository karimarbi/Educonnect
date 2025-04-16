@echo off
echo Configuration d'IntelliJ IDEA pour le projet jdbca23

REM Vérifier si IntelliJ IDEA est installé
where idea64.exe >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo IntelliJ IDEA n'est pas installé ou n'est pas dans le PATH.
    echo Veuillez installer IntelliJ IDEA depuis https://www.jetbrains.com/idea/download/
    pause
    exit /b 1
)

REM Vérifier si Scene Builder est installé
where scenebuilder >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Scene Builder n'est pas installé ou n'est pas dans le PATH.
    echo Veuillez installer Scene Builder depuis https://gluonhq.com/products/scene-builder/
    pause
    exit /b 1
)

REM Vérifier si Java est installé
java -version >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Java n'est pas installé ou n'est pas dans le PATH.
    echo Veuillez installer Java 17 depuis https://www.oracle.com/java/technologies/downloads/#java17
    pause
    exit /b 1
)

REM Vérifier si Maven est installé
mvn -version >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Maven n'est pas installé ou n'est pas dans le PATH.
    echo Veuillez installer Maven depuis https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

REM Vérifier si MySQL est installé
mysql --version >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo MySQL n'est pas installé ou n'est pas dans le PATH.
    echo Veuillez installer MySQL depuis https://dev.mysql.com/downloads/mysql/
    pause
    exit /b 1
)

echo Toutes les dépendances sont installées.

REM Ouvrir le projet dans IntelliJ IDEA
echo Ouverture du projet dans IntelliJ IDEA...
idea64.exe .

echo Configuration terminée.
echo Veuillez suivre les instructions dans le fichier README.md pour configurer le projet dans IntelliJ IDEA.
pause 