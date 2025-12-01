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

## Step-by-step: getting the project and running it in Android Studio

### 1) Copy the full project folder to your computer
Pick the method that fits your setup. Both give you the entire `rfv/` directory at once.

- **Zip (Linux/macOS)**: From the parent directory run `zip -r rfv.zip rfv/`. This compresses the folder so you can move one file instead of hundreds.
- **Zip (Windows PowerShell)**: Open PowerShell in the parent folder and run `Compress-Archive -Path rfv -DestinationPath rfv.zip`.
  - If `cd /workspace` fails on Windows (paths start with a drive letter), first run `Get-ChildItem` to see where you saved the folder, then `Set-Location <that-folder>` and run the `Compress-Archive` command.
- **Push to your GitHub**:
  1. Create an empty repo on GitHub.
  2. In the project folder run `git remote add origin <your-repo-url>`, then optionally `git branch -M main`, then `git push -u origin main`.
  3. Clone that repo on your computer.

### 2) Open in Android Studio (what each step does)
The project is configured for Android Studio Giraffe/Koala with Android Gradle Plugin 8.3, Kotlin 1.9, and Compose Material 3.

1. **Restore the Gradle wrapper JAR**: Download `gradle/wrapper/gradle-wrapper.jar` for Gradle 8.4 and place it under `gradle/wrapper/`. This lets the project use the exact Gradle version it expects.
2. **File > Open the `rfv` folder**: Opens the project and reads `settings.gradle.kts`/`build.gradle.kts` to know the modules and plugins.
3. **Let Gradle sync (needs Android SDK 34)**: Android Studio will download dependencies and generate Compose/Room code. If SDK 34 is missing, install it when prompted.
4. **Run the `app` configuration on a device/emulator (Android 8+)**: Builds the APK and installs it. On first launch Android will request permissions for calls, calendar, usage access, location, and foreground service so tracking and the map picker work.

### 3) Where things are once it opens
- Manual log + map picker + CalDAV credentials UI: `app/src/main/java/com/rfv/app/ui/` (`MainActivity`, `CaldavSettingsScreen`).
- Tracking services and receivers: `app/src/main/java/com/rfv/app/tracking/`.
- Data layer (Room entities/DAOs): `app/src/main/java/com/rfv/app/data/`.

