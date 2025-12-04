package com.bersyte.noteapp

import android.os.SystemClock
import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.Matchers.allOf
import com.bersyte.noteapp.adapter.NoteAdapter
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

/*
 Regression tests following TESTING_GUIDE_SENIOR.md
 Tests assume robots/page objects in SmokeTestsWithRobots.kt are available
*/

class RegressionTests {
    companion object {
        private const val TAG = "RegressionTestsPerf"
    }

    private var scenario: ActivityScenario<MainActivity>? = null

    @get:Rule
    val testName = TestName()

    private var startTime: Long = 0L

    @Before
    fun setUp() {
        startTime = SystemClock.elapsedRealtime()
        Log.i(TAG, "Starting test: ${'$'}{testName.methodName}")
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        try { scenario?.close() } catch (_: Exception) {}
        val duration = SystemClock.elapsedRealtime() - startTime
        Log.i(TAG, "Finished test: ${'$'}{testName.methodName} durationMs=${'$'}duration")
    }

    @Test
    fun f_reg_01_empty_title_validation() {
        // Attempt to create a note with an empty title — expected: validation prevents save and NewNote screen remains
        // Programmatic navigation + attempt save
        NewNoteRobot.createNote("")

        // If validation prevented save, the title input should still be visible on the NewNote screen
        onView(withId(R.id.etNoteTitle)).check(matches(isDisplayed()))
    }

    @Test
    fun f_reg_02_navigation_back_stack() {
        // Navigate to NewNote and press back, expect Home visible
        try {
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val resumed = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).iterator().next()
                if (resumed is androidx.fragment.app.FragmentActivity) {
                    val navHost = resumed.supportFragmentManager.findFragmentById(R.id.fragmentHost) as? androidx.navigation.fragment.NavHostFragment
                    navHost?.navController?.navigate(R.id.action_homeFragment_to_newNoteFragment)
                }
            }
        } catch (_: Exception) {
            // fallback: click FAB
            try { onView(withId(R.id.fabAddNote)).perform(androidx.test.espresso.action.ViewActions.click()) } catch (_: Exception) {}
        }

        // Wait briefly for UI
        SystemClock.sleep(500)

        // Press back
        androidx.test.espresso.Espresso.pressBack()

        // Home should be visible
        HomeRobot.assertHomeVisible()
    }

    @Test
    fun f_reg_03_room_persistence_across_restarts() {
        val title = "Persist_" + System.currentTimeMillis()
        NewNoteRobot.createNote(title)

        // Close and relaunch activity to simulate app restart
        try { scenario?.close() } catch (_: Exception) {}
        SystemClock.sleep(300)
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Verify note persists in list
        try {
            onView(withId(R.id.recyclerView)).perform(
                androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem<NoteAdapter.NoteViewHolder>(
                    hasDescendant(withText(startsWith("Persist_"))), androidx.test.espresso.action.ViewActions.click()
                )
            )
            onView(withId(R.id.etNoteTitleUpdate)).check(matches(withText(startsWith("Persist_"))))
            } catch (_: Exception) {
            onView(withId(R.id.recyclerView)).check(matches(hasDescendant(allOf(withId(R.id.tvNoteTitle), withText(startsWith("Persist_"))))))
        }
    }

    @Test
    fun f_reg_04_large_input_handling() {
        val longTitle = "L".repeat(2000)
        val longBody = "B".repeat(8000)

        NewNoteRobot.createNote(longTitle, longBody)

        // Verify that an item with long title prefix is visible in the list
        // Check prefix to avoid matching entire long string
        try {
            onView(withId(R.id.recyclerView)).perform(
                androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem<NoteAdapter.NoteViewHolder>(
                    hasDescendant(withText(startsWith("L"))), androidx.test.espresso.action.ViewActions.click()
                )
            )
            onView(withId(R.id.etNoteTitleUpdate)).check(matches(withText(startsWith("L"))))
        } catch (_: Exception) {
            onView(withId(R.id.recyclerView)).check(matches(hasDescendant(allOf(withId(R.id.tvNoteTitle), withText(startsWith("L"))))))
        }
    }
}
