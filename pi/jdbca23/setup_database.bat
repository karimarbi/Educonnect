@echo off
echo Configuration de la base de données MySQL pour le projet jdbca23

REM Vérifier si MySQL est installé
where mysql >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo MySQL n'est pas installé ou n'est pas dans le PATH.
    echo Veuillez installer MySQL depuis https://dev.mysql.com/downloads/mysql/
    pause
    exit /b 1
)

REM Exécuter le script SQL
echo Exécution du script SQL...
mysql -u root < setup_database.sql

if %ERRORLEVEL% NEQ 0 (
    echo Erreur lors de l'exécution du script SQL.
    echo Veuillez vérifier que MySQL est en cours d'exécution et que l'utilisateur root n'a pas de mot de passe.
    pause
    exit /b 1
)

echo Configuration de la base de données terminée.
pause 