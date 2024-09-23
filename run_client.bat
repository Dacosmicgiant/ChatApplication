@echo off
REM Navigate to project root
cd /d %~dp0

REM Compile Client
javac --module-path lib\javafx-sdk-17.0.12\lib --add-modules javafx.controls,javafx.fxml -d bin -cp lib\gson-2.8.9.jar src\com\chatapp\client\*.java src\com\chatapp\common\*.java
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)

REM Run Client
java --module-path lib\javafx-sdk-17.0.12\lib --add-modules javafx.controls,javafx.fxml -cp ".;lib\gson-2.8.9.jar" com.chatapp.client.ChatGUI
pause
