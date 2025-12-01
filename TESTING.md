TEST ENGINEER RUN NOTES

This document explains exact steps to run the mvvm_note_app_kotlin_android_studio app on macOS (the commands also work on Linux with small path changes).

Prerequisites
- JDK 11 (Temurin/OpenJDK 11). Verify with: java -version (should report a 11.x runtime).
- Android SDK installed (platform-tools, emulator, build-tools). Ensure $ANDROID_SDK_ROOT is set. Example: export ANDROID_SDK_ROOT=/Users/olena.imfeld/Library/Android/sdk
- An AVD configured (recommended: Pixel_9_Pro_XL or a Play-backed AVD with at least Android 13+).
- Gradle wrapper (./gradlew included in the repo).

Quick start (copy & paste)
1) Clone the repo (if not already present):
   git clone https://github.com/IsaiasCuvula/mvvm_note_app_kotlin_android_studio.git
   cd mvvm_note_app_kotlin_android_studio

2) Ensure gradle wrapper is executable:
   chmod +x ./gradlew

3) Confirm Java 11 is used (macOS example):
   export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-11.jdk/Contents/Home
   java -version

4) Start an emulator (Android Studio → AVD Manager or CLI):
   $ANDROID_SDK_ROOT/emulator/emulator -avd Pixel_9_Pro_XL -gpu host &
   # Confirm device with:
   $ANDROID_SDK_ROOT/platform-tools/adb devices -l

5) Build the debug APK using the included wrapper:
   ./gradlew clean :app:assembleDebug

6) Install the APK to the running emulator:
   ./gradlew :app:installDebug
   # Or install directly with adb:
   $ANDROID_SDK_ROOT/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk

7) Launch the app on the emulator (if it does not auto-launch):
   $ANDROID_SDK_ROOT/platform-tools/adb shell am start -n com.bersyte.noteapp/.MainActivity

8) View logs while testing:
   $ANDROID_SDK_ROOT/platform-tools/adb logcat | grep -i "com.bersyte.noteapp" -n

Key troubleshooting tips
- If the emulator process is running but the window is not visible: check Mission Control or cmd+tab. You can attempt to bring the GUI to front via AppleScript if needed on macOS.
- If you get kapt/Room/RoomSchema errors on Apple Silicon: ensure you are using JDK 11. The project includes mavenCentral() in buildscript which fixes many kapt issues.
- If Gradle fails due to missing SDK components: install missing platform(s) via SDK Manager or Android Studio.
- If adb device appears as "offline" or "unauthorized": restart the emulator and run adb kill-server; adb start-server.

How I validated the repo locally (what I did)
- Cloned upstream repository to /Users/olena.imfeld/Desktop/mvvm_note_app_kotlin_android_studio
- Started an AVD and verified emulator attached (adb devices)
- Built the project using ./gradlew assembleDebug and installed the debug APK
- Launched the MainActivity and confirmed the app is resumed on the emulator

Pushing this local copy to your GitHub
I did not push to your GitHub because you didn't provide a target remote. You have two safe options:

Option A — you create the empty GitHub repo and push from your machine:
  1. Create an empty repo on github.com (do NOT initialize with a README).
  2. From the project directory on your machine:
     git remote add myrepo <YOUR_REPO_URL>
     git push myrepo master --set-upstream

Option B — create and push with GitHub CLI (if gh is installed & authenticated):
  gh repo create mvvm_note_app_kotlin_android_studio --public --confirm
  git push origin master --set-upstream

If you want me to push from this environment, provide the new repo HTTPS or SSH URL and confirm and I will add the remote and push.

Contact & Logs
- If you hit errors, copy & paste the full Gradle build output and the output of java -version.
- While reproducing issues, include adb logcat lines and emulator stdout/stderr logs (I saved emulator logs to /tmp/emulator_gui.log on my run).

Notes
- Commands in this file use absolute SDK tool paths where helpful: replace $ANDROID_SDK_ROOT with your SDK path if not set.
- The project uses AndroidX, Room, and Kotlin/Kapt. Using the provided Gradle wrapper avoids most environment mismatch issues.
