Test Cases Summary - mobileapp_ai_show_case

Generated: 2025-12-02

Assumptions
- Tests use Espresso + UiAutomator fallbacks and run against MainActivity on emulator/device.
- Robots/page objects: NewNoteRobot, UpdateNoteRobot, HomeRobot exist and are used.
- DB is Room; ordering is typically newest-first (tests use prefixes).
- Toasts are flaky on some emulators; UI assertions preferred where possible.

Scope
This document extracts and formalizes the existing instrumentation & unit tests into structured test cases (title, objective, preconditions, steps, expected result, priority, automation feasibility, notes). No tests were modified or added — this is a specification only.

1) Smoke tests
- s_sm_01_appLaunch_homeVisible
  Objective: Verify app launches and home screen elements present
  Preconditions: App installed; able to launch MainActivity
  Steps: Launch MainActivity; assert recyclerView visible; assert fabAddNote visible
  Expected: RecyclerView and FAB displayed
  Priority: High (smoke)
  Feasibility: Easy
  Notes: Keep lightweight; good CI smoke.

- s_sm_02_createNote_showsInList
  Objective: Create note and confirm it appears in list
  Preconditions: App at Home
  Steps: createNote(title) via NewNoteRobot; assert list contains title prefix
  Expected: New item displayed in list
  Priority: High
  Feasibility: Easy–Moderate
  Notes: Use unique prefix; consider cleanup.

- s_sm_03_updateNote_changesTitle
  Objective: Verify update flow persists title change
  Preconditions: Note created (test creates it)
  Steps: createNote(orig); open first item; update title; save; assert updated title visible
  Expected: Updated title present in list
  Priority: High
  Feasibility: Moderate

- s_sm_04_deleteNote_removesFromList
  Objective: Delete a note and ensure removed
  Preconditions: Note created
  Steps: createNote; open update; delete (or fallback long-press); assert title not present
  Expected: Title absent
  Priority: High
  Feasibility: Moderate

2) Comprehensive / E2E
- ct_01_full_crud_flow
  Objective: Create → verify → open → update → verify → delete → verify absence and empty-state
  Preconditions: App launched; clean/known state
  Steps: NewNoteRobot.createNote(title, body); verify list; open; assert fields; update & save; verify updated; delete; assert absence; check empty-state if applicable
  Expected: CRUD operations succeed, UI reflects changes
  Priority: High
  Feasibility: Moderate
  Notes: Long test — consider splitting.

- ct_02_empty_title_shows_toast
  Objective: Validation prevents save with empty title and shows toast "Please enter note title"
  Preconditions: On NewNote screen
  Steps: Attempt save with empty title (NewNoteRobot.createNote("") ); poll for toast up to 3s
  Expected: Toast with expected text appears; NewNote screen remains
  Priority: High
  Feasibility: Moderate→Hard
  Notes: Toasts can be flaky; prefer UI validation assertion instead.

- ct_03_search_filters_list
  Objective: Search filters notes list
  Preconditions: Multiple notes created
  Steps: create notes base+A/B/C; open search; enter base+B; assert B visible
  Expected: Matching note visible (optionally only match)
  Priority: Medium
  Feasibility: Moderate

- ct_04_rotate_preserves_notes
  Objective: Ensure notes persist after activity recreate/rotation
  Preconditions: Note created
  Steps: createNote; assert visible; scenario.recreate(); assert still visible
  Expected: Note still displayed after recreate
  Priority: Medium
  Feasibility: Moderate

3) Regression tests
- f_reg_01_empty_title_validation
  Objective: Save prevented for empty title (NewNote remains)
  Preconditions: On NewNote
  Steps: NewNoteRobot.createNote(""); assert etNoteTitle displayed
  Expected: Save blocked; title field still visible
  Priority: High
  Feasibility: Easy

- f_reg_02_navigation_back_stack
  Objective: Back from NewNote returns to Home
  Preconditions: On NewNote
  Steps: navigate to NewNote; press back; assert Home visible
  Expected: Home shown
  Priority: Medium
  Feasibility: Easy

- f_reg_03_room_persistence_across_restarts
  Objective: Verify Room persistence across activity restart
  Preconditions: App launched
  Steps: createNote(title); close & relaunch activity; assert note visible
  Expected: Note persisted
  Priority: High
  Feasibility: Moderate

- f_reg_04_large_input_handling
  Objective: App handles very large title/body inputs
  Preconditions: App launched
  Steps: createNote(longTitle (2000 chars), longBody (8000 chars)); assert prefix visible
  Expected: Item displayed; app doesn't crash
  Priority: Low–Medium
  Feasibility: Moderate

4) Full Coverage tests (broader scenarios)
- fc_01_create_multiple_notes_and_search — bulk create & search
- fc_02_edit_note_body_and_verify — edit body & unicode
- fc_03_delete_note_and_verify_absence_and_empty_message — delete & empty-state
- fc_04_create_note_with_special_characters_and_verify_ui_encoding — unicode encoding
- fc_05_persistence_after_process_restart — persistence after process/activity restart
(Each follows same structure: create → action → assert)

5) Extra coverage (edge cases)
- ec_01_create_long_note_shown — long texts
- ec_02_empty_body_allowed_saves_note — body optional
- ec_03_search_clears_and_shows_all — verify clear search returns full list
- ec_04_backstack_navigation_between_fragments — navigation/backstack
- ec_05_edittext_preserves_text_on_rotation — preserve typed text after recreate
- ec_06_delete_note_via_update_screen — delete via update screen

6) HomeFragment & Repository / ViewModel & Unit tests
- HomeFragment: empty-state card view, recycler visibility, selection/back navigation checks
- RepositoryViewModelInstrumentedTests: DB CRUD & ViewModel observation
- NoteViewModelUnitTest: unit test for ViewModel to delegate to repository

Recommendations (short)
- Prefer UI-element assertions over toasts; if toast required use UiAutomator + retries.
- Split long E2E into smaller focused tests.
- Ensure DB isolation/cleanup between tests (in-memory or test DB per run).
- Add IdlingResources or explicit waits rather than Thread.sleep where possible.
- Keep robots small and deterministic, use explicit waitForView with timeout (already implemented in NewNoteRobot).
- Capture screenshots/logs on failure and attach to Allure (already done — ensure consistent naming).

Next steps suggested (I can implement on request)
- Deliver prioritized test matrix (list of tests to include in smoke/regression/full, with estimated runtimes & flakiness risk).
- Export these test cases into CSV/XLSX for your test management tool.
- Propose small code/test changes to reduce flakiness for toast validation (e.g., assert UI validation id or add test-only wrapper).
