@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
set JAVAFX_PATH=C:\Users\selmi\Downloads\openjfx-22.0.2_windows-x64_bin-sdk\javafx-sdk-22.0.2\lib

if not exist "target\classes" mkdir target\classes

javac -d target/classes -cp "%JAVAFX_PATH%\javafx.controls.jar;%JAVAFX_PATH%\javafx.fxml.jar;%JAVAFX_PATH%\javafx.graphics.jar;%JAVAFX_PATH%\javafx.base.jar" src/main/java/esprit/tn/entities/*.java src/main/java/esprit/tn/services/*.java src/main/java/esprit/tn/main/*.java

xcopy /Y /I src\main\resources\*.fxml target\classes\

pause 