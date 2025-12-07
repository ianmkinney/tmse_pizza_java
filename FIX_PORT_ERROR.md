# Fix: Port 5900 Connection Error

## Problem
Fly.io is trying to connect to port 5900, but your desktop GUI application isn't listening on that port.

## Solution

### Option 1: Remove Service Configuration (Recommended)

If Fly.io auto-created a service configuration, remove it:

```bash
# Check current configuration
fly config show -a tmse-pizza-java

# If there's a service section, you can remove it by editing fly.toml
# The current fly.toml should not have any [http_service] or [[services]] sections
```

### Option 2: Redeploy with Updated Configuration

The Dockerfile and fly.toml have been updated. Redeploy:

```bash
cd /Users/macbaby/Desktop/pizza_project/pizza_project_final/java_version

# Redeploy with the fixed configuration
fly deploy -a tmse-pizza-java
```

### Option 3: Scale Down and Remove Service (If Needed)

If the service is still configured:

```bash
# Scale down the app
fly scale count 0 -a tmse-pizza-java

# Remove any service configuration (if it exists in the deployed app)
# Then redeploy
fly deploy -a tmse-pizza-java
```

## What Changed

1. **Dockerfile**: Removed `EXPOSE 5900` (commented out)
2. **fly.toml**: No service configuration - this is a desktop app, not a web service

## Verify

After redeploying, check the status:

```bash
fly status -a tmse-pizza-java
fly logs -a tmse-pizza-java
```

The app should run without port connection errors. Since it's a desktop GUI app, you won't be able to access it via HTTP, but it will run in headless mode.

