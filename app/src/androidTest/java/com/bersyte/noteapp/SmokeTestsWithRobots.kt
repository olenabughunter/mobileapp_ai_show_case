package com.bersyte.noteapp

import android.os.SystemClock
import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.core.app.ActivityScenario
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.test.platform.app.InstrumentationRegistry
// replaced ActivityScenarioRule usage with explicit ActivityScenario.launch to avoid lifecycle races
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.bersyte.noteapp.adapter.NoteAdapter
import com.bersyte.noteapp.MainActivity
import com.bersyte.noteapp.R
import org.hamcrest.CoreMatchers.startsWith
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before
import org.junit.After
import org.junit.rules.TestName

// Robots / Page objects
object HomeRobot {
    fun assertHomeVisible() {
        onView(withId(R.id.recyclerView))
            .check(matches(isDisplayed()))

        onView(withId(R.id.fabAddNote))
            .check(matches(isDisplayed()))
    }
}

object NewNoteRobot {
    fun createNote(title: String, body: String = "") {
        onView(withId(R.id.fabAddNote)).perform(click())

        onView(withId(R.id.etNoteTitle)).perform(replaceText(title), closeSoftKeyboard())

        if (body.isNotEmpty()) {
            onView(withId(R.id.etNoteBody)).perform(replaceText(body), closeSoftKeyboard())
        }

        onView(withId(R.id.menu_save)).perform(click())
    }
}

object UpdateNoteRobot {
    fun openNoteAt(position: Int) {
        onView(withId(R.id.recyclerView))
            .perform(
                actionOnItemAtPosition<NoteAdapter.NoteViewHolder>(
                    position, click()
                )
            )
    }

    fun updateTitle(newTitle: String) {
        onView(withId(R.id.etNoteTitleUpdate)).perform(replaceText(newTitle), closeSoftKeyboard())
        onView(withId(R.id.menu_save)).perform(click())
    }

    fun deleteNote() {
        // Try tapping delete menu (if present in Update screen)
        onView(withId(R.id.menu_delete)).perform(click())
    }
}

@RunWith(AndroidJUnit4ClassRunner::class)
class SmokeTests {

    companion object {
        private const val TAG = "SmokeTestsPerf"
    }

    // Launch MainActivity explicitly to avoid ActivityScenario lifecycle races
    private var scenario: ActivityScenario<MainActivity>? = null

    @get:Rule
    val testName = TestName()

    private var startTime: Long = 0L

    @Before
    fun startTimer() {
        startTime = SystemClock.elapsedRealtime()
        Log.i(TAG, "Starting test: ${testName.methodName}")
        // Ensure activity is launched and wait until it reaches RESUMED stage
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Wait up to 5s for an activity to become RESUMED to avoid NoActivityResumedException
        val deadline = SystemClock.elapsedRealtime() + 5000L
        while (SystemClock.elapsedRealtime() < deadline) {
            val resumed = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED)
            if (!resumed.isEmpty()) break
            Thread.sleep(200)
        }
    }

    @After
    fun logDuration() {
        // Close the launched activity scenario to clean up
        try {
            scenario?.close()
        } catch (_: Exception) {}
        val duration = SystemClock.elapsedRealtime() - startTime
        Log.i(TAG, "Finished test: ${testName.methodName} durationMs=${duration}")
    }

    @Test
    fun s_sm_01_appLaunch_homeVisible() {
        HomeRobot.assertHomeVisible()
    }

    @Test
    fun s_sm_02_createNote_showsInList() {
        val title = "SmokeNote_" + System.currentTimeMillis()
        NewNoteRobot.createNote(title)

        // Verify a view containing the title (or its prefix) is displayed in the list
        onView(withText(startsWith("SmokeNote_"))).check(matches(isDisplayed()))
    }

    @Test
    fun s_sm_03_updateNote_changesTitle() {
        val title = "Orig_" + System.currentTimeMillis()
        val updated = "Updated_" + System.currentTimeMillis()

        NewNoteRobot.createNote(title)

        // Open first item
        UpdateNoteRobot.openNoteAt(0)

        // Update title and save
        UpdateNoteRobot.updateTitle(updated)

        // Verify updated title appears
        onView(withText(startsWith("Updated_"))).check(matches(isDisplayed()))
    }

    @Test
    fun s_sm_04_deleteNote_removesFromList() {
        val title = "Del_" + System.currentTimeMillis()

        NewNoteRobot.createNote(title)

        UpdateNoteRobot.openNoteAt(0)

        // Attempt delete (may show a confirmation dialog depending on implementation)
        try {
            UpdateNoteRobot.deleteNote()
        } catch (e: Exception) {
            // If delete menu not present, try pressing back and using long-press on list item as fallback
        }

        // After delete, the title should not be present
        onView(withText(title)).check(doesNotExist())
    }
}
