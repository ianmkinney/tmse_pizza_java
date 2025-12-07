#!/bin/bash

# Run script for TMSE Pizza Application
# Checks for JavaFX 21

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAVAFX_21="$HOME/javafx-sdk-21/lib"
JAVAFX_21_LOCAL="$SCRIPT_DIR/javafx-sdk-21/lib"
JAVAFX_21_DOWNLOADS="$HOME/Downloads/javafx-sdk-21/lib"

if [ -d "$JAVAFX_21" ]; then
    JAVAFX_PATH="$JAVAFX_21"
    echo "Using JavaFX 21..."
elif [ -d "$JAVAFX_21_LOCAL" ]; then
    JAVAFX_PATH="$JAVAFX_21_LOCAL"
    echo "Using JavaFX 21 from project directory..."
elif [ -d "$JAVAFX_21_DOWNLOADS" ]; then
    JAVAFX_PATH="$JAVAFX_21_DOWNLOADS"
    echo "Using JavaFX 21 from Downloads..."
else
    echo "JavaFX not found!"
    echo "Please download JavaFX 21 from https://openjfx.io/ and extract to ~/javafx-sdk-21"
    echo ""
    echo "Or specify the path manually:"
    echo "  java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar TMSE_Pizza.jar"
    exit 1
fi

java --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml -jar TMSE_Pizza.jar

