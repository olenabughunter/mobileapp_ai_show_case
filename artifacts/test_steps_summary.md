RegressionTests report (connected):
file:///Users/olena.imfeld/Desktop/mvvm_note_app_kotlin_android_studio/app/build/reports/androidTests/connected/flavors/debugAndroidTest/com.bersyte.noteapp.RegressionTests.html

Summary:
- Total tests: 4
- Failures: 0
- Total duration: 5.879s

Per-test timings (device: Pixel_9_Pro_XL(AVD) - API 16):
- f_reg_01_empty_title_validation — passed (0.989s)
- f_reg_02_navigation_back_stack — passed (1.244s)
- f_reg_03_room_persistence_across_restarts — passed (2.199s)
- f_reg_04_large_input_handling — passed (1.447s)

Extracted TestSteps log (excerpt) — shows robot-level runtime steps captured in emulator logcat during regression run:

98885:12-01 14:40:36.956 I/TestSteps( 7899): NewNoteRobot.createNote: start (title='Del_1764596436944')
98886:12-01 14:40:36.956 I/TestSteps( 7899): NewNoteRobot.createNote: attempting programmatic navigation to NewNoteFragment
98904:12-01 14:40:37.042 I/TestSteps( 7899): NewNoteRobot.createNote: waiting for etNoteTitle
99004:12-01 14:40:37.505 I/TestSteps( 7899): NewNoteRobot.createNote: typing title (prefix='Del_1764596436944')
99030:12-01 14:40:37.527 I/TestSteps( 7899): NewNoteRobot.createNote: attempting to click menu_save
99062:12-01 14:40:38.303 I/TestSteps( 7899): NewNoteRobot.createNote: finished
99063:12-01 14:40:38.303 I/TestSteps( 7899): UpdateNoteRobot.openNoteAt: clicking recyclerView item at position=0
99102:12-01 14:40:39.041 I/TestSteps( 7899): UpdateNoteRobot.deleteNote: attempting to click menu_delete
99681:12-01 14:40:40.737 I/TestSteps( 7899): HomeRobot.assertHomeVisible: checking recyclerView and fabAddNote
100223:12-01 14:40:42.108 I/TestSteps( 7899): NewNoteRobot.createNote: start (title='SmokeNote_1764596442108')
100224:12-01 14:40:42.108 I/TestSteps( 7899): NewNoteRobot.createNote: attempting programmatic navigation to NewNoteFragment
100226:12-01 14:40:42.112 I/TestSteps( 7899): NewNoteRobot.createNote: waiting for etNoteTitle
100303:12-01 14:40:42.571 I/TestSteps( 7899): NewNoteRobot.createNote: typing title (prefix='SmokeNote_1764596442108')
100329:12-01 14:40:42.588 I/TestSteps( 7899): NewNoteRobot.createNote: attempting to click menu_save
100357:12-01 14:40:43.323 I/TestSteps( 7899): NewNoteRobot.createNote: finished
100817:12-01 14:40:44.557 I/TestSteps( 7899): NewNoteRobot.createNote: start (title='Orig_1764596444557')
100818:12-01 14:40:44.557 I/TestSteps( 7899): NewNoteRobot.createNote: attempting programmatic navigation to NewNoteFragment
100819:12-01 14:40:44.561 I/TestSteps( 7899): NewNoteRobot.createNote: waiting for etNoteTitle
100889:12-01 14:40:45.021 I/TestSteps( 7899): NewNoteRobot.createNote: typing title (prefix='Orig_1764596444557')
100915:12-01 14:40:45.040 I/TestSteps( 7899): NewNoteRobot.createNote: attempting to click menu_save
100958:12-01 14:40:45.785 I/TestSteps( 7899): NewNoteRobot.createNote: finished
100959:12-01 14:40:45.785 I/TestSteps( 7899): UpdateNoteRobot.openNoteAt: clicking recyclerView item at position=0
100997:12-01 14:40:46.504 I/TestSteps( 7899): UpdateNoteRobot.updateTitle: replacing title with prefix='Updated_1764596444557'
101009:12-01 14:40:46.525 I/TestSteps( 7899): UpdateNoteRobot.updateTitle: attempting to click fab_done
103696:12-01 14:43:20.707 I/TestSteps( 8187): NewNoteRobot.createNote: start (title='Persist_1764596600704')
103697:12-01 14:43:20.707 I/TestSteps( 8187): NewNoteRobot.createNote: attempting programmatic navigation to NewNoteFragment
103699:12-01 14:43:20.715 I/TestSteps( 8187): NewNoteRobot.createNote: waiting for etNoteTitle
103797:12-01 14:43:21.260 I/TestSteps( 8187): NewNoteRobot.createNote: typing title (prefix='Persist_1764596600704')
103823:12-01 14:43:21.281 I/TestSteps( 8187): NewNoteRobot.createNote: attempting to click menu_save
103857:12-01 14:43:22.053 I/TestSteps( 8187): NewNoteRobot.createNote: finished
104742:12-01 14:43:23.836 I/TestSteps( 8187): NewNoteRobot.createNote: start (title='LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL')
104743:12-01 14:43:23.836 I/TestSteps( 8187): NewNoteRobot.createNote: attempting programmatic navigation to NewNoteFragment
104745:12-01 14:43:23.840 I/TestSteps( 8187): NewNoteRobot.createNote: waiting for etNoteTitle
104865:12-01 14:43:24.304 I/TestSteps( 8187): NewNoteRobot.createNote: typing title (prefix='LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL')
104891:12-01 14:43:24.341 I/TestSteps( 8187): NewNoteRobot.createNote: typing body (length=8000)
104904:12-01 14:43:24.362 I/TestSteps( 8187): NewNoteRobot.createNote: attempting to click menu_save
104939:12-01 14:43:25.102 I/TestSteps( 8187): NewNoteRobot.createNote: finished
105503:12-01 14:43:27.424 I/TestSteps( 8187): HomeRobot.assertHomeVisible: checking recyclerView and fabAddNote
105936:12-01 14:43:28.680 I/TestSteps( 8187): NewNoteR
