#!/bin/bash

# Run script for TMSE Pizza Application
# Checks for JavaFX 17 or 25

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAVAFX_17="$HOME/javafx-sdk-17/lib"
JAVAFX_25_LOCAL="$SCRIPT_DIR/javafx-sdk-25.0.1/lib"
JAVAFX_25_HOME="$HOME/Downloads/javafx-sdk-25.0.1/lib"

if [ -d "$JAVAFX_17" ]; then
    JAVAFX_PATH="$JAVAFX_17"
    echo "Using JavaFX 17..."
elif [ -d "$JAVAFX_25_LOCAL" ]; then
    JAVAFX_PATH="$JAVAFX_25_LOCAL"
    echo "Using JavaFX 25.0.1 from project directory..."
elif [ -d "$JAVAFX_25_HOME" ]; then
    JAVAFX_PATH="$JAVAFX_25_HOME"
    echo "Using JavaFX 25.0.1 from Downloads..."
    echo "WARNING: JavaFX 25 requires Java 23. You have Java 17."
    echo "The application may not run correctly. Consider upgrading to Java 23."
else
    echo "JavaFX not found!"
    echo "Please download JavaFX 17 from https://openjfx.io/ and extract to ~/javafx-sdk-17"
    echo ""
    echo "Or specify the path manually:"
    echo "  java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar TMSE_Pizza.jar"
    exit 1
fi

java --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml -jar TMSE_Pizza.jar

