# Fly.io Deployment Guide for TMSE Pizza Java Application

This guide provides step-by-step instructions for deploying the TMSE Pizza JavaFX application to Fly.io using Docker.

## Important Notes

⚠️ **Limitation**: This is a **desktop GUI application** built with JavaFX. Fly.io is designed for web services and server applications. While we can containerize and run the application, **you won't be able to interact with the GUI directly** without additional setup (like VNC).

**Recommended Alternatives:**
- For web deployment, use the Next.js version in `next_app/` folder
- For desktop distribution, use GitHub Releases to host the JAR file
- For JavaFX web deployment, consider JPro Framework

However, if you still want to deploy to Fly.io for testing or other purposes, follow these instructions.

---

## Prerequisites

1. **Fly.io Account**: Sign up at [fly.io](https://fly.io) (free tier available)
2. **Fly CLI**: Install the Fly.io command-line tool
3. **Docker**: Optional, for local testing
4. **Git**: For version control

### Install Fly CLI

**macOS:**
```bash
curl -L https://fly.io/install.sh | sh
```

**Linux:**
```bash
curl -L https://fly.io/install.sh | sh
```

**Windows:**
```powershell
powershell -Command "iwr https://fly.io/install.ps1 -useb | iex"
```

Verify installation:
```bash
fly version
```

---

## Step-by-Step Deployment

### Step 1: Login to Fly.io

```bash
fly auth login
```

This will open a browser for authentication.

### Step 2: Navigate to Project Directory

```bash
cd /Users/macbaby/Desktop/pizza_project/pizza_project_final/java_version
```

### Step 3: Initialize Fly.io App (First Time Only)

```bash
fly launch
```

This command will:
- Detect your Dockerfile
- Ask for an app name (or use the one in `fly.toml`)
- Ask for a region (choose closest to you)
- Create the app on Fly.io

**Note**: If you already have a `fly.toml` file, you can skip this step or use:
```bash
fly apps create tmse-pizza
```

### Step 4: Review Configuration

Edit `fly.toml` if needed:
- Change `app = "tmse-pizza"` to your preferred app name
- Adjust `primary_region` to your preferred region
- Modify memory/CPU if needed

### Step 5: Build and Deploy

```bash
fly deploy
```

This will:
1. Build the Docker image
2. Push it to Fly.io
3. Deploy the application
4. Start the app

### Step 6: Check Deployment Status

```bash
fly status
```

View logs:
```bash
fly logs
```

---

## Configuration Details

### Dockerfile Overview

The Dockerfile uses a multi-stage build:
1. **Builder stage**: Compiles Java source and creates JAR
2. **Runtime stage**: Runs the application with Xvfb (headless display)

### Key Components

- **Java 17**: Eclipse Temurin JRE
- **JavaFX 17**: Downloaded and included in the image
- **Xvfb**: Virtual framebuffer for headless GUI execution
- **Data persistence**: Uses Fly.io volumes (see below)

---

## Data Persistence

The application stores data in the `data/` directory. To persist this data across deployments:

### Create a Volume

```bash
fly volumes create data --region iad --size 1
```

### Update fly.toml

Add volume mount configuration:

```toml
[mounts]
  source = "data"
  destination = "/app/data"
```

### Deploy with Volume

```bash
fly deploy
```

---

## Accessing the Application

Since this is a desktop GUI application, you have limited options:

### Option 1: View Logs Only

```bash
fly logs
```

The application will run, but you can't interact with it.

### Option 2: SSH into Container (For Debugging)

```bash
fly ssh console
```

Then inside the container:
```bash
# Check if Xvfb is running
ps aux | grep Xvfb

# Check if Java process is running
ps aux | grep java
```

### Option 3: Add VNC Server (Advanced)

To actually see and interact with the GUI, you would need to:
1. Add a VNC server to the Dockerfile
2. Expose VNC port (5900)
3. Connect via VNC client

This is complex and not recommended for production.

---

## Common Commands

### View App Status
```bash
fly status
```

### View Logs
```bash
fly logs
```

### SSH into Container
```bash
fly ssh console
```

### Restart App
```bash
fly apps restart tmse-pizza
```

### Scale App
```bash
fly scale count 1
fly scale memory 512
```

### View App Info
```bash
fly info
```

### Open App Dashboard
```bash
fly dashboard
```

---

## Troubleshooting

### Build Fails

**Error**: "Cannot find JavaFX modules"
- **Solution**: The Dockerfile downloads JavaFX automatically. Check build logs.

**Error**: "Out of memory"
- **Solution**: Increase memory in `fly.toml`:
  ```toml
  [[vm]]
    memory_mb = 1024
  ```

### App Won't Start

**Check logs:**
```bash
fly logs
```

**Common issues:**
- JavaFX not found: Check Dockerfile JavaFX path
- Display not set: Xvfb should handle this automatically
- Port conflicts: Not applicable for this desktop app

### Data Not Persisting

**Create and mount volume:**
```bash
fly volumes create data --region iad --size 1
```

Update `fly.toml` with mount configuration and redeploy.

---

## Updating the Application

1. Make changes to source code
2. Rebuild locally (optional, for testing):
   ```bash
   docker build -t tmse-pizza .
   ```
3. Deploy to Fly.io:
   ```bash
   fly deploy
   ```

---

## Cost Considerations

Fly.io free tier includes:
- **3 shared-cpu-1x VMs** with 256MB RAM
- **3GB persistent volume storage**
- **160GB outbound data transfer**

For this application:
- **Memory**: 512MB recommended (may need paid tier)
- **Storage**: Minimal (text files)
- **CPU**: Shared CPU is fine

**Estimated cost**: $0-5/month (depending on usage)

---

## Alternative: Better Deployment Options

### For Web Access
Deploy the Next.js version instead:
```bash
cd ../next_app
# Follow Next.js deployment guide
```

### For Desktop Distribution
1. Build JAR: `./build.sh`
2. Create GitHub Release
3. Upload `TMSE_Pizza.jar`
4. Share download link

### For JavaFX Web
Use JPro Framework to convert JavaFX to web application.

---

## Support Resources

- [Fly.io Documentation](https://fly.io/docs/)
- [Fly.io Community](https://community.fly.io/)
- [JavaFX Documentation](https://openjfx.io/)
- [Docker Documentation](https://docs.docker.com/)

---

## Summary

While it's possible to deploy this JavaFX desktop application to Fly.io, **it's not the ideal platform** for GUI applications. Consider:

1. ✅ **Next.js version** for web deployment (recommended)
2. ✅ **GitHub Releases** for desktop distribution
3. ⚠️ **Fly.io** for containerized deployment (limited GUI access)
4. ✅ **JPro Framework** for JavaFX-to-web conversion

---

**Last Updated**: 2024
**Maintained By**: TMSE Pizza Development Team

