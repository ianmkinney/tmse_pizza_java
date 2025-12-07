#!/bin/bash
# Complete packaging script for macOS distribution
# Creates a folder with launcher, JAR, and instructions

echo "=========================================="
echo "TMSE Pizza : macOS Package Creator"
echo "=========================================="
echo ""

# Create distribution directory
DIST_DIR="TMSE_Pizza_macOS"
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
cp create-macos-launcher.command "$DIST_DIR/TMSE_Pizza.command"

# Make launcher executable
chmod +x "$DIST_DIR/TMSE_Pizza.command"

# Create a simple README
cat > "$DIST_DIR/README.txt" << 'EOF'
TMSE Pizza : macOS Installation Guide
======================================

QUICK START:
1. Double-click "TMSE_Pizza.command" to run
2. The launcher will automatically download JavaFX if needed
3. Make sure Java 21+ is installed (download from https://adoptium.net/)

REQUIREMENTS:
- macOS 10.14 (Mojave) or later
- Java 21 or higher
- Internet connection (for first-time JavaFX download)

TROUBLESHOOTING:
- If you get "Java not found": Install Java from https://adoptium.net/
- If you get "permission denied": Right-click the .command file and select "Open"
- If JavaFX download fails: Download manually from https://openjfx.io/
  Extract to: javafx-sdk-21 folder in this directory

CREATING AN .APP BUNDLE:
To create a native macOS .app bundle:
1. Run: ./create-macos-app.sh
2. This creates a .app bundle in dist/ directory
3. The .app can be distributed like any macOS application

CREATING A .DMG INSTALLER:
1. First create the .app bundle: ./create-macos-app.sh
2. Then create the .dmg: ./create-macos-dmg.sh
3. The .dmg file can be distributed to users

For more details, see README_MACOS.md
EOF

echo ""
echo "Package created in: $DIST_DIR/"
echo ""
echo "Contents:"
ls -lh "$DIST_DIR/"
echo ""
echo "To create an .app bundle:"
echo "  ./create-macos-app.sh"
echo ""
echo "To create a .dmg installer:"
echo "  1. First run: ./create-macos-app.sh"
echo "  2. Then run: ./create-macos-dmg.sh"
echo ""
echo "Package is ready for distribution!"

