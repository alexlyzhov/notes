import jxgrabkey.*;

public class Keys {
	private final static int LIST_ID = 0;
	private final static int NEW_ID = 1;
	// private final static int TAGS_ID = 2;
	// private final static int TRASH_ID = 3;
	private final static int EXIT_ID = 4;

	private JXGrabKey gk;
	private HotkeyListener listener;

	public Keys() {
		final Notes notes = Notes.getInstance();
		final NotesWindow notesWindow = notes.getWindow();
		System.loadLibrary("JXGrabKey");
		gk = JXGrabKey.getInstance();
		listener = new HotkeyListener() {
			public void onHotkey(int id) {
				switch(id) {
					case LIST_ID:
						notesWindow.toggleVisible();
						break;
					case NEW_ID:
						notes.createNote();
						break;
					// case TAGS_ID:
					// 	notesWindow.toggleTags();
					// 	break;
					// case TRASH_ID:
					// 	notesWindow.toggleTrash();
					// 	break;
					case EXIT_ID:
						notes.exit();
						break;
				}
			}
		};
		gk.addHotkeyListener(listener);
		try {
			gk.registerX11Hotkey(LIST_ID, X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.COLON); //Alt+Shift+:
			gk.registerX11Hotkey(NEW_ID, X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.APOSTROPHE); //Alt+Shift+'
			// gk.registerX11Hotkey(TAGS_ID, X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.BRACKET_RIGHT); //Alt+Shift+]
			// gk.registerX11Hotkey(TRASH_ID, X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.BRACKET_LEFT); //Alt+Shift+[
			gk.registerX11Hotkey(EXIT_ID, X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.Q); //Alt+Shift+Q
		} catch(HotkeyConflictException ex) {ex.printStackTrace();}
	}

	public void cleanUp() {
		new Thread() {
			public void run() {
				gk.unregisterHotKey(LIST_ID);
				gk.unregisterHotKey(NEW_ID);
				// gk.unregisterHotKey(TAGS_ID);
				// gk.unregisterHotKey(TRASH_ID);
				gk.unregisterHotKey(EXIT_ID);
				gk.removeHotkeyListener(listener);
				gk.cleanUp();
			} 
		}.start();
	}
}