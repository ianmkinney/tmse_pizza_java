# Multi-stage build for TMSE Pizza JavaFX Application
FROM eclipse-temurin:17-jdk as builder

# Install build dependencies
RUN apt-get update && apt-get install -y \
    unzip \
    wget \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy source files
COPY src/ ./src/
COPY MANIFEST.MF ./
COPY build.sh ./

# Download and extract JavaFX SDK 17
RUN wget -q https://download2.gluonhq.com/openjfx/17.0.2/openjfx-17.0.2_linux-x64_bin-sdk.zip && \
    unzip -q openjfx-17.0.2_linux-x64_bin-sdk.zip && \
    mv javafx-sdk-17.0.2 javafx-sdk && \
    rm openjfx-17.0.2_linux-x64_bin-sdk.zip

# Build the application
RUN chmod +x build.sh && \
    mkdir -p build && \
    javac -d build \
        --module-path ./javafx-sdk/lib \
        --add-modules javafx.controls,javafx.fxml \
        src/com/tmse/pizza/models/*.java \
        src/com/tmse/pizza/storage/*.java \
        src/com/tmse/pizza/gui/*.java \
        src/com/tmse/pizza/*.java && \
    jar cvfm TMSE_Pizza.jar MANIFEST.MF -C build .

# Runtime stage
FROM eclipse-temurin:17-jre

# Install Xvfb, VNC server, and window manager for GUI access
RUN apt-get update && apt-get install -y \
    xvfb \
    x11vnc \
    fluxbox \
    wget \
    tigervnc-common \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy JavaFX SDK from builder
COPY --from=builder /app/javafx-sdk ./javafx-sdk

# Copy JAR file
COPY --from=builder /app/TMSE_Pizza.jar ./

# Create data directory (app will create files at runtime)
# Note: We don't copy the data/ directory to avoid build errors if it's missing
# The application will create the necessary data files when it runs
RUN mkdir -p data

# Set up Xvfb display
ENV DISPLAY=:99

# Create startup script with VNC server
RUN echo '#!/bin/bash\n\
set -e\n\
\n\
# Start Xvfb (virtual framebuffer)\n\
Xvfb :99 -screen 0 1920x1080x24 -ac +extension GLX +render -noreset &\n\
XVFB_PID=$!\n\
\n\
# Wait for Xvfb to be ready\n\
sleep 2\n\
\n\
# Start window manager (Fluxbox)\n\
DISPLAY=:99 fluxbox &\n\
\n\
# Set VNC password (default: pizza123, change via VNC_PASSWORD env var)\n\
VNC_PASSWORD=${VNC_PASSWORD:-pizza123}\n\
mkdir -p ~/.vnc\n\
echo "$VNC_PASSWORD" | vncpasswd -f > ~/.vnc/passwd\n\
chmod 600 ~/.vnc/passwd\n\
\n\
# Start VNC server\n\
x11vnc -display :99 -forever -shared -rfbport 5900 -rfbauth ~/.vnc/passwd -xkb -bg -o /tmp/x11vnc.log\n\
\n\
# Wait a moment for VNC to start\n\
sleep 2\n\
\n\
# Export display\n\
export DISPLAY=:99\n\
\n\
# Start the JavaFX application\n\
echo "Starting TMSE Pizza Application..."\n\
java --module-path ./javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar TMSE_Pizza.jar &\n\
JAVA_PID=$!\n\
\n\
# Keep container running\n\
echo "VNC server running on port 5900"\n\
echo "JavaFX application started (PID: $JAVA_PID)"\n\
echo "Connect using a VNC client to: your-app.fly.dev:5900"\n\
\n\
# Wait for processes\n\
wait $JAVA_PID\n\
' > /app/start.sh && chmod +x /app/start.sh

# Expose VNC port
EXPOSE 5900

# Run the application
CMD ["/app/start.sh"]

