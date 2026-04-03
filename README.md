# Umbrel Android (v0.1)

Umbrel Android transforms your phone into a portable home server. Running on a proot-based Linux environment, it allows you to self-host apps like Bitcoin, Nextcloud, and more, directly on your Android device.

## Features
- **Proot Runtime**: Run Linux services in user-space without root access.
- **Service Management**: Start, stop, and monitor services with ease.
- **App Store**: Install and manage Umbrel apps using a translation layer for existing `docker-compose.yml` manifests.
- **Network Gateway**: Access your services via `phone-ip:port` through a built-in reverse proxy.

## Development
The project is built using Kotlin and Jetpack Compose.

To build the project locally, you will need the Android SDK and JDK 17.

```bash
cd packages/android/umbrel-app
./gradlew assembleDebug
```

## CI/CD
The repository includes GitHub Workflows for:
- Manual Debug APK generation.
- Manual Signed Release APK generation with GitHub Release integration.
- Automated Lint and Unit Testing on Pull Requests.

