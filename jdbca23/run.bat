@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
set JAVAFX_PATH=C:\Users\selmi\Downloads\openjfx-22.0.2_windows-x64_bin-sdk\javafx-sdk-22.0.2\lib

java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp target/classes esprit.tn.main.MainFX

pause 