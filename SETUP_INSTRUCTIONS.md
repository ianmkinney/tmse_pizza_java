# TMSE Pizza Ordering System - Setup Instructions

Complete setup guide for the Java desktop application.

## Quick Start

If you already have Java 21+ and JavaFX installed:

```bash
cd /Users/macbaby/Desktop/pizza_project/pizza_project_final/java_version
chmod +x build.sh
./build.sh
./run.sh
```

---

## Prerequisites

### Step 1: Install Java JDK 21 or higher

**macOS (using Homebrew):**
```bash
brew install openjdk@21
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
echo 'export JAVA_HOME="/opt/homebrew/opt/openjdk@21"' >> ~/.zshrc
source ~/.zshrc
```

**macOS (manual download):**
1. Visit: https://adoptium.net/temurin/releases/
2. Download JDK 21 for macOS (ARM64 for Apple Silicon, x64 for Intel)
3. Install the .pkg file

**Windows:**
1. Visit: https://adoptium.net/temurin/releases/
2. Download JDK 21 for Windows
3. Run the installer

**Verify installation:**
```bash
java -version
javac -version
```

### Step 2: Download and Install JavaFX

**Important:** JavaFX version must match your Java version (e.g., Java 21 needs JavaFX 21).

**macOS:**
```bash
# Download from https://openjfx.io/
# Select: Version 21, macOS, ARM64 (or x64 for Intel)
# Then extract:
cd ~/Downloads
unzip openjfx-21.0.2_osx-aarch64_bin.zip  # Use x64 if Intel Mac
mv javafx-sdk-21.0.2 ~/javafx-sdk-21
```

**Windows:**
1. Visit: https://openjfx.io/
2. Download JavaFX 21 for Windows
3. Extract to `C:\javafx-sdk-21` or any location you prefer

---

## Building the Application

**macOS/Linux:**
```bash
cd /Users/macbaby/Desktop/pizza_project/pizza_project_final/java_version
chmod +x build.sh
./build.sh
```

**Windows:**
```cmd
cd C:\path\to\pizza_project\pizza_project_final\java_version
build.bat
```

**Manual build (if scripts fail):**
```bash
mkdir -p build
javac -d build --module-path ~/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml src/com/tmse/pizza/models/*.java src/com/tmse/pizza/storage/*.java src/com/tmse/pizza/gui/*.java src/com/tmse/pizza/*.java
jar cvfm TMSE_Pizza.jar MANIFEST.MF -C build .
```

---

## Running the Application

**Option 1: Using run script (macOS/Linux):**
```bash
./run.sh
```

**Option 2: Run JAR directly (if JavaFX is bundled):**
```bash
java -jar TMSE_Pizza.jar
```

**Option 3: Run with JavaFX module path:**
```bash
# macOS/Linux:
java --module-path ~/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml -jar TMSE_Pizza.jar

# Windows:
java --module-path C:\javafx-sdk-21\lib --add-modules javafx.controls,javafx.fxml -jar TMSE_Pizza.jar
```

---

## Default User Accounts

**Customer Account:**
- Username: `customer`
- Password: `password123`

**Admin Account:**
- Username: `admin`
- Password: `admin123`

---

## Troubleshooting

**"Error: JavaFX runtime components are missing"**
```bash
java --module-path ~/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml -jar TMSE_Pizza.jar
```

**"Cannot find module javafx.controls"**
- Make sure JavaFX version matches your Java version
- Verify JavaFX is extracted to `~/javafx-sdk-21` (macOS/Linux) or `C:\javafx-sdk-21` (Windows)
- Check the path in the command above

**"ClassNotFoundException"**
- Rebuild the application: `./build.sh` or `build.bat`
- Ensure all files compiled successfully

**Build script fails**
- Make sure you're in the `java_version` directory
- Verify `javac` and `jar` commands are available: `javac -version` and `jar --version`
- Check that JavaFX is installed in the expected location

---

## Project Features

- **4 Pizza Sizes:** Personal (8"), Small (10"), Medium (12"), Large (16")
- **8 Toppings:** Pepperoni, Italian Sausage, Fresh Mushrooms, Onions, Bell Peppers, Black Olives, Crispy Bacon, Extra Cheese
- **3 Crust Options:** Hand-Tossed, Thin Ninja Style, Deep Dish
- **5 Beverages:** Mutant Ooze, Turtle Juice, Rockin Rhino Rootbeer, Fruit Ninja, Ninja Water
- **3 Beverage Sizes:** Small (12 oz), Medium (20 oz), Large (32 oz)
- **Order Management:** Add items to cart, view order summary with pricing and totals
- **File Storage:** User accounts and orders stored in `data/` directory

## Menu Items

**Specialty Pizzas:**
- Cowabunga Classic - $8.99 (Pepperoni, Extra Cheese)
- Shredder Supreme - $11.99 (Sausage, Mushrooms, Bell Peppers)
- Mutant Veggie Melt - $9.99 (Mushrooms, Bell Peppers, Olives)
- Ninja Chicken Combo - $11.99 (Bacon, Onions)
- Splinter's Wisdom - $8.99 (Extra Cheese)
- Build Your Own - $7.99 (Choose up to 4 toppings)

**Beverages:**
- Mutant Ooze - $1.99/$2.49/$2.99
- Turtle Juice - $1.99/$2.49/$2.99
- Rockin Rhino Rootbeer - $2.99/$3.49/$3.99
- Fruit Ninja - $2.99/$3.49/$3.99
- Ninja Water - $1.49/$1.99/$2.49

---

## Project Structure

```
java_version/
├── src/                    # Source code
│   └── com/tmse/pizza/
│       ├── models/         # Data models
│       ├── storage/        # File storage system
│       ├── gui/            # GUI windows
│       └── PizzaApp.java  # Main application
├── build/                  # Compiled classes (created during build)
├── data/                   # Data storage (created at runtime)
│   ├── users.txt
│   └── orders.txt
├── build.sh                # Linux/Mac build script
├── build.bat               # Windows build script
├── run.sh                  # Linux/Mac run script
└── TMSE_Pizza.jar          # Executable JAR (created after build)
```

---

## Notes

- All prices include automatic 8% tax calculation
- Maximum 4 toppings per pizza
- Orders are saved when confirmed
- The application uses file-based storage to simulate a database

