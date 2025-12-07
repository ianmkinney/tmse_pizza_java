# TMSE Pizza Application - Deployment Plan

This document outlines deployment strategies for making the TMSE Pizza JavaFX application accessible on the web for free.

## Current Application Architecture

- **Technology**: JavaFX desktop application
- **Language**: Java
- **Build**: JAR file (TMSE_Pizza.jar)
- **Storage**: File-based (text files in `data/` directory)

## Deployment Options

### Option 1: JAR File Hosting (Simplest - Recommended for Quick Demo)

**Description**: Package the application as a JAR and host it for download.

**Steps**:
1. Build the JAR file using the existing build scripts
2. Upload JAR to a free file hosting service
3. Create a simple HTML page with download link and instructions

**Free Hosting Options**:
- **GitHub Releases**: Upload JAR as a release asset
- **Google Drive**: Share download link
- **Dropbox**: Public folder link
- **SourceForge**: Free project hosting

**Pros**:
- ✅ No code changes needed
- ✅ Works immediately
- ✅ Free

**Cons**:
- ❌ Users need Java installed
- ❌ Desktop application only
- ❌ Not truly "web-based"

**Implementation**:
```bash
# Build JAR
cd java_version
./build.sh  # or build.bat on Windows

# Upload TMSE_Pizza.jar to hosting service
# Create download page
```

---

### Option 2: JavaFX Web Deployment (Modern Approach)

**Description**: Convert JavaFX application to run in a web browser using JavaFX Web technology.

**Tools Needed**:
- **jlink**: Create custom runtime
- **jpackage**: Create native installers
- **JPro Framework** (free for open source): JavaFX-to-web conversion
- **Gluon Substrate**: Compile to native/web

**Recommended Tool: JPro Framework**

**Steps**:
1. Add JPro dependency to project
2. Wrap JavaFX application with JPro server
3. Deploy to cloud platform

**Free Hosting Options**:
- **Heroku** (free tier with limitations)
- **Render** (free tier)
- **Railway** (free tier)
- **Fly.io** (free tier)

**Implementation**:
```xml
<!-- Add to pom.xml or build.gradle -->
<dependency>
    <groupId>com.jpro</groupId>
    <artifactId>jpro-webapi</artifactId>
    <version>2022.1.2</version>
</dependency>
```

**Pros**:
- ✅ True web deployment
- ✅ No Java installation needed for users
- ✅ Accessible via browser

**Cons**:
- ⚠️ Requires code modifications
- ⚠️ Learning curve for JPro
- ⚠️ Some platforms may require paid tier for production

---

### Option 3: Convert to Web Application (Recommended for Production)

**Description**: Convert JavaFX UI to a web framework (React, Angular, Vue, or Next.js).

**Steps**:
1. Extract business logic from JavaFX controllers
2. Create REST API backend (Spring Boot, Java)
3. Build web frontend matching JavaFX design
4. Deploy frontend and backend separately

**Free Hosting Options**:

**Frontend**:
- **Vercel** (free tier) - Best for Next.js/React
- **Netlify** (free tier) - Static sites
- **GitHub Pages** (free) - Static hosting

**Backend**:
- **Render** (free tier) - Java applications
- **Railway** (free tier) - Full-stack apps
- **Fly.io** (free tier) - Container deployment
- **Oracle Cloud Free Tier** - Always free compute

**Pros**:
- ✅ True web application
- ✅ Better performance
- ✅ Mobile-friendly
- ✅ No Java installation needed

**Cons**:
- ⚠️ Significant development time
- ⚠️ Need to rebuild UI in web framework

**Note**: You already have a Next.js version in `next_app/` folder - this could be deployed directly!

---

### Option 4: Docker Container Deployment (Fly.io)

**Description**: Package application in Docker container and deploy to Fly.io.

**Location**: `java_version/` directory contains:
- `Dockerfile` - Multi-stage build with JavaFX support
- `fly.toml` - Fly.io configuration
- `FLY_IO_DEPLOYMENT.md` - **Complete step-by-step guide**

**Quick Start**:
```bash
# 1. Install Fly CLI
curl -L https://fly.io/install.sh | sh

# 2. Login
fly auth login

# 3. Navigate to project
cd java_version

# 4. Deploy
fly deploy
```

