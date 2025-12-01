TESTING GUIDE — Senior Test Engineer

Purpose
This document is a compact, actionable testing strategy and test concept for the MVVM Note Android app (no code changes). It is written for a senior test/automation engineer who will design and implement automated tests, run them locally and in CI, and hand over to the team.

Prerequisites (environment)
- macOS / Linux with JDK 11 installed (Temurin/OpenJDK 11). Verify: java -version
- Android SDK with platform-tools, emulator and build-tools. Ensure ANDROID_SDK_ROOT is set, e.g.
  export ANDROID_SDK_ROOT=/Users/olena.imfeld/Library/Android/sdk
- An AVD (recommended: Pixel_9_Pro_XL or a Play-backed AVD with Android 13+)
- Gradle wrapper is included in repo: ./gradlew

Quick run (manual) — validated steps
1) Make wrapper executable: chmod +x ./gradlew
2) Start emulator (example): $ANDROID_SDK_ROOT/emulator/emulator -avd Pixel_9_Pro_XL -gpu host &
3) Confirm device: $ANDROID_SDK_ROOT/platform-tools/adb devices -l
4) Build debug APK: ./gradlew clean :app:assembleDebug
5) Install: ./gradlew :app:installDebug  OR adb install -r app/build/outputs/apk/debug/app-debug.apk
6) Launch: $ANDROID_SDK_ROOT/platform-tools/adb shell am start -n com.bersyte.noteapp/.MainActivity
7) Read logs: $ANDROID_SDK_ROOT/platform-tools/adb logcat | grep -i "com.bersyte.noteapp" -n

Objectives
- Provide a clear automation approach that requires no changes to the app under test.
- Create reliable instrumentation (UI) tests that exercise key user flows and edge cases.
- Provide unit-test and Robolectric guidance where appropriate (logic-only tests).
- Integrate tests into CI so build verification includes test runs.

Scope
In scope:
- Instrumentation UI tests for core flows (create/edit/delete/search notes, navigation, toolbar behavior).
- Unit tests / Robolectric for ViewModel, Repository and DAO logic where feasible without changing production code.
- Smoke and regression suites.

Out of scope:
- App code modifications, feature additions, or deep performance profiling beyond quick checks.

Test Strategy — Approach & Tools
- UI/Instrumentation tests: Espresso + AndroidX Test (AndroidJUnitRunner). These tests will run as connectedAndroidTest on emulators.
- Unit tests: JUnit4/JUnit5 + Robolectric for fast JVM-level testing for viewmodel/repository business logic.
- Mocks: MockK for mocking repositories or DAOs in unit tests (no production modification required).
- Test runner: Gradle wrapper and connectedAndroidTest for instrumentation; ./gradlew test for unit tests.
- Test data: Use ephemeral in-memory DB (Room supports this in tests) or use unique test data to avoid cross-test interference.
- Reporting: Use Gradle test reports and AndroidJUnit XML output. Integrate with CI test reporters (JUnit XML to GitHub Actions/GitLab/Jenkins).

Automation Architecture — Recommended Structure
- Keep tests under app/src/androidTest for instrumentation and app/src/test for unit tests (existing structure).
- Follow Page Object / Robot pattern to make Espresso tests robust to UI changes. Example layers:
  - Screens / Robots: encapsulate UI actions and assertions
  - Tests: compose robot calls into flows
  - Test utilities: common setup/teardown, IdlingResource helpers if needed
- Keep tests independent and idempotent. Use @Before to install fresh DB state or clear repository.

Prioritized Test Suites & Cases (ready to automate)
A. Sanity / Smoke (fast)
- S-SM-01: App launches -> Home fragment visible (note list shown or empty state)
  Steps: Launch app; Assert HomeFragment root view displayed.
  Expected: Home fragment visible; add-note FAB present.

- S-SM-02: Create note (happy path)
  Steps: Tap FAB -> NewNote screen -> enter title/body -> Save
  Expected: Note appears in list with title and snippet; toast or confirmation shown.

