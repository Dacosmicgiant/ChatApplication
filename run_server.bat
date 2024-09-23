@echo off
REM Navigate to project root
cd /d %~dp0

REM Compile Server
javac -d bin -cp lib\gson-2.8.9.jar src\com\chatapp\server\*.java src\com\chatapp\common\*.java
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)

REM Run Server
java -cp ".;lib\gson-2.8.9.jar" com.chatapp.server.ChatServer
pause
