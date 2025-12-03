#!/usr/bin/env bash
set -euo pipefail

# Script: run_tests_and_generate_allure.sh
# Purpose: Run the Android instrumentation regression suite using Java 11 for this session,
# then generate an Allure HTML report from the results and open it (macOS).
# Usage: chmod +x run_tests_and_generate_allure.sh && ./run_tests_and_generate_allure.sh

JAVA11_HOME="/Library/Java/JavaVirtualMachines/temurin-11.jdk/Contents/Home"
export JAVA_HOME="$JAVA11_HOME"
echo "Using JAVA_HOME=$JAVA_HOME"

# Ensure Gradle daemon is restarted so it picks up JAVA_HOME
./gradlew --stop

# Run connected instrumentation tests for the RegressionSuite
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.bersyte.noteapp.RegressionSuite --stacktrace --info

# Generate Allure report if CLI is available
if command -v allure >/dev/null 2>&1; then
  echo "Generating Allure report from app/build/allure-results..."
  allure generate app/build/allure-results -o app/build/allure-report --clean
  echo "Allure report generated at app/build/allure-report/index.html"
  # Open in macOS default browser
  if command -v open >/dev/null 2>&1; then
    open app/build/allure-report/index.html
  else
    echo "Open the report file at: app/build/allure-report/index.html"
  fi
else
  echo "Allure CLI not found. Install it with: brew install allure"
  echo "Or generate report elsewhere with: allure generate app/build/allure-results -o app/build/allure-report --clean"
fi
