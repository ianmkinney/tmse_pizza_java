#!/bin/bash
# Script to create Windows installer using jpackage
# Requires: Java 21+, jpackage tool, WiX Toolset (for .msi) or Inno Setup (for .exe)

echo "Creating Windows installer for TMSE Pizza Application..."
echo ""

# Check if jpackage is available
if ! command -v jpackage &> /dev/null; then
    echo "ERROR: jpackage not found"
    echo "jpackage is included with Java 14+"
    echo "Make sure Java 21+ is installed and in PATH"
    exit 1
fi

# Check if JAR exists
if [ ! -f "TMSE_Pizza.jar" ]; then
    echo "ERROR: TMSE_Pizza.jar not found"
    echo "Please build the application first: ./build.sh"
    exit 1
fi

# Create app name and version
APP_NAME="TMSE Pizza"
APP_VERSION="1.0.0"
MAIN_CLASS="com.tmse.pizza.PizzaApp"

echo "Building Windows installer..."
echo "Application: $APP_NAME"
echo "Version: $APP_VERSION"
echo ""

# Create installer using jpackage
jpackage \
    --input . \
    --name "$APP_NAME" \
    --main-jar TMSE_Pizza.jar \
    --main-class "$MAIN_CLASS" \
    --type exe \
    --app-version "$APP_VERSION" \
    --description "TMSE Pizza Ordering System" \
    --vendor "TMSE" \
    --copyright "2025 TMSE Pizza" \
    --win-dir-chooser \
    --win-menu \
    --win-shortcut \
    --java-options "--module-path javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml" \
    --java-options "-Xmx512m" \
    --dest dist

if [ $? -eq 0 ]; then
    echo ""
    echo "SUCCESS: Installer created in dist/ directory"
    echo "The installer will bundle Java runtime and JavaFX"
else
    echo ""
    echo "ERROR: Failed to create installer"
    echo ""
    echo "Note: jpackage requires:"
    echo "  - Java 21+ with jpackage tool"
    echo "  - For .exe: Inno Setup 6+ (https://jrsoftware.org/isdl.php)"
    echo "  - For .msi: WiX Toolset 3.11+ (https://wixtoolset.org/)"
    exit 1
fi

