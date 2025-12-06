#!/bin/bash

# Build script for TMSE Pizza Java Application

echo "Building TMSE Pizza Application..."

# Create build directory
mkdir -p build

# Compile Java files
echo "Compiling Java source files..."

# Try to find JavaFX
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAVAFX_17="$HOME/javafx-sdk-17/lib"
JAVAFX_25_LOCAL="$SCRIPT_DIR/javafx-sdk-25.0.1/lib"
JAVAFX_25_HOME="$HOME/Downloads/javafx-sdk-25.0.1/lib"

if [ -d "$JAVAFX_17" ]; then
    echo "Found JavaFX 17, compiling..."
    javac -d build \
        --module-path "$JAVAFX_17" \
        --add-modules javafx.controls,javafx.fxml \
        src/com/tmse/pizza/models/*.java \
        src/com/tmse/pizza/storage/*.java \
        src/com/tmse/pizza/gui/*.java \
        src/com/tmse/pizza/*.java
elif [ -d "$JAVAFX_25_LOCAL" ]; then
    echo "Found JavaFX 25.0.1 in project directory, compiling..."
    javac -d build \
        --module-path "$JAVAFX_25_LOCAL" \
        --add-modules javafx.controls,javafx.fxml \
        src/com/tmse/pizza/models/*.java \
        src/com/tmse/pizza/storage/*.java \
        src/com/tmse/pizza/gui/*.java \
        src/com/tmse/pizza/*.java
elif [ -d "$JAVAFX_25_HOME" ]; then
    echo "Found JavaFX 25.0.1 in Downloads..."
    echo "WARNING: JavaFX 25 requires Java 23, but you have Java 17"
    echo "Attempting to compile anyway (may fail)..."
    javac -d build \
        --module-path "$JAVAFX_25_HOME" \
        --add-modules javafx.controls,javafx.fxml \
        src/com/tmse/pizza/models/*.java \
        src/com/tmse/pizza/storage/*.java \
        src/com/tmse/pizza/gui/*.java \
        src/com/tmse/pizza/*.java
else
    echo "JavaFX not found!"
    echo "Please download JavaFX 17 from https://openjfx.io/ and extract to ~/javafx-sdk-17"
    echo "Or install Java 23 to use JavaFX 25.0.1"
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

