# Running the mvvm_note_app_kotlin_android_studio (Test engineer instructions)

Prerequisites
- JDK 11 installed (Temurin/OpenJDK 11)
- Android Studio or Android SDK + emulator
- Android SDK Platform 30 and an AVD or a physical device

Quick CLI steps
1. Clone repository:
   git clone https://github.com/IsaiasCuvula/mvvm_note_app_kotlin_android_studio.git
   cd mvvm_note_app_kotlin_android_studio

2. Make gradlew executable (macOS/Linux):
   chmod +x ./gradlew

3. Set JAVA_HOME to JDK 11 (macOS example):
   export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-11.jdk/Contents/Home

4. Start an emulator (via Android Studio AVD Manager or):
   $ANDROID_SDK_ROOT/emulator/emulator -avd <AVD_NAME>

5. Build:
   ./gradlew assembleDebug

6. Install on emulator/device:
   ./gradlew installDebug
   or
   adb install -r app/build/outputs/apk/debug/app-debug.apk

7. Launch the app on the emulator/device.

Notes/Troubleshooting
- If kapt fails with Room native library errors on Apple Silicon (aarch64), ensure you are using JDK 11 and that mavenCentral() is available in buildscript and allprojects (this repo already includes the necessary fix).
- Schema export warning from Room is benign for running; to export schemas set kapt argument room.schemaLocation (optional).
- Prefer using the Gradle wrapper shipped with the project (./gradlew).

Contact
- For issues reproducing or CI setup, provide the full Gradle output and java -version.
