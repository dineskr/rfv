# RFV – Personal Activity Logger (Android)

Design notes and requirements for an Android app that:
- Automatically logs call start/end times to calendar events synced with a self-hosted CalDAV server (with in-app CalDAV credentials UI).
- Tracks foreground app usage (screen time) in the background.
- Supports manual logs with editable start/end times, notes, and map-selected locations using an open-source map provider.
- Provides notifications and widgets for quick actions and active tracking.

See [docs/design.md](docs/design.md) for architecture, data model, permissions, and roadmap. The scaffolded main screen includes a manual log form with editable start/end times and a MapLibre picker for selecting coordinates, plus a CalDAV credentials editor that saves your server URL, username, token, and calendar ID into encrypted shared preferences.

## Project layout
- `app/` – Android app module using Jetpack Compose, Room, and WorkManager.
- `app/src/main/java/com/rfv/app` – Application bootstrap (`RfvApp`), tracking services, and UI entry point.
- `docs/` – Design documentation and requirements.

## Build
The project is configured for Android Studio Giraffe/Koala with Android Gradle Plugin 8.3, Kotlin 1.9, and Compose Material 3.
1. Download `gradle/wrapper/gradle-wrapper.jar` from Gradle 8.4 (e.g., via a machine with internet access) and place it under `gradle/wrapper/`.
2. Open the project in Android Studio.
3. Sync Gradle (requires Android SDK 34 installed).
4. Run the `app` configuration on a device or emulator (Android 8+). Permissions for calls, calendar, usage access, location, and foreground service are requested at runtime.

## Copy or push the project to your machine
Use whichever route is easiest for you to get the entire `/workspace/rfv` folder onto your computer before opening it in Android Studio.

**Zip the workspace**
- From a terminal in the parent directory: `zip -r rfv.zip rfv/`.
- Transfer `rfv.zip` to your computer, unzip it, then open the `rfv` folder in Android Studio.

**Push to your GitHub**
1. Create an empty repo on GitHub.
2. Inside `/workspace/rfv` run:
   - `git remote add origin <your-repo-url>`
   - `git branch -M main` (optional)
   - `git push -u origin main`
3. Clone that repo on your computer and open it in Android Studio.
