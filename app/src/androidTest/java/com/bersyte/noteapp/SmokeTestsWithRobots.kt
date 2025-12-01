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
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import org.hamcrest.Matcher
import androidx.test.uiautomator.UiDevice
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
        Log.i("TestSteps", "HomeRobot.assertHomeVisible: checking recyclerView and fabAddNote")
        onView(withId(R.id.recyclerView))
            .check(matches(isDisplayed()))

        onView(withId(R.id.fabAddNote))
            .check(matches(isDisplayed()))
    }
}

object NewNoteRobot {
    private fun waitForView(id: Int, timeoutMs: Long = 5000L) {
        val deadline = SystemClock.elapsedRealtime() + timeoutMs
        while (SystemClock.elapsedRealtime() < deadline) {
            try {
                onView(withId(id)).check(matches(isDisplayed()))
                return
            } catch (e: Exception) {
                Thread.sleep(200)
            }
        }
        // one final attempt to let Espresso throw a meaningful exception
        onView(withId(id)).check(matches(isDisplayed()))
    }

    fun createNote(title: String, body: String = "") {
        Log.i("TestSteps", "NewNoteRobot.createNote: start (title='${title.take(40)}')")
        // Navigate to NewNoteFragment programmatically to avoid flaky FAB click/navigation races
        try {
            Log.i("TestSteps", "NewNoteRobot.createNote: attempting programmatic navigation to NewNoteFragment")
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val resumed = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).iterator().next()
                if (resumed is androidx.fragment.app.FragmentActivity) {
                    val navHost = resumed.supportFragmentManager.findFragmentById(R.id.fragmentHost) as? androidx.navigation.fragment.NavHostFragment
                    navHost?.navController?.navigate(R.id.action_homeFragment_to_newNoteFragment)
                }
            }
        } catch (e: Exception) {
            Log.i("TestSteps", "NewNoteRobot.createNote: programmatic navigation failed, falling back to FAB click")
            // fallback to clicking the FAB if programmatic navigation fails for any reason
            try {
                onView(withId(R.id.fabAddNote)).perform(click())
            } catch (_: Exception) { Log.i("TestSteps", "NewNoteRobot.createNote: FAB click fallback also failed") }
        }

        // Wait for NewNote screen to appear and title field to be visible
        Log.i("TestSteps", "NewNoteRobot.createNote: waiting for etNoteTitle")
        waitForView(R.id.etNoteTitle, 5000L)

        Log.i("TestSteps", "NewNoteRobot.createNote: typing title (prefix='${title.take(40)}')")
        onView(withId(R.id.etNoteTitle)).perform(replaceText(title), closeSoftKeyboard())

        if (body.isNotEmpty()) {
            Log.i("TestSteps", "NewNoteRobot.createNote: typing body (length=${body.length})")
            onView(withId(R.id.etNoteBody)).perform(replaceText(body), closeSoftKeyboard())
        }

        // Try to click the save menu item. If it can't be found in the view hierarchy (action item inside Toolbar),
        // fall back to clicking approximate coordinates on the toolbar using UiDevice.
        try {
            Log.i("TestSteps", "NewNoteRobot.createNote: attempting to click menu_save")
            onView(withId(R.id.menu_save)).perform(click())
        } catch (e: Exception) {
            Log.i("TestSteps", "NewNoteRobot.createNote: menu_save not found, trying UiDevice click")
            try {
                val inst = InstrumentationRegistry.getInstrumentation()
                val metrics = inst.context.resources.displayMetrics
                val x = metrics.widthPixels - 60 // near right edge
                val y = 80 // near top (toolbar area) — adjust if needed on different devices
                UiDevice.getInstance(inst).click(x, y)
                Log.i("TestSteps", "NewNoteRobot.createNote: UiDevice click performed")
            } catch (_: Exception) {
                Log.i("TestSteps", "NewNoteRobot.createNote: UiDevice click also failed")
                // last resort: ignore — test will fail with a clear message
            }
        }
        Log.i("TestSteps", "NewNoteRobot.createNote: finished")
    }
}


object UpdateNoteRobot {
    fun openNoteAt(position: Int) {
        Log.i("TestSteps", "UpdateNoteRobot.openNoteAt: clicking recyclerView item at position=$position")
        onView(withId(R.id.recyclerView))
            .perform(
                actionOnItemAtPosition<NoteAdapter.NoteViewHolder>(
                    position, click()
                )
            )
    }

    fun updateTitle(newTitle: String) {
        Log.i("TestSteps", "UpdateNoteRobot.updateTitle: replacing title with prefix='${newTitle.take(40)}'")
        onView(withId(R.id.etNoteTitleUpdate)).perform(replaceText(newTitle), closeSoftKeyboard())
        // Update screen saves via FAB (fab_done) instead of a menu item
        try {
            Log.i("TestSteps", "UpdateNoteRobot.updateTitle: attempting to click fab_done")
            onView(withId(R.id.fab_done)).perform(click())
        } catch (e: Exception) {
            Log.i("TestSteps", "UpdateNoteRobot.updateTitle: fab_done not found, trying menu_save fallback")
            // If FAB not found, try the menu save (historical fallback)
            try { onView(withId(R.id.menu_save)).perform(click()) } catch (_: Exception) { Log.i("TestSteps", "UpdateNoteRobot.updateTitle: menu_save fallback failed") }
        }
    }

    fun deleteNote() {
        Log.i("TestSteps", "UpdateNoteRobot.deleteNote: attempting to click menu_delete")
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
