@echo off
echo Configuration complète du projet jdbca23

REM Exécuter le script de configuration d'IntelliJ IDEA
echo Exécution du script de configuration d'IntelliJ IDEA...
call setup_intellij.bat

REM Exécuter le script de configuration de la base de données
echo Exécution du script de configuration de la base de données...
call setup_database.bat

REM Exécuter le script de compilation et d'exécution du projet
echo Exécution du script de compilation et d'exécution du projet...
call run_project.bat

echo Configuration complète terminée.
pause 