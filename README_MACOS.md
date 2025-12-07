# Creating macOS Application for TMSE Pizza

This guide explains how to create a macOS application (.app bundle) or installer (.dmg) for the TMSE Pizza application.

## Option 1: Simple Launcher Script (Recommended for Distribution)

The `create-macos-launcher.command` script creates a launcher that:
- Checks for Java installation
- Downloads JavaFX automatically if needed
- Runs the application

### To Use:

1. **Make it executable** (if not already):
   ```bash
   chmod +x TMSE_Pizza.command
   ```

2. **Run it**:
   - Double-click `TMSE_Pizza.command` in Finder
   - Or run from terminal: `./TMSE_Pizza.command`

3. **First-time setup**:
   - The launcher will download JavaFX automatically (~45MB)
   - This only happens once

### Distribution:

- Include the `.command` file and JAR file
- Users need Java 21+ installed
- Users may need to right-click and select "Open" the first time (macOS security)

## Option 2: Native .app Bundle with jpackage (Best for End Users)

Creates a professional macOS .app bundle that can be distributed like any macOS application.

### Prerequisites:

1. **Java 21+** with jpackage tool
2. **macOS** (jpackage for macOS must run on macOS)

### Steps:

1. **Build the application**:
   ```bash
   ./build.sh
   ```

2. **Download JavaFX SDK 21** (if not already present):
   ```bash
   # For Apple Silicon (M1/M2/M3):
   curl -L -o javafx.zip https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_osx-aarch64_bin-sdk.zip
   
   # For Intel Macs:
   curl -L -o javafx.zip https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_osx-x64_bin-sdk.zip
   
   unzip javafx.zip
   mv javafx-sdk-21.0.2 javafx-sdk-21
   ```

3. **Create .app bundle**:
   ```bash
   chmod +x create-macos-app.sh
   ./create-macos-app.sh
   ```

4. **Result**:
   - .app bundle will be in `dist/TMSE Pizza.app`
   - Users can double-click to run
   - Java runtime is bundled (no separate Java install needed)

## Option 3: Create .dmg Installer

Creates a disk image (.dmg) for easy distribution and installation.

### Steps:

1. **First create the .app bundle** (see Option 2)

2. **Create .dmg**:
   ```bash
   chmod +x create-macos-dmg.sh
   ./create-macos-dmg.sh
   ```

3. **Result**:
   - .dmg file will be in `dist/TMSE_Pizza.dmg`
   - Users can double-click to mount
   - Drag .app to Applications folder to install

## Distribution Requirements

### For Simple Launcher (.command):
- Users need Java 21+ installed
- Launcher will download JavaFX automatically
- Small file size (~120KB for JAR + launcher)

### For .app Bundle (jpackage):
- No Java installation needed (bundled)
- Professional macOS application
- Larger file size (~150-200MB)

### For .dmg Installer:
- No Java installation needed (bundled)
- Professional installer experience
- Users drag .app to Applications folder

## Code Signing (Optional, for Distribution Outside App Store)

To code sign your .app bundle for distribution:

```bash
# Sign the .app bundle
codesign --deep --force --verify --verbose --sign "Developer ID Application: Your Name" "dist/TMSE Pizza.app"

# Verify signature
codesign --verify --verbose "dist/TMSE Pizza.app"
```

## Notarization (Optional, for macOS Gatekeeper)

For distribution outside the App Store, you may want to notarize:

```bash
# Create a zip for notarization
ditto -c -k --keepParent "dist/TMSE Pizza.app" "dist/TMSE_Pizza.zip"

# Submit for notarization (requires Apple Developer account)
xcrun notarytool submit "dist/TMSE_Pizza.zip" --apple-id "your@email.com" --team-id "YOUR_TEAM_ID" --password "app-specific-password" --wait
```

## Recommended Approach

**For easy distribution**: Use Option 1 (launcher script)
- Small file size
- Automatic JavaFX download
- Users just need Java installed

**For professional distribution**: Use Option 2 or 3 (.app bundle or .dmg)
- No Java installation required
- Professional macOS application
- Larger download size
- Better user experience

## Troubleshooting

**"Permission denied" when running .command file**:
- Right-click the file → Open
- Or: `chmod +x TMSE_Pizza.command` in terminal

**"jpackage not found"**:
- Make sure Java 21+ is installed
- Check: `java -version` and `jpackage --version`

**"JavaFX download fails"**:
- Check internet connection
- Download manually from https://openjfx.io/
- Extract to `javafx-sdk-21` in the same directory

