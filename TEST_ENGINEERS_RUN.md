Test Engineers - How to run mvvm_note_app_kotlin_android_studio

Prerequisites
- macOS with Android Studio or SDK installed
- Java 11 (OpenJDK 11 / Temurin 11) set as JAVA_HOME when building from CLI
- Android SDK tools (adb, emulator) in PATH
- At least one AVD configured (use `emulator -list-avds`)

Quick steps (CLI)
1. Clone repository:
   git clone https://github.com/IsaiasCuvula/mvvm_note_app_kotlin_android_studio.git
2. Use Java 11 for the build (example):
   export JAVA_HOME=$(/usr/libexec/java_home -v 11)
3. Build the debug APK:
   ./gradlew assembleDebug
4. Start an emulator if not running:
   emulator -avd Pixel_9_Pro_XL &
5. Install the APK:
   adb install -r app/build/outputs/apk/debug/app-debug.apk
6. Launch the app:
   adb shell am start -n com.bersyte.noteapp/.MainActivity

Notes / Troubleshooting
- If Gradle fails with errors about unsupported class file version, ensure you run the build with Java 11 (not Java 24).
- The project originally used jcenter(); that repository has been removed and the top-level build.gradle was updated to use mavenCentral() instead.
- If kapt warns about room schema export, you can provide annotation processor arg:
  -Proom.schemaLocation=app/schemas

Using Android Studio
- Open the project directory in Android Studio.
- Android Studio will prompt to use a compatible Gradle wrapper; accept and let it sync.
- Run on an AVD or connected device using the Run action.

CI
- The repository is pinned to Gradle 6.5 via gradle/wrapper/gradle-wrapper.properties for compatibility.
