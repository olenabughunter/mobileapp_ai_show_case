MVVM Note App — Test Engineer Run Guide

Overview

This document explains how to run the app locally on macOS (same commands work on Linux with small path tweaks) and how to run instrumentation and unit tests on an emulator or connected device.

Prerequisites

- Android Studio (latest stable) or command-line SDK tools
- Java 11+ installed and JAVA_HOME set
- ANDROID_SDK_ROOT set to your Android SDK path (e.g. /Users/<you>/Library/Android/sdk)
- One Android Virtual Device (AVD) configured (API 29+ recommended)
- adb available on PATH (comes with Android SDK platform-tools)
- Gradle wrapper is included in the repo; use ./gradlew (Linux/macOS) or gradlew.bat (Windows)

Quick checklist

- [ ] Start AVD (emulator)
- [ ] Build the app (assembleDebug)
- [ ] Install the APK on the emulator
- [ ] Launch the app
- [ ] Run instrumentation tests (connectedAndroidTest)

Common paths and commands (macOS)

# From project root
PROJECT_ROOT="/Users/<you>/path/to/mvvm_note_app_kotlin_android_studio"
cd "$PROJECT_ROOT"

Start an emulator (example: Pixel_4_API_30)
$ANDROID_SDK_ROOT/emulator/emulator -avd Pixel_4_API_30 &
# Wait for it to boot: adb wait-for-device; adb shell getprop sys.boot_completed

Build debug APK
./gradlew :app:assembleDebug

Install the debug APK
# This will install the debug variant on the running emulator / device
$ANDROID_SDK_ROOT/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk

Launch the app (MainActivity)
$ANDROID_SDK_ROOT/platform-tools/adb shell am start -n com.bersyte.noteapp/.MainActivity

View logs while testing
$ANDROID_SDK_ROOT/platform-tools/adb logcat | grep -i "com.bersyte.noteapp" -n

Run instrumentation tests on connected device or emulator
# Prefer running the module-level connected task so Gradle targets the app module
./gradlew :app:connectedDebugAndroidTest

If you prefer a single test class run via adb (slower to set up):
# Replace with the correct test runner and test class path if required
$ANDROID_SDK_ROOT/platform-tools/adb shell am instrument -w \
  -e class com.bersyte.noteapp.SmokeTests \
  com.bersyte.noteapp.test/androidx.test.runner.AndroidJUnitRunner

Run unit tests (JVM)
./gradlew :app:testDebugUnitTest

Notes about running the provided smoke tests

- I applied a small change to app/src/androidTest/java/com/bersyte/noteapp/SmokeTestsWithRobots.kt to ensure lifecycle queries run on the main thread. This prevents "Querying activity state off main thread is not allowed." seen in prior test runs.
- The recommended connected test command is: ./gradlew :app:connectedDebugAndroidTest
- If tests still see NoActivityResumedException, ensure the emulator is fully booted and that animations are disabled (Developer options -> Window animation scale = 0, Transition animation scale = 0, Animator duration scale = 0) for stability.
- Clear app data between runs if stateful tests fail: $ANDROID_SDK_ROOT/platform-tools/adb shell pm clear com.bersyte.noteapp
- For CI, use an AVD image matching API>=29 and a device with enough RAM (2GB+). Use headless emulator flags if needed.

Troubleshooting

- "Task 'connectedDebugAndroidTest' not found" — run the command from the repository root and include the module path: ./gradlew :app:connectedDebugAndroidTest
- If Gradle fails due to Java version, set JAVA_HOME to a compatible JDK (11+ recommended).
- If build fails due to Android Gradle Plugin warnings, update Gradle / AGP or ignore if build succeeds.

What I changed in the repo (for transparency)

- Modified app/src/androidTest/java/com/bersyte/noteapp/SmokeTestsWithRobots.kt
  - The wait-loop now uses InstrumentationRegistry.getInstrumentation().runOnMainSync to query ActivityLifecycleMonitorRegistry on the main thread.

Next steps I can take if you want me to continue

- Push this local repository (with the test fix) to your GitHub. I need either:
  - The target repo HTTPS URL (empty repo) and a Personal Access Token (PAT) with repo permissions (I can push via HTTPS using the token) OR
  - Your confirmation to create a remote and instructions to use your machine's ssh key (you can add my SSH public key to allow push) OR
  - You can create a repo and give me its URL/permissions.

- Run the connected Android tests on a running emulator here (I attempted but the CI environment task invocation returned an error). If you'd like me to try again, please provide guidance or run the commands locally using this doc.

If you'd like, I can commit this file into the cloned repo and attempt to push. Please provide the destination GitHub repo (URL) and preferred method (HTTPS + PAT or SSH).
