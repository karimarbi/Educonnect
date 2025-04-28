@echo off
echo Compilation et exécution du projet jdbca23

REM Vérifier si Maven est installé
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Maven n'est pas installé ou n'est pas dans le PATH.
    echo Veuillez installer Maven depuis https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

REM Nettoyer le cache Maven et forcer le téléchargement des dépendances
echo Nettoyage du cache Maven...
call mvn dependency:purge-local-repository -DreResolve=false

REM Compiler le projet avec un clean complet
echo Compilation du projet...
call mvn clean install -U

if %ERRORLEVEL% NEQ 0 (
    echo Erreur lors de la compilation du projet.
    pause
    exit /b 1
)

REM Exécuter le projet
echo Exécution du projet...
call mvn javafx:run

if %ERRORLEVEL% NEQ 0 (
    echo Erreur lors de l'exécution du projet.
    pause
    exit /b 1
)

echo Exécution terminée.
pause 