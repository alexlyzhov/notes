import jxgrabkey.*;

public class Keys {
	private final static int LIST_ID = 0;
	private final static int NEW_ID = 1;
	private final static int TAGS_ID = 2;
	private final static int TRASH_ID = 3;

	private JXGrabKey gk;
	private HotkeyListener listener;

	public Keys(final Notes notes, final NotesWindow notesWindow) {
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
					case TAGS_ID:
						notesWindow.toggleTags();
						break;
					case TRASH_ID:
						notesWindow.toggleTrash();
						break;
				}
			}
		};
		gk.addHotkeyListener(listener);
		try {
			gk.registerX11Hotkey(LIST_ID, X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.COLON); //Alt+Shift+:
			gk.registerX11Hotkey(NEW_ID, X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.APOSTROPHE); //Alt+Shift+'
			gk.registerX11Hotkey(TAGS_ID, X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.BRACKET_RIGHT); //Alt+Shift+]
			gk.registerX11Hotkey(TRASH_ID, X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.BRACKET_LEFT); //Alt+Shift+[
		} catch(HotkeyConflictException ex) {ex.printStackTrace();}
	}

	public void cleanUp() {
		gk.unregisterHotKey(LIST_ID);
		gk.unregisterHotKey(NEW_ID);
		gk.unregisterHotKey(TAGS_ID);
		gk.unregisterHotKey(TRASH_ID);
		gk.removeHotkeyListener(listener);
		gk.cleanUp();
	}
}