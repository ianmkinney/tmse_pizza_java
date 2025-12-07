# VNC GUI Access Guide for TMSE Pizza Application

Your JavaFX application is now configured with VNC (Virtual Network Computing) for remote GUI access!

## Quick Start

### 1. Deploy the Application

```bash
cd /Users/macbaby/Desktop/pizza_project/pizza_project_final/java_version
fly deploy -a tmse-pizza-java
```

### 2. Get Your VNC URL

After deployment, your app will be available at:
```
tmse-pizza-java.fly.dev:5900
```

### 3. Connect with a VNC Client

**Default VNC Password**: `pizza123`

## VNC Client Options

### macOS

**Option 1: Built-in Screen Sharing**
1. Open Finder
2. Press `Cmd + K` (or Go → Connect to Server)
3. Enter: `vnc://tmse-pizza-java.fly.dev:5900`
4. Enter password: `pizza123`

**Option 2: RealVNC Viewer** (Recommended)
1. Download from: https://www.realvnc.com/en/connect/download/viewer/
2. Install and open
3. Enter: `tmse-pizza-java.fly.dev:5900`
4. Enter password: `pizza123`

**Option 3: TigerVNC Viewer**
```bash
brew install tigervnc-viewer
vncviewer tmse-pizza-java.fly.dev:5900
```

### Windows

**RealVNC Viewer**
1. Download from: https://www.realvnc.com/en/connect/download/viewer/
2. Install and open
3. Enter: `tmse-pizza-java.fly.dev:5900`
4. Enter password: `pizza123`

**TightVNC Viewer**
1. Download from: https://www.tightvnc.com/download.php
2. Install and connect to: `tmse-pizza-java.fly.dev:5900`

### Linux

**TigerVNC Viewer**
```bash
sudo apt-get install tigervnc-viewer
vncviewer tmse-pizza-java.fly.dev:5900
```

**Remmina** (GUI)
```bash
sudo apt-get install remmina
# Then add new VNC connection in Remmina
```

### Web Browser (No Installation Needed!)

**noVNC** - Access via web browser:
1. Visit: `https://tmse-pizza-java.fly.dev:5900` (if configured)
2. Or use a noVNC gateway service

## Changing the VNC Password

### Option 1: Set via Environment Variable

```bash
fly secrets set VNC_PASSWORD=your-new-password -a tmse-pizza-java
fly deploy -a tmse-pizza-java
```

### Option 2: Edit fly.toml

Add to the `[env]` section:
```toml
[env]
  VNC_PASSWORD = "your-secure-password"
```

Then redeploy:
```bash
fly deploy -a tmse-pizza-java
```

## Troubleshooting

### Can't Connect to VNC

1. **Check if app is running:**
   ```bash
   fly status -a tmse-pizza-java
   ```

2. **Check logs:**
   ```bash
   fly logs -a tmse-pizza-java
   ```
   Look for: "VNC server running on port 5900"

3. **Verify port is exposed:**
   ```bash
   fly config show -a tmse-pizza-java
   ```
   Should show port 5900 in services section

4. **Try SSH and check manually:**
   ```bash
   fly ssh console -a tmse-pizza-java
   # Inside container:
   ps aux | grep x11vnc
   netstat -tlnp | grep 5900
   ```

### Application Not Visible

1. The JavaFX app should start automatically
2. If not visible, check logs:
   ```bash
   fly logs -a tmse-pizza-java
   ```
3. You can manually start it via SSH:
   ```bash
   fly ssh console -a tmse-pizza-java
   # Inside container:
   export DISPLAY=:99
   java --module-path ./javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar TMSE_Pizza.jar
   ```

### Connection is Slow

- VNC over the internet can be slow
- Consider using a VNC client with compression
- RealVNC and TigerVNC have good compression options

## Security Notes

⚠️ **Important Security Considerations:**

1. **Change the default password** - The default password is `pizza123`. Change it immediately:
   ```bash
   fly secrets set VNC_PASSWORD=your-strong-password -a tmse-pizza-java
   ```

2. **VNC is not encrypted by default** - Fly.io uses TLS/HTTPS for the connection, but consider:
   - Using SSH tunnel for additional security
   - Setting a strong password
   - Not exposing sensitive data

3. **Access Control** - Consider restricting access:
   - Use Fly.io IP allowlists (if available)
   - Monitor access via `fly logs`

## Architecture

The setup includes:
- **Xvfb**: Virtual framebuffer (headless display server)
- **Fluxbox**: Lightweight window manager
- **x11vnc**: VNC server that shares the X11 display
- **JavaFX Application**: Your TMSE Pizza app running on the virtual display

## Performance

- **Resolution**: 1920x1080 (can be adjusted in Dockerfile)
- **Color Depth**: 24-bit
- **Memory**: Uses ~512MB RAM
- **CPU**: Shared CPU (1 core)

## Next Steps

1. Deploy: `fly deploy -a tmse-pizza-java`
2. Wait for deployment to complete
3. Connect with VNC client: `tmse-pizza-java.fly.dev:5900`
4. Enter password: `pizza123` (or your custom password)
5. Enjoy your GUI application!

---

**Need Help?**
- Check logs: `fly logs -a tmse-pizza-java`
- SSH into container: `fly ssh console -a tmse-pizza-java`
- View status: `fly status -a tmse-pizza-java`

