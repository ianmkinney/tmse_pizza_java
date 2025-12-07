#!/bin/bash
# macOS launcher script for TMSE Pizza Application
# This script checks for Java, downloads JavaFX if needed, and runs the application

echo "========================================"
echo "TMSE Pizza Application Launcher"
echo "========================================"
echo ""

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo ""
    echo "Please install Java 21 or higher:"
    echo "  Option 1: Homebrew: brew install openjdk@21"
    echo "  Option 2: Download from: https://adoptium.net/temurin/releases/"
    echo ""
    read -p "Press Enter to exit..."
    exit 1
fi

echo "[OK] Java found"
java -version
echo ""

# Set application paths
JAR_FILE="$SCRIPT_DIR/TMSE_Pizza.jar"
JAVAFX_DIR="$SCRIPT_DIR/javafx-sdk-21"
JAVAFX_HOME="$HOME/javafx-sdk-21"
JAVAFX_DOWNLOADS="$HOME/Downloads/javafx-sdk-21"

# Detect architecture
ARCH=$(uname -m)
if [ "$ARCH" = "arm64" ]; then
    JAVAFX_URL="https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_osx-aarch64_bin-sdk.zip"
    JAVAFX_ZIP="$SCRIPT_DIR/javafx-sdk-osx-aarch64.zip"
else
    JAVAFX_URL="https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_osx-x64_bin-sdk.zip"
    JAVAFX_ZIP="$SCRIPT_DIR/javafx-sdk-osx-x64.zip"
fi

# Check if JAR exists
if [ ! -f "$JAR_FILE" ]; then
    echo "ERROR: TMSE_Pizza.jar not found in $SCRIPT_DIR"
    echo "Please ensure the JAR file is in the same directory as this launcher."
    read -p "Press Enter to exit..."
    exit 1
fi

echo "[OK] JAR file found"
echo ""

# Check if JavaFX exists, download if not
if [ ! -d "$JAVAFX_DIR" ] && [ ! -d "$JAVAFX_HOME" ] && [ ! -d "$JAVAFX_DOWNLOADS" ]; then
    echo "JavaFX not found. Downloading JavaFX 21..."
    echo "This may take a few minutes..."
    echo ""
    
    # Download JavaFX
    if command -v curl &> /dev/null; then
        curl -L -o "$JAVAFX_ZIP" "$JAVAFX_URL"
    elif command -v wget &> /dev/null; then
        wget -O "$JAVAFX_ZIP" "$JAVAFX_URL"
    else
        echo "ERROR: Neither curl nor wget found. Please download JavaFX manually:"
        echo "  $JAVAFX_URL"
        echo "  Extract to: $JAVAFX_DIR"
        read -p "Press Enter to exit..."
        exit 1
    fi
    
    if [ ! -f "$JAVAFX_ZIP" ]; then
        echo "ERROR: Failed to download JavaFX"
        echo "Please download JavaFX 21 manually from https://openjfx.io/"
        echo "Extract it to: $JAVAFX_DIR"
        read -p "Press Enter to exit..."
        exit 1
    fi
    
    echo "Extracting JavaFX..."
    unzip -q "$JAVAFX_ZIP" -d "$SCRIPT_DIR"
    rm "$JAVAFX_ZIP"
    
    # Rename extracted folder
    if [ -d "$SCRIPT_DIR/javafx-sdk-21.0.2" ]; then
        mv "$SCRIPT_DIR/javafx-sdk-21.0.2" "$JAVAFX_DIR"
    fi
    
    if [ ! -d "$JAVAFX_DIR" ]; then
        echo "ERROR: Failed to extract JavaFX"
        read -p "Press Enter to exit..."
        exit 1
    fi
    
    echo "[OK] JavaFX downloaded and extracted"
    echo ""
else
    echo "[OK] JavaFX found"
    echo ""
fi

# Determine JavaFX path
if [ -d "$JAVAFX_DIR" ]; then
    JAVAFX_PATH="$JAVAFX_DIR/lib"
elif [ -d "$JAVAFX_HOME" ]; then
    JAVAFX_PATH="$JAVAFX_HOME/lib"
elif [ -d "$JAVAFX_DOWNLOADS" ]; then
    JAVAFX_PATH="$JAVAFX_DOWNLOADS/lib"
fi

# Run the application
echo "Starting TMSE Pizza Application..."
echo ""

java --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml -jar "$JAR_FILE"

if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: Application failed to start"
    echo ""
    echo "Troubleshooting:"
    echo "1. Make sure Java 21 or higher is installed"
    echo "2. Check that JavaFX was downloaded correctly"
    echo "3. Try running manually:"
    echo "   java --module-path \"$JAVAFX_PATH\" --add-modules javafx.controls,javafx.fxml -jar \"$JAR_FILE\""
    echo ""
    read -p "Press Enter to exit..."
fi

