TMSE Pizza - macOS Installation Guide
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