**Detailed Instructions**: See `FLY_IO_DEPLOYMENT.md` for complete guide.

**Free Hosting Options**:
- **Fly.io** (free tier) - ✅ **Recommended for Docker**
  - 3 shared-cpu VMs (256MB RAM each)
  - 3GB persistent storage
  - 160GB outbound data
- **Render** (free tier) - Docker support
- **Railway** (free tier) - Container hosting
- **Google Cloud Run** (free tier) - Pay per use

**Dockerfile Features**:
- Multi-stage build (optimized image size)
- JavaFX 17 included automatically
- Xvfb for headless GUI execution
- Data persistence support

**Pros**:
- ✅ Consistent deployment
- ✅ Easy scaling
- ✅ Portable
- ✅ Complete Dockerfile and config provided
- ✅ Detailed deployment guide included

**Cons**:
- ⚠️ Desktop GUI app - limited interaction without VNC
- ⚠️ Fly.io optimized for web services, not desktop apps
- ⚠️ Consider Next.js version for true web deployment

**Important Note**: This is a desktop GUI application. Fly.io deployment will run it in headless mode. For web access, consider deploying the Next.js version instead.

---

### Option 5: Use Existing Next.js Version (Quickest Web Deployment)

**Description**: You already have a Next.js version! Deploy that directly.

**Location**: `pizza_project_final/next_app/`

**Free Hosting Options**:
- **Vercel** (recommended for Next.js) - Free tier includes:
  - Automatic deployments from GitHub
  - HTTPS
  - Global CDN
  - Serverless functions
- **Netlify** - Free tier
- **Railway** - Free tier

**Steps**:
1. Push Next.js app to GitHub
2. Connect to Vercel
3. Deploy automatically

**Vercel Deployment**:
```bash
cd pizza_project_final/next_app
npm install
npx vercel
```

**Pros**:
- ✅ Already built!
- ✅ True web application
- ✅ Free hosting
- ✅ No code changes needed

**Cons**:
- ⚠️ Need to ensure backend is set up (if using server.js)

---

## Recommended Approach: Hybrid Strategy

### Phase 1: Quick Demo (1-2 hours)
**Use Option 1**: Host JAR file on GitHub Releases
- Build JAR
- Create GitHub release
- Add download instructions

### Phase 2: Web Deployment (Recommended)
**Use Option 5**: Deploy Next.js version to Vercel
- Already have Next.js app
- Free hosting
- True web experience
- Can deploy immediately

### Phase 3: JavaFX Web (Future)
**Use Option 2**: Convert JavaFX to web using JPro
- Maintain Java codebase
- Web deployment
- Requires more setup

---

## Deployment Checklist

### For JAR File Hosting:
- [ ] Build JAR file (`./build.sh` or `build.bat`)
- [ ] Test JAR file locally
- [ ] Create GitHub release
- [ ] Upload JAR to release
- [ ] Create README with Java installation instructions
- [ ] Add download link

### For Next.js Deployment (Vercel):
- [ ] Review `next_app/` code
- [ ] Ensure all dependencies are in `package.json`
- [ ] Push code to GitHub repository
- [ ] Sign up for Vercel (free)
- [ ] Import GitHub repository
- [ ] Configure build settings:
  - Build command: `npm run build`
  - Output directory: `.next`
- [ ] Deploy
- [ ] Test deployed application

### For JavaFX Web (JPro):
- [ ] Add JPro dependency
- [ ] Wrap application with JPro server
- [ ] Test locally
- [ ] Create deployment configuration
- [ ] Deploy to Heroku/Render/Railway
- [ ] Configure environment variables

---

## Environment Setup for Web Deployment

### Database/Storage Considerations

Currently using file-based storage (`data/` directory). For web deployment, consider:

1. **Keep file-based** (if single instance)
   - Works for small deployments
   - Files stored on server

2. **Migrate to database** (recommended for production)
   - **Free Database Options**:
     - **PostgreSQL**: Supabase (free tier), ElephantSQL (free tier)
     - **MySQL**: PlanetScale (free tier), AWS RDS (free tier)
     - **MongoDB**: MongoDB Atlas (free tier)
     - **SQLite**: Works for small apps, file-based

