@echo off
REM This script is used to compile and run the ChatServer for the chat application.

REM Navigate to the project root directory (where the script is located)
cd /d %~dp0

REM Compile the Java source files located in the server and common packages.
REM The output class files will be placed in the 'bin' directory.
REM The Gson library (gson-2.8.9.jar) is included in the classpath for JSON parsing.
javac -d bin -cp lib\gson-2.8.9.jar src\com\chatapp\server\*.java src\com\chatapp\common\*.java

REM Check if the compilation was successful. If an error occurred, display a failure message.
if errorlevel 1 (
    echo Compilation failed.
    REM Pause the script so the user can see the error message before the script exits.
    pause
    REM Exit the script with a non-zero status indicating failure.
    exit /b 1
)

REM Run the compiled ChatServer class from the 'bin' directory.
REM The Gson library is also included in the runtime classpath.
java -cp "bin;lib\gson-2.8.9.jar" com.chatapp.server.ChatServer

REM Pause the script to keep the console window open after the server is started.
pause
