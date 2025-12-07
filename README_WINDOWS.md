# Creating Windows Executable for TMSE Pizza

This guide explains how to create a Windows .exe file that can run the TMSE Pizza application on any Windows computer.

## Option 1: Simple Launcher Script (Recommended for Distribution)

The `create-windows-launcher.bat` script creates a launcher that:
- Checks for Java installation
- Downloads JavaFX automatically if needed
- Runs the application

### To Convert to .exe:

1. **Install Bat To Exe Converter** (free tool):
   - Download from: https://www.battoexeconverter.com/
   - Or use: https://github.com/topics/bat-to-exe

2. **Convert the launcher**:
   - Open Bat To Exe Converter
   - Load `create-windows-launcher.bat`
   - Set options:
     - Invisible: No (so users see progress)
     - Include JAR: Yes (embed TMSE_Pizza.jar)
   - Click "Compile" to create .exe

3. **Distribute**:
   - Include the .exe file
   - Users need Java 21+ installed (or bundle it - see Option 2)

## Option 2: Native Installer with jpackage (Best for End Users)

Creates a professional installer that bundles Java runtime.

### Prerequisites:

1. **Java 21+** with jpackage tool
2. **Inno Setup 6+** (for .exe installer):
   - Download: https://jrsoftware.org/isdl.php
   - Install and add to PATH

### Steps:

1. **Build the application**:
   ```bash
   ./build.sh
   ```

2. **Download JavaFX SDK 21**:
   - Download: https://openjfx.io/
   - Extract to: `javafx-sdk-21/` in project directory

3. **Create installer**:
   ```bash
   chmod +x create-installer.sh
   ./create-installer.sh
   ```

4. **Result**:
   - Installer will be in `dist/` directory
   - Users can install like any Windows program
   - Java runtime is bundled (no separate Java install needed)

## Option 3: Launch4j (Alternative)

Creates a Windows .exe wrapper around the JAR.

### Steps:

1. **Download Launch4j**: http://launch4j.sourceforge.net/

2. **Configure**:
   - Output file: `TMSE_Pizza.exe`
   - Jar: `TMSE_Pizza.jar`
   - Main class: `com.tmse.pizza.PizzaApp`
   - JRE: Min version `21.0.0`
   - Header: GUI
   - Options: Add VM options:
     ```
     --module-path javafx-sdk-21/lib
     --add-modules javafx.controls,javafx.fxml
     ```

3. **Build**:
   - Click "Build wrapper"
   - Distribute the .exe with the JAR file

## Distribution Requirements

### For Simple Launcher (.bat or .exe):
- Users need Java 21+ installed
- Launcher will download JavaFX automatically

### For jpackage Installer:
- No Java installation needed (bundled)
- Larger file size (~150-200MB)

### For Launch4j:
- Users need Java 21+ installed
- JavaFX must be included separately or downloaded

## Recommended Approach

**For easy distribution**: Use Option 1 (launcher script converted to .exe)
- Small file size
- Automatic JavaFX download
- Users just need Java installed

**For professional distribution**: Use Option 2 (jpackage)
- No Java installation required
- Professional installer
- Larger download size

