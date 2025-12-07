#!/bin/bash

# Build script for TMSE Pizza Java Application

echo "Building TMSE Pizza Application..."

# Create build directory
mkdir -p build

# Compile Java files
echo "Compiling Java source files..."

# Try to find JavaFX
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAVAFX_21="$HOME/javafx-sdk-21/lib"
JAVAFX_21_LOCAL="$SCRIPT_DIR/javafx-sdk-21/lib"
JAVAFX_21_DOWNLOADS="$HOME/Downloads/javafx-sdk-21/lib"

if [ -d "$JAVAFX_21" ]; then
    echo "Found JavaFX 21, compiling..."
    javac -d build \
        --module-path "$JAVAFX_21" \
        --add-modules javafx.controls,javafx.fxml \
        src/com/tmse/pizza/models/*.java \
        src/com/tmse/pizza/storage/*.java \
        src/com/tmse/pizza/gui/*.java \
        src/com/tmse/pizza/*.java
elif [ -d "$JAVAFX_21_LOCAL" ]; then
    echo "Found JavaFX 21 in project directory, compiling..."
    javac -d build \
        --module-path "$JAVAFX_21_LOCAL" \
        --add-modules javafx.controls,javafx.fxml \
        src/com/tmse/pizza/models/*.java \
        src/com/tmse/pizza/storage/*.java \
        src/com/tmse/pizza/gui/*.java \
        src/com/tmse/pizza/*.java
elif [ -d "$JAVAFX_21_DOWNLOADS" ]; then
    echo "Found JavaFX 21 in Downloads, compiling..."
    javac -d build \
        --module-path "$JAVAFX_21_DOWNLOADS" \
        --add-modules javafx.controls,javafx.fxml \
        src/com/tmse/pizza/models/*.java \
        src/com/tmse/pizza/storage/*.java \
        src/com/tmse/pizza/gui/*.java \
        src/com/tmse/pizza/*.java
else
    echo "JavaFX not found!"
    echo "Please download JavaFX 21 from https://openjfx.io/ and extract to ~/javafx-sdk-21"
    exit 1
fi

if [ $? -ne 0 ]; then
    echo "Compilation failed! Make sure JavaFX is installed or in classpath."
    exit 1
fi

# Create JAR file
echo "Creating JAR file..."
jar cvfm TMSE_Pizza.jar MANIFEST.MF -C build .

echo "Build complete!"
echo ""
echo "To run the application:"
echo "  java -jar TMSE_Pizza.jar"
echo ""
echo "If you get JavaFX errors, use:"
echo "  java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar TMSE_Pizza.jar"

