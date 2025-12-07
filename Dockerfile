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

# Install Xvfb and other dependencies for headless GUI
RUN apt-get update && apt-get install -y \
    xvfb \
    x11vnc \
    fluxbox \
    wget \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy JavaFX SDK from builder
COPY --from=builder /app/javafx-sdk ./javafx-sdk

# Copy JAR file
COPY --from=builder /app/TMSE_Pizza.jar ./

# Copy data directory (if it exists, create if not)
COPY data/ ./data/
RUN mkdir -p data

# Set up Xvfb display
ENV DISPLAY=:99

# Create startup script
RUN echo '#!/bin/bash\n\
Xvfb :99 -screen 0 1024x768x24 &\n\
export DISPLAY=:99\n\
java --module-path ./javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar TMSE_Pizza.jar\n\
' > /app/start.sh && chmod +x /app/start.sh

# Expose port (if you want to add VNC later)
EXPOSE 5900

# Run the application
CMD ["/app/start.sh"]

