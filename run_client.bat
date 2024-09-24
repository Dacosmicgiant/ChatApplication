@echo off
REM Disable command echoing to keep the output clean.

REM Navigate to the project root directory
cd /d %~dp0
REM %~dp0 refers to the directory of the batch file, allowing navigation to the script's location.

REM Compile Client Java files
javac --module-path lib\javafx-sdk-17.0.12\lib --add-modules javafx.controls,javafx.fxml -d bin -cp lib\gson-2.8.9.jar src\com\chatapp\client\*.java src\com\chatapp\common\*.java
REM The `javac` command compiles the Java files found in the specified source directories.
REM --module-path specifies the path to the JavaFX libraries.
REM --add-modules indicates the required JavaFX modules for the application.
REM -d specifies the destination directory for compiled class files (bin).
REM -cp specifies the classpath, including Gson library for JSON handling.

REM Check if the compilation was successful
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)
REM If the previous command encountered an error (error level is 1), notify the user, 
REM pause the execution to allow the user to see the message, and exit the script with an error code.

REM Run the Client application
java --module-path lib\javafx-sdk-17.0.12\lib --add-modules javafx.controls,javafx.fxml -cp "bin;lib\gson-2.8.9.jar" com.chatapp.client.ChatGUI
REM The `java` command runs the compiled Client application.
REM --module-path and --add-modules are used again to ensure the JavaFX libraries are available.
REM -cp specifies the classpath to include both the compiled classes and the Gson library.

pause
REM Pause the script to keep the console window open after the application has been run, 
REM allowing the user to see any output or errors.
