# Umbrel Android

Transform your Android phone into a portable home server. Self-host apps like Bitcoin, Nextcloud, and more, directly on your mobile device using a secure, proot-based Linux environment.

## 🚀 Features
- **Pocket NAS**: Share files over your local network via SMB.
- **App Ecosystem**: Run standard Umbrel apps via an intelligent docker-compose translation layer.
- **Proot Runtime**: High-performance Linux services running in user-space (no root required).
- **Network Gateway**: Built-in reverse proxy for easy service access.

## 🛠️ Development

### Requirements
- JDK 17
- Android SDK
- Gradle 8.2

### Build Locally
```bash
# Build Debug APK
./npm run build:debug
```

### Run Tests
```bash
npm run test
```

## ⛓️ CI/CD Pipeline
The repository uses a unified GitHub Actions pipeline (`android-pipeline.yml`) for:
1. **Validation**: Automated linting and unit testing on every PR and push.
2. **Debug Builds**: Automatic APK generation on pushes to the main branch.
3. **Releases**: Manual, signed production releases with automatic versioning and GitHub Release integration.

---
Built with ❤️ for the Umbrel Community.
