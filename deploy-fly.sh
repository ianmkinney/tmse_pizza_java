#!/bin/bash

# Quick deployment script for Fly.io
# This script helps you deploy the TMSE Pizza application to Fly.io

set -e

echo "🚀 TMSE Pizza - Fly.io Deployment Script"
echo "=========================================="
echo ""

# Check if fly CLI is installed
if ! command -v fly &> /dev/null; then
    echo "❌ Fly CLI not found!"
    echo ""
    echo "Please install Fly CLI first:"
    echo "  macOS/Linux: curl -L https://fly.io/install.sh | sh"
    echo "  Windows: powershell -Command \"iwr https://fly.io/install.ps1 -useb | iex\""
    echo ""
    exit 1
fi

echo "✅ Fly CLI found: $(fly version)"
echo ""

# Check if user is logged in
if ! fly auth whoami &> /dev/null; then
    echo "🔐 Not logged in to Fly.io"
    echo "Opening login page..."
    fly auth login
else
    echo "✅ Logged in as: $(fly auth whoami)"
fi

echo ""

# Check if fly.toml exists
if [ ! -f "fly.toml" ]; then
    echo "📝 Initializing Fly.io app..."
    fly launch --no-deploy
    echo ""
    echo "⚠️  Please review fly.toml and adjust settings if needed"
    echo "   Then run this script again to deploy"
    exit 0
fi

# Check if Dockerfile exists
if [ ! -f "Dockerfile" ]; then
    echo "❌ Dockerfile not found!"
    echo "Please ensure Dockerfile exists in the current directory"
    exit 1
fi

echo "📦 Building and deploying to Fly.io..."
echo ""

# Deploy
fly deploy

echo ""
echo "✅ Deployment complete!"
echo ""
echo "Useful commands:"
echo "  fly logs          - View application logs"
echo "  fly status        - Check deployment status"
echo "  fly ssh console   - SSH into the container"
echo "  fly dashboard     - Open Fly.io dashboard"
echo ""

