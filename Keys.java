import jxgrabkey.*;
import java.awt.event.KeyEvent; //get rid of awt
import java.awt.event.InputEvent;

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
			gk.registerAwtHotkey(LIST_ID, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK, KeyEvent.VK_O);
			gk.registerAwtHotkey(NEW_ID, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK, KeyEvent.VK_N);
		} catch(HotkeyConflictException ex) {ex.printStackTrace();}
	}

	public void cleanUp() {
		gk.unregisterHotKey(LIST_ID);
		gk.unregisterHotKey(NEW_ID);
		gk.removeHotkeyListener(listener);
		gk.cleanUp();
	}
}