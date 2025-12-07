#!/bin/bash
# Script to create macOS .dmg installer
# Requires: .app bundle (created with create-macos-app.sh) and hdiutil (built-in macOS tool)

echo "Creating macOS .dmg installer for TMSE Pizza Application..."
echo ""

APP_NAME="TMSE Pizza"
APP_BUNDLE="dist/$APP_NAME.app"
DMG_NAME="TMSE_Pizza"
DMG_PATH="dist/${DMG_NAME}.dmg"

# Check if .app bundle exists
if [ ! -d "$APP_BUNDLE" ]; then
    echo "ERROR: $APP_BUNDLE not found"
    echo "Please create the .app bundle first: ./create-macos-app.sh"
    exit 1
fi

# Check if running on macOS
if [[ "$OSTYPE" != "darwin"* ]]; then
    echo "ERROR: This script must be run on macOS"
    exit 1
fi

echo "Creating .dmg installer..."
echo ""

# Create a temporary directory for DMG contents
TEMP_DMG_DIR="dist/dmg_contents"
rm -rf "$TEMP_DMG_DIR"
mkdir -p "$TEMP_DMG_DIR"

# Copy .app bundle to temp directory
cp -R "$APP_BUNDLE" "$TEMP_DMG_DIR/"

# Create Applications symlink
ln -s /Applications "$TEMP_DMG_DIR/Applications"

# Create a README
cat > "$TEMP_DMG_DIR/README.txt" << 'EOF'
TMSE Pizza Installation
=======================

1. Drag "TMSE Pizza.app" to the Applications folder
2. Open Applications and double-click "TMSE Pizza.app" to launch

The application includes Java runtime, so no additional installation is needed.
EOF

# Remove existing DMG if it exists
rm -f "$DMG_PATH"

# Create DMG using hdiutil
hdiutil create -volname "$APP_NAME" -srcfolder "$TEMP_DMG_DIR" -ov -format UDZO "$DMG_PATH"

if [ $? -eq 0 ]; then
    echo ""
    echo "SUCCESS: .dmg installer created: $DMG_PATH"
    echo ""
    echo "The .dmg file can be distributed to users."
    echo "Users can double-click it to mount and install the application."
    
    # Clean up temp directory
    rm -rf "$TEMP_DMG_DIR"
else
    echo ""
    echo "ERROR: Failed to create .dmg installer"
    rm -rf "$TEMP_DMG_DIR"
    exit 1
fi

