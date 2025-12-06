@echo off
REM Build script for TMSE Pizza Java Application (Windows)

echo Building TMSE Pizza Application...

REM Create build directory
if not exist build mkdir build

REM Compile Java files
echo Compiling Java source files...
javac -d build -encoding UTF-8 src\com\tmse\pizza\models\*.java src\com\tmse\pizza\storage\*.java src\com\tmse\pizza\gui\*.java src\com\tmse\pizza\*.java

if errorlevel 1 (
    echo Compilation failed! Make sure JavaFX is installed or in classpath.
    pause
    exit /b 1
)

REM Create JAR file
echo Creating JAR file...
jar cvfm TMSE_Pizza.jar MANIFEST.MF -C build .

echo.
echo Build complete!
echo.
echo To run the application:
echo   java -jar TMSE_Pizza.jar
echo.
echo If you get JavaFX errors, use:
echo   java --module-path C:\path\to\javafx\lib --add-modules javafx.controls,javafx.fxml -jar TMSE_Pizza.jar
echo.

pause

