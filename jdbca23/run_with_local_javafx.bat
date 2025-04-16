@echo off
echo Exécution du projet avec le SDK JavaFX local

set JAVAFX_PATH=C:\Users\selmi\Downloads\openjfx-24_windows-x64_bin-sdk\javafx-sdk-24\lib

REM Vérifier si le chemin JavaFX existe
if not exist "%JAVAFX_PATH%" (
    echo Le chemin vers JavaFX n'existe pas: %JAVAFX_PATH%
    echo Veuillez vérifier le chemin dans le script.
    pause
    exit /b 1
)

REM Compiler le projet
echo Compilation du projet...
call mvn clean package

if %ERRORLEVEL% NEQ 0 (
    echo Erreur lors de la compilation du projet.
    pause
    exit /b 1
)

REM Exécuter le projet avec le module-path local
echo Exécution du projet...
java --module-path "%JAVAFX_PATH%" ^
     --add-modules javafx.controls,javafx.fxml ^
     -cp target/jdbca23-1.0-SNAPSHOT.jar ^
     esprit.tn.main.MainFX

if %ERRORLEVEL% NEQ 0 (
    echo Erreur lors de l'exécution du projet.
    pause
    exit /b 1
)

echo Exécution terminée.
pause 