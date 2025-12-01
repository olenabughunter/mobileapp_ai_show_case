Test Engineers - How to run mvvm_note_app_kotlin_android_studio (Updated: Start & interact with the app)

Purpose
- Short, explicit steps to start the app and interact with it on an emulator or device for manual testing.

Prerequisites
- macOS with Android Studio or Android SDK (adb, emulator) installed and in PATH
- Java 11 (OpenJDK/Temurin 11) available (used for CLI builds)
- At least one AVD configured (see `emulator -list-avds`)

Quick CLI steps (interactive / foreground emulator)
1. Ensure emulator is available:
   emulator -list-avds
   Example output: Pixel_9_Pro_XL

2. Start an emulator in the foreground (so you can see and interact with it):
   emulator -avd Pixel_9_Pro_XL -netdelay none -netspeed full
   - This runs the emulator in the terminal and opens the emulator window. Do not run with -no-window.

3. Build the debug APK (from project root):
   export JAVA_HOME=$(/usr/libexec/java_home -v 11)
   ./gradlew assembleDebug
   - The built APK will be at: app/build/outputs/apk/debug/app-debug.apk

4. Verify the emulator is connected:
   adb devices -l
   - Look for a device like `emulator-5554   device`.

5. Install (or reinstall) the APK on the running emulator:
   adb install -r app/build/outputs/apk/debug/app-debug.apk

6. Launch the app (explicit Activity start):
   adb shell am start -n com.bersyte.noteapp/.MainActivity
   - If the activity or package changes, update the above component name accordingly.

7. (Optional) Bring the app to the foreground if it’s already running:
   adb shell am start -S -n com.bersyte.noteapp/.MainActivity

8. Verify the app process and focused window:
   adb shell pidof com.bersyte.noteapp
   adb shell dumpsys window windows | grep -E 'mCurrentFocus|mFocusedApp'

9. Capture logs while reproducing issues:
   adb logcat -v time | grep -i bersyte
   - Open a separate terminal for logcat so it keeps streaming.

Manual / Android Studio steps
- Open the project root in Android Studio.
- Let Gradle sync using the bundled Gradle wrapper.
- Start an AVD from Device Manager (double-click an AVD to open it) — select the Pixel_9_Pro_XL or create one.
- Use the Run menu (Shift+F10) or the green Run button to install & run the app on the selected device.
- You can interact with the running emulator window directly.

How to open the app from the emulator UI (if not started via adb/Run):
- Open the app drawer on the emulator screen and tap the app icon (NoteApp / the app package label).
- If you can’t find the icon, open Settings -> Apps, find the app package (com.bersyte.noteapp) and tap Open.

Useful verification commands
- List installed packages containing 'noteapp':
  adb shell pm list packages | grep noteapp
- Show apk path on device:
  adb shell pm path com.bersyte.noteapp

Troubleshooting
- Gradle / Java compatibility: use Java 11 for builds if you encounter classfile version errors.
- Emulator fails to start or shows graphics errors: re-create AVD with a different device or system image, try -gpu host or -gpu swiftshader_indirect.
- If adb reports ‘device offline’ or no device: restart adb server:
  adb kill-server && adb start-server
- If the app doesn't launch: confirm package & activity with `aapt dump badging app/build/outputs/apk/debug/app-debug.apk` or check AndroidManifest.

Extras for automation / test scripting
- Install & launch combined (one-liner):
  adb install -r app/build/outputs/apk/debug/app-debug.apk && adb shell am start -n com.bersyte.noteapp/.MainActivity
- UI dump for locating elements (useful for automated clicks):
  adb shell uiautomator dump /sdcard/uidump.xml && adb pull /sdcard/uidump.xml .

If anything in the package or main activity name is different in future versions, replace com.bersyte.noteapp/.MainActivity above with the correct component. Contact the code owner if you need a signed release build or CI tokens.
