#!/bin/bash
# Complete packaging script for Windows distribution
# Creates a folder with launcher, JAR, and instructions

echo "=========================================="
echo "TMSE Pizza - Windows Package Creator"
echo "=========================================="
echo ""

# Create distribution directory
DIST_DIR="TMSE_Pizza_Windows"
rm -rf "$DIST_DIR"
mkdir -p "$DIST_DIR"

# Check if JAR exists
if [ ! -f "TMSE_Pizza.jar" ]; then
    echo "Building application..."
    ./build.sh
    if [ $? -ne 0 ]; then
        echo "ERROR: Build failed"
        exit 1
    fi
fi

echo "Copying files..."
cp TMSE_Pizza.jar "$DIST_DIR/"
cp create-windows-launcher.bat "$DIST_DIR/TMSE_Pizza.bat"
cp README_WINDOWS.md "$DIST_DIR/README.txt"

# Create a simple README
cat > "$DIST_DIR/README.txt" << 'EOF'
TMSE Pizza - Windows Installation Guide
========================================

QUICK START:
1. Double-click "TMSE_Pizza.bat" to run
2. The launcher will automatically download JavaFX if needed
3. Make sure Java 21+ is installed (download from https://adoptium.net/)

REQUIREMENTS:
- Windows 7 or later
- Java 21 or higher
- Internet connection (for first-time JavaFX download)

TROUBLESHOOTING:
- If you get "Java not found": Install Java from https://adoptium.net/
- If JavaFX download fails: Download manually from https://openjfx.io/
  Extract to: javafx-sdk-21 folder in this directory

CREATING AN .EXE FILE:
To convert TMSE_Pizza.bat to an .exe file:
1. Download "Bat To Exe Converter" (free)
2. Load TMSE_Pizza.bat
3. Set "Include JAR" option to embed TMSE_Pizza.jar
4. Compile to create TMSE_Pizza.exe

For more details, see README_WINDOWS.md
EOF

echo ""
echo "Package created in: $DIST_DIR/"
echo ""
echo "Contents:"
ls -lh "$DIST_DIR/"
echo ""
echo "To create an .exe file:"
echo "1. Use Bat To Exe Converter to convert TMSE_Pizza.bat"
echo "2. Or use jpackage (see README_WINDOWS.md)"
echo ""
echo "Package is ready for distribution!"

