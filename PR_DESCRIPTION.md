This PR adds broader feature and edge-case instrumentation tests to improve coverage.

Files changed:
- app/src/androidTest/java/com/bersyte/noteapp/FeatureCoverageTests.kt
- app/src/androidTest/java/com/bersyte/noteapp/ExtraCoverageTests.kt

New tests added:
- fc_01_create_multiple_notes_and_search: create 3 notes and verify search filters
- fc_02_edit_note_body_and_verify: edit note body includes unicode and persists
- fc_03_delete_note_and_verify_absence_and_empty_message: delete flow and empty-state check
- fc_04_create_note_with_special_characters_and_verify_ui_encoding: unicode chars preserved
- fc_05_persistence_after_process_restart: simulate activity restart and verify persistence
- ec_01_create_long_note_shown: long title and body accepted and visible
- ec_02_empty_body_allowed_saves_note: allow saving empty body
- ec_03_search_clears_and_shows_all: clearing search restores full list
- ec_04_backstack_navigation_between_fragments: nav/backstack behavior between fragments
- ec_05_edittext_preserves_text_on_rotation: EditText state preserved across recreate
- ec_06_delete_note_via_update_screen: delete via update screen removes note

Notes and recommendations:
- I scoped some text-match assertions to the recyclerView to avoid AmbiguousViewMatcherException (toolbar/other views sometimes also matched prefixes).
- Local instrumentation runs on my machine failed due to a kapt/Java compatibility issue (Type V not present). Environment: Java 24, Gradle 6.5, Kotlin plugin 1.3.72; project uses ext.kotlin_version=1.4.32.
  Recommended: run instrumentation tests with Java 11 or 17 (set JAVA_HOME accordingly) or update Gradle/Kotlin plugin if you need to use newer JDK.

How to run (from project root):
1. export JAVA_HOME=$(/usr/libexec/java_home -v 11)
2. ./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.bersyte.noteapp.ExtraCoverageTests

Please review tests and let me know if you'd like them added to the CI/regression suite config. If you want, I can open a PR on GitHub (branch fix/ct_02_toast_flakiness is pushed).
