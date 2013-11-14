import jxgrabkey.*;

public class Keys {
	private final static int LIST_ID = 0;
	private final static int NEW_ID = 1;
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
					case EXIT_ID:
						notes.exit();
						break;
				}
			}
		};
		gk.addHotkeyListener(listener);
		try {
			gk.registerX11Hotkey(LIST_ID, 0, X11KeysymDefinitions.KP_ENTER); //Enter on keypad
			gk.registerX11Hotkey(NEW_ID, 0, X11KeysymDefinitions.KP_ADD); //Plus on keypad
			gk.registerX11Hotkey(EXIT_ID, X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.Q); //Alt+Shift+Q
		} catch(HotkeyConflictException ex) {ex.printStackTrace();}
	}

	public void cleanUp() {
		new Thread() {
			public void run() {
				gk.unregisterHotKey(LIST_ID);
				gk.unregisterHotKey(NEW_ID);
				gk.unregisterHotKey(EXIT_ID);
				gk.removeHotkeyListener(listener);
				gk.cleanUp();
			} 
		}.start();
	}
}