3. **Use cloud storage**
   - **AWS S3** (free tier)
   - **Google Cloud Storage** (free tier)
   - **Firebase Realtime Database** (free tier)

---

## Free Hosting Platform Comparison

| Platform | Free Tier | Best For | Java Support | Web Apps |
|----------|-----------|----------|--------------|----------|
| **Vercel** | Yes | Next.js/React | ❌ | ✅ |
| **Netlify** | Yes | Static sites | ❌ | ✅ |
| **Render** | Yes | Full-stack | ✅ | ✅ |
| **Railway** | Yes | Full-stack | ✅ | ✅ |
| **Fly.io** | Yes | Docker/Containers | ✅ | ✅ |
| **Heroku** | Limited | General | ✅ | ✅ |
| **GitHub Pages** | Yes | Static sites | ❌ | ✅ |
| **Oracle Cloud** | Yes | VMs/Containers | ✅ | ✅ |

---

## Step-by-Step: Deploy Next.js App to Vercel (Recommended)

### Prerequisites:
- GitHub account
- Vercel account (free)

### Steps:

1. **Prepare the Next.js app**:
   ```bash
   cd pizza_project_final/next_app
   npm install
   npm run build  # Test build locally
   ```

2. **Push to GitHub**:
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin <your-github-repo-url>
   git push -u origin main
   ```

3. **Deploy to Vercel**:
   - Go to [vercel.com](https://vercel.com)
   - Sign up with GitHub
   - Click "Import Project"
   - Select your repository
   - Vercel auto-detects Next.js
   - Click "Deploy"
   - Wait for deployment (2-3 minutes)

4. **Your app is live!**
   - Get URL like: `your-app.vercel.app`
   - Automatic HTTPS
   - Global CDN

5. **Update automatically**:
   - Every git push auto-deploys
   - Preview deployments for PRs

---

## Step-by-Step: Host JAR on GitHub Releases

1. **Build JAR**:
   ```bash
   cd pizza_project_final/java_version
   ./build.sh  # Linux/Mac
   # or
   build.bat   # Windows
   ```

2. **Create GitHub Release**:
   - Go to your GitHub repository
   - Click "Releases" → "Create a new release"
   - Tag version: `v1.0.0`
   - Release title: `TMSE Pizza v1.0.0`
   - Upload `TMSE_Pizza.jar`
   - Add release notes

3. **Create download page** (optional):
   - Create `index.html` in repository root
   - Add download link and instructions

---

## Cost Estimate: $0 (All Free Tiers)

- **Hosting**: $0 (free tiers)
- **Domain**: $0 (use provided subdomain) or ~$10/year (custom domain)
- **Database**: $0 (free tiers)
- **CDN**: $0 (included with hosting)
- **SSL/HTTPS**: $0 (included)

---

## Recommendations

### For Immediate Deployment:
**Deploy Next.js app to Vercel** - It's already built and ready to go!

### For JavaFX Preservation:
**Use JPro Framework** - Maintains Java codebase while enabling web access

### For Maximum Compatibility:
**Hybrid Approach** - Offer both JAR download and web version

---

## Next Steps

1. ✅ **Choose deployment option**
2. ✅ **Test locally first**
3. ✅ **Set up hosting account**
4. ✅ **Deploy application**
5. ✅ **Test deployed version**
6. ✅ **Share URL with users**

---

## Additional Resources

- [Vercel Documentation](https://vercel.com/docs)
- [JPro Framework](https://jpro.one/)
- [JavaFX Web Deployment Guide](https://openjfx.io/openjfx-docs/)
- [Render Documentation](https://render.com/docs)
- [Railway Documentation](https://docs.railway.app/)

---

## Support

For deployment issues, refer to:
- Platform-specific documentation
- JavaFX Web deployment guides
- Next.js deployment guides

---

## Full Screen Implementation Notes

The application is configured to:
- Start in full screen mode automatically
- Maintain full screen throughout all window/tab transitions
- Allow minimizing (full screen is restored when window is restored)
- Prevent size changes when switching between tabs/windows

Full screen is set once at application startup (`PizzaApp.java`) and maintained via a listener that automatically restores full screen if it gets disabled.

---

**Last Updated**: 2024
**Maintained By**: TMSE Pizza Development Team

