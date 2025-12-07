@echo off
REM Windows launcher script for TMSE Pizza Application
REM This script checks for Java, downloads JavaFX if needed, and runs the application

setlocal enabledelayedexpansion

echo ========================================
echo TMSE Pizza Application Launcher
echo ========================================
echo.

REM Check if Java is installed
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java is not installed or not in PATH
    echo.
    echo Please install Java 21 or higher from:
    echo https://adoptium.net/temurin/releases/
    echo.
    pause
    exit /b 1
)

echo [OK] Java found
java -version
echo.

REM Set application directory
set APP_DIR=%~dp0
set JAR_FILE=%APP_DIR%TMSE_Pizza.jar
set JAVAFX_DIR=%APP_DIR%javafx-sdk-21
set JAVAFX_URL=https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_windows-x64_bin-sdk.zip
set JAVAFX_ZIP=%APP_DIR%javafx-sdk.zip

REM Check if JAR exists
if not exist "%JAR_FILE%" (
    echo ERROR: TMSE_Pizza.jar not found in %APP_DIR%
    echo Please ensure the JAR file is in the same directory as this launcher.
    pause
    exit /b 1
)

echo [OK] JAR file found
echo.

REM Check if JavaFX exists, download if not
if not exist "%JAVAFX_DIR%" (
    echo JavaFX not found. Downloading JavaFX 21...
    echo This may take a few minutes...
    echo.
    
    REM Try to download using PowerShell
    powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%JAVAFX_URL%' -OutFile '%JAVAFX_ZIP%'}"
    
    if not exist "%JAVAFX_ZIP%" (
        echo ERROR: Failed to download JavaFX
        echo Please download JavaFX 21 manually from https://openjfx.io/
        echo Extract it to: %JAVAFX_DIR%
        pause
        exit /b 1
    )
    
    echo Extracting JavaFX...
    powershell -Command "Expand-Archive -Path '%JAVAFX_ZIP%' -DestinationPath '%APP_DIR%' -Force"
    del "%JAVAFX_ZIP%"
    
    REM Rename extracted folder
    if exist "%APP_DIR%javafx-sdk-21.0.2" (
        move "%APP_DIR%javafx-sdk-21.0.2" "%JAVAFX_DIR%" >nul
    )
    
    if not exist "%JAVAFX_DIR%" (
        echo ERROR: Failed to extract JavaFX
        pause
        exit /b 1
    )
    
    echo [OK] JavaFX downloaded and extracted
    echo.
) else (
    echo [OK] JavaFX found
    echo.
)

REM Run the application
echo Starting TMSE Pizza Application...
echo.

java --module-path "%JAVAFX_DIR%\lib" --add-modules javafx.controls,javafx.fxml -jar "%JAR_FILE%"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Application failed to start
    echo.
    echo Troubleshooting:
    echo 1. Make sure Java 21 or higher is installed
    echo 2. Check that JavaFX was downloaded correctly
    echo 3. Try running manually:
    echo    java --module-path "%JAVAFX_DIR%\lib" --add-modules javafx.controls,javafx.fxml -jar "%JAR_FILE%"
    echo.
    pause
)

endlocal

