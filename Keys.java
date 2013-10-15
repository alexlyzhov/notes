import jxgrabkey.*;

public class Keys {
	private final static int LIST_ID = 0;
	private final static int NEW_ID = 1;

	private JXGrabKey gk;
	private HotkeyListener listener;

	public Keys(final Notes notes) {
		System.loadLibrary("JXGrabKey");
		gk = JXGrabKey.getInstance();
		listener = new HotkeyListener() {
			public void onHotkey(int id) {
				switch(id) {
					case LIST_ID:
						notes.toggleVisible();
						break;
					case NEW_ID:
						notes.createNote();
						break;
				}
			}
		};
		gk.addHotkeyListener(listener);
		try {
			gk.registerX11Hotkey(LIST_ID, X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.COLON);
			gk.registerX11Hotkey(NEW_ID, X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.APOSTROPHE);
		} catch(HotkeyConflictException ex) {ex.printStackTrace();}
	}

	public void cleanUp() {
		gk.unregisterHotKey(LIST_ID);
		gk.unregisterHotKey(NEW_ID);
		gk.removeHotkeyListener(listener);
		gk.cleanUp();
	}
}