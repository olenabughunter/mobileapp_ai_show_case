package com.bersyte.noteapp

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bersyte.noteapp.db.NoteDatabase
import com.bersyte.noteapp.model.Note
import com.bersyte.noteapp.repository.NoteRepository
import com.bersyte.noteapp.viewmodel.NoteViewModel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Instrumented tests that validate repository + ViewModel behavior against an in-memory Room DB.
 * These tests exercise suspend DAO methods and LiveData observation in a deterministic way.
 */

@RunWith(AndroidJUnit4::class)
class RepositoryViewModelInstrumentedTests {

    private lateinit var db: NoteDatabase
    private lateinit var repository: NoteRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = NoteRepository(db)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun repository_insert_delete_update_and_query() = runBlocking {
        val note = Note(0, "RepoTestTitle", "RepoTestBody")

        // insert
        repository.insertNote(note)

        // query
        val notesAfterInsert = repository.getAllNotes().getOrAwaitValue()
        assertEquals(1, notesAfterInsert.size)
        val inserted = notesAfterInsert[0]
        assertEquals("RepoTestTitle", inserted.noteTitle)
        assertEquals("RepoTestBody", inserted.noteBody)

        // update
        val updated = inserted.copy(noteTitle = "RepoTestTitle_UPDATED", noteBody = "UpdatedBody")
        repository.updateNote(updated)
        val notesAfterUpdate = repository.getAllNotes().getOrAwaitValue()
        assertEquals(1, notesAfterUpdate.size)
        assertEquals("RepoTestTitle_UPDATED", notesAfterUpdate[0].noteTitle)

        // delete
        repository.deleteNote(notesAfterUpdate[0])
        val notesAfterDelete = repository.getAllNotes().getOrAwaitValue()
        assertEquals(0, notesAfterDelete.size)
    }

    @Test
    fun viewModel_add_and_observe_notes() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = NoteViewModel(app, repository)

        // Add a note via ViewModel (this launches on viewModelScope)
        val note = Note(0, "VM_Title", "VM_Body")
        vm.addNote(note)

        // Wait for LiveData to be updated and assert
        val all = vm.getAllNote().getOrAwaitValue()
        // Depending on scheduling, there may be 1 note now
        assertEquals(1, all.size)
        assertEquals("VM_Title", all[0].noteTitle)
    }

    // Helper to observe LiveData once with timeout — avoids test flakiness
    private fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 3,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)

        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                latch.countDown()
                // remove observer on main thread
                try {
                    InstrumentationRegistry.getInstrumentation().runOnMainSync {
                        this@getOrAwaitValue.removeObserver(this)
                    }
                } catch (_: Exception) {}
            }
        }

        // Register observer on the main thread to satisfy LiveData's requirements
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            this.observeForever(observer)
        }

        try {
            afterObserve.invoke()

            // Don't wait indefinitely if the LiveData is not set.
            if (!latch.await(time, timeUnit)) {
                throw TimeoutException("LiveData value was never set.")
            }

        } finally {
            try {
                InstrumentationRegistry.getInstrumentation().runOnMainSync {
                    this.removeObserver(observer)
                }
            } catch (_: Exception) {}
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}
