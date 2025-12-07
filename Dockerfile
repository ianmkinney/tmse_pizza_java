# Multi-stage build for TMSE Pizza JavaFX Application
FROM eclipse-temurin:21-jdk as builder

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

# Download and extract JavaFX SDK 21
RUN wget -q https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_linux-x64_bin-sdk.zip && \
    unzip -q openjfx-21.0.2_linux-x64_bin-sdk.zip && \
    mv javafx-sdk-21.0.2 javafx-sdk && \
    rm openjfx-21.0.2_linux-x64_bin-sdk.zip && \
    # Verify native libraries are present
    ls -la javafx-sdk/lib/ | head -20 && \
    echo "JavaFX SDK extracted successfully"

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
FROM eclipse-temurin:21-jre

# Install Xvfb, VNC server, window manager, web server, X11 libraries, GTK libraries, and network tools
# GTK libraries are needed for JavaFX on Linux
RUN apt-get update && apt-get install -y \
    xvfb \
    x11vnc \
    fluxbox \
    wget \
    tigervnc-common \
    python3 \
    python3-pip \
    python3-venv \
    libx11-dev \
    libxext-dev \
    libxrender-dev \
    libxtst6 \
    libxi6 \
    libgtk-3-0 \
    libgtk-3-dev \
    libgdk-pixbuf2.0-0 \
    libgl1 \
    libglx0 \
    libglib2.0-0 \
    net-tools \
    iproute2 \
    && rm -rf /var/lib/apt/lists/*

# Install noVNC and websockify for web-based VNC access
# Use virtual environment to avoid externally-managed-environment error
RUN mkdir -p /opt/novnc && \
    cd /opt/novnc && \
    wget -q https://github.com/novnc/noVNC/archive/refs/tags/v1.4.0.tar.gz && \
    tar -xzf v1.4.0.tar.gz --strip-components=1 && \
    rm v1.4.0.tar.gz && \
    python3 -m venv /opt/novnc/venv && \
    /opt/novnc/venv/bin/pip install --no-cache-dir websockify && \
    # Copy vnc_lite.html as index.html so it's served by default
    cp /opt/novnc/vnc_lite.html /opt/novnc/index.html

# Set working directory
WORKDIR /app

# Copy JavaFX SDK from builder (includes native libraries)
COPY --from=builder /app/javafx-sdk ./javafx-sdk

# Verify JavaFX native libraries are present
RUN ls -la javafx-sdk/lib/*.so 2>/dev/null | head -5 || echo "Checking for native libraries..." && \
    echo "JavaFX SDK copied with native libraries"

# Copy JAR file
COPY --from=builder /app/TMSE_Pizza.jar ./

# Create data directory (app will create files at runtime)
# Note: We don't copy the data/ directory to avoid build errors if it's missing
# The application will create the necessary data files when it runs
RUN mkdir -p data

# Set up Xvfb display
ENV DISPLAY=:99

# Create minimal Fluxbox config to avoid warnings
# Fluxbox will create defaults if these don't exist, but we create them to silence warnings
RUN mkdir -p /root/.fluxbox && \
    echo "session.screen0.toolbar.onHead: 0" > /root/.fluxbox/init && \
    echo "session.screen0.toolbar.placement: TopCenter" >> /root/.fluxbox/init && \
    echo "session.screen0.toolbar.height: 0" >> /root/.fluxbox/init && \
    echo "session.screen0.slit.placement: TopRight" >> /root/.fluxbox/init && \
    echo "session.screen0.iconbar.mode: Workspace" >> /root/.fluxbox/init && \
    echo "session.screen0.iconbar.alignment: Relative" >> /root/.fluxbox/init && \
    echo "session.screen0.iconbar.iconWidth: 80" >> /root/.fluxbox/init && \
    echo "session.screen0.iconbar.iconTextPadding: 10" >> /root/.fluxbox/init && \
    echo "session.screen0.iconbar.usePixmap: true" >> /root/.fluxbox/init && \
    chmod 644 /root/.fluxbox/init

# Create startup script with VNC server
RUN echo '#!/bin/bash\n\
set +e  # Don'\''t exit on error\n\
\n\
echo "=== Starting TMSE Pizza Application ==="\n\
\n\
# Start Xvfb (virtual framebuffer)\n\
echo "Starting Xvfb..."\n\
Xvfb :99 -screen 0 1920x1080x24 -ac +extension GLX +render -noreset > /tmp/xvfb.log 2>&1 &\n\
XVFB_PID=$!\n\
sleep 3\n\
\n\
# Check if Xvfb started\n\
if ! kill -0 $XVFB_PID 2>/dev/null; then\n\
    echo "ERROR: Xvfb failed to start"\n\
    cat /tmp/xvfb.log\n\
    exit 1\n\
fi\n\
echo "Xvfb started (PID: $XVFB_PID)"\n\
\n\
# Start window manager (Fluxbox)\n\
echo "Starting Fluxbox..."\n\
DISPLAY=:99 fluxbox > /tmp/fluxbox.log 2>&1 &\n\
FLUXBOX_PID=$!\n\
sleep 2\n\
echo "Fluxbox started (PID: $FLUXBOX_PID)"\n\
\n\
# Set VNC password (default: pizza123, change via VNC_PASSWORD env var)\n\
VNC_PASSWORD=${VNC_PASSWORD:-pizza123}\n\
echo "VNC password: $VNC_PASSWORD"\n\
\n\
# Start VNC server with password\n\
echo "Starting VNC server on port 5900..."\n\
# Use -passwd option and bind to 0.0.0.0 to be reachable from outside\n\
# Remove -bg flag and run in foreground with & to ensure proper binding\n\
x11vnc -display :99 -forever -shared -rfbport 5900 -rfbaddr 0.0.0.0 -passwd "$VNC_PASSWORD" -xkb -o /tmp/x11vnc.log > /tmp/x11vnc.log 2>&1 &\n\
X11VNC_PID=$!\n\
sleep 3\n\
\n\
# Check if VNC started and is listening on the correct address\n\
if ! kill -0 $X11VNC_PID 2>/dev/null; then\n\
    echo "ERROR: VNC server failed to start"\n\
    cat /tmp/x11vnc.log\n\
else\n\
    echo "VNC server started (PID: $X11VNC_PID)"\n\
    # Verify it's listening on 0.0.0.0:5900\n\
    if netstat -tlnp 2>/dev/null | grep -q ":5900" || ss -tlnp 2>/dev/null | grep -q ":5900"; then\n\
        echo "VNC server is listening on port 5900"\n\
    else\n\
        echo "WARNING: VNC server may not be listening on port 5900"\n\
    fi\n\
fi\n\
\n\
# Start noVNC web server (websockify) for browser access\n\
echo "Starting noVNC web server on port 6080..."\n\
# Start websockify bound to 0.0.0.0 to be reachable from outside\n\
cd /opt/novnc && /opt/novnc/venv/bin/websockify --web=. --listen 0.0.0.0:6080 localhost:5900 > /tmp/novnc.log 2>&1 &\n\
NOVNC_PID=$!\n\
sleep 3\n\
if kill -0 $NOVNC_PID 2>/dev/null; then\n\
    echo "noVNC web server started (PID: $NOVNC_PID)"\n\
    # Verify it's listening on 0.0.0.0:6080\n\
    if netstat -tlnp 2>/dev/null | grep -q ":6080" || ss -tlnp 2>/dev/null | grep -q ":6080"; then\n\
        echo "noVNC is listening on port 6080"\n\
    else\n\
        echo "WARNING: noVNC may not be listening on port 6080"\n\
    fi\n\
else\n\
    echo "ERROR: noVNC failed to start"\n\
    cat /tmp/novnc.log\n\
fi\n\
\n\
# Export display\n\
export DISPLAY=:99\n\
\n\
# Start the JavaFX application\n\
echo "Starting JavaFX application..."\n\
# Wait a bit more for Fluxbox to fully initialize\n\
sleep 3\n\
\n\
# Start JavaFX app - make sure it's visible and not minimized\n\
cd /app\n\
# Set library path for JavaFX native libraries\n\
# JavaFX needs the native libraries from javafx-sdk/lib to be accessible\n\
export LD_LIBRARY_PATH=/app/javafx-sdk/lib:$LD_LIBRARY_PATH\n\
export DISPLAY=:99\n\
# Let JavaFX use GTK (default on Linux) - we've installed GTK libraries\n\
# GTK works with Xvfb/X11 display server\n\
# Use software rendering for better compatibility\n\
java -Djava.awt.headless=false \\\n\
     -Dprism.order=sw \\\n\
     -Djava.library.path=/app/javafx-sdk/lib \\\n\
     -Djava.awt.preferences.systemRoot=/tmp \\\n\
     --module-path /app/javafx-sdk/lib \\\n\
     --add-modules javafx.controls,javafx.fxml \\\n\
     -jar TMSE_Pizza.jar > /tmp/java.log 2>&1 &\n\
JAVA_PID=$!\n\
sleep 5\n\
\n\
# Check if Java started and is still running\n\
if ! kill -0 $JAVA_PID 2>/dev/null; then\n\
    echo "ERROR: Java application failed to start or crashed immediately"\n\
    echo "=== Java Application Logs ==="\n\
    cat /tmp/java.log\n\
    echo "=== End of Java Logs ==="\n\
    echo "Container will stay running for debugging."\n\
    echo "You can SSH in and manually start the app"\n\
    # Keep container running for debugging\n\
    tail -f /tmp/java.log /tmp/x11vnc.log\n\
else\n\
    echo "=== Application Started Successfully ==="\n\
    echo "JavaFX app PID: $JAVA_PID"\n\
    echo "Check /tmp/java.log for application output"\n\
    echo "If the window is not visible, try:"\n\
    echo "  1. Check all workspaces (use Ctrl+Alt+Arrow keys in VNC)"\n\
    echo "  2. Look for the window in Fluxbox taskbar"\n\
    echo "  3. SSH in and check: ps aux | grep java"\n\
    \n\
    # Keep container running - monitor Java process\n\
    while kill -0 $JAVA_PID 2>/dev/null; do\n\
        sleep 10\n\
    done\n\
    \n\
    echo "Java application exited. Container will stay running for VNC access."\n\
    # Keep container alive for VNC access even if Java exits\n\
    tail -f /dev/null\n\
fi\n\
' > /app/start.sh && chmod +x /app/start.sh

# Expose VNC port (5900) and noVNC web port (6080)
EXPOSE 5900 6080

# Run the application
CMD ["/app/start.sh"]