- S-SM-03: Update note
  Steps: Create note; tap note -> Update screen -> change title -> Save
  Expected: Updated title visible in list.

- S-SM-04: Delete note
  Steps: Create note; long-press or select delete -> confirm
  Expected: Note removed from list.

B. Functional / Regression (medium)
- F-REG-01: Empty title validation
  Steps: Open new note; leave title empty; attempt Save
  Expected: Validation prevents save, shows error (toast or field error).

- F-REG-02: Search notes
  Steps: Create multiple notes; use search tool; query substring
  Expected: Only matching notes displayed.

- F-REG-03: Navigation back stack
  Steps: Navigate NewNote -> press back
  Expected: Return to Home without creating note (or confirm discard flow if present).

- F-REG-04: Room persistence across restarts
  Steps: Create notes; close app; reopen
  Expected: Notes persist in list.

C. Edge / Negative (lower priority)
- E-NEG-01: Large input handling (long title/body)
- E-NEG-02: Simulated low-memory or rotation (screen rotation preserves content)
- E-NEG-03: DB concurrency (if app uses background threads) — verify no crashes under multiple quick writes

Test Data and Isolation
- Use clear-and-seed approach: each test starts by clearing the DB or using an in-memory DB. Use unique titles (timestamp or random) so parallel runs do not collide.
- For instrumentation tests that must run on connectedAndroidTest, make sure to uninstall app or clear app data between runs if persistent state affects results: adb shell pm clear com.bersyte.noteapp

Implementation Notes & Examples
- Use AndroidJUnitRunner and rules: @RunWith(AndroidJUnit4::class), ActivityScenarioRule(MainActivity::class.java)
- Example Gradle commands:
  - Unit tests: ./gradlew test
  - Instrumentation tests (connected emulator): ./gradlew connectedAndroidTest
- Use Espresso IdlingResource if background threads cause flakiness. Prefer using CountingIdlingResource exposed via test-only hooks if available — otherwise rely on UI synchronization with Espresso (ViewMatchers/await).

CI Integration (high level)
- Use a CI job that:
  1) Sets up JDK 11 and Android SDK
  2) Starts an AVD or uses a hosted emulator service (GitHub Actions: reactiveandroid/emulator-action or emulator-runner)
  3) Runs ./gradlew assembleDebug connectedAndroidTest --no-daemon --stacktrace
  4) Collects test output: build/reports/androidTests/connected/index.html and intermediates/ or junit XML. Fail job on test failures.
- Keep a fast smoke suite that runs on every PR; run full regression nightly or on release branches.

Reporting & Flakiness Handling
- Capture screenshots and device logs on test failures (adb logcat snapshot and screenshot via adb exec-out screencap -p > fail.png).
- Use retries sparingly (CI-level retry of flaky test job only after addressing root cause).

Handover Checklist for Implementation
- [ ] Create robots/page objects for Home, NewNote, UpdateNote.
- [ ] Implement smoke tests (S-SM-01..04) in app/src/androidTest.
- [ ] Add unit tests for NoteViewModel and NoteRepository in app/src/test.
- [ ] Add GitHub Actions job to run smoke suite against emulator.

Where this file is located
/Users/olena.imfeld/Desktop/mvvm_note_app_kotlin_android_studio/TESTING_GUIDE_SENIOR.md

How I validated these instructions
- Steps are based on TESTING.md in repo which contains exact commands to run emulator, build and install APK. The test cases map to implemented screens in app/src/main/res/layout and fragments (HomeFragment, NewNoteFragment, UpdateNoteFragment).

If you want I can:
- Generate starter Espresso test files (robots + one smoke test) placed under app/src/androidTest.
- Add a GitHub Actions workflow file that runs the smoke suite on a hosted emulator.

Contact
If anything in CI or emulator setup fails, provide the Gradle output and adb logcat to diagnose.
