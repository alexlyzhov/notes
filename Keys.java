import jxgrabkey.*;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

public class Keys {
	private final static int LIST_ID = 0;
	private final static int NEW_ID = 1;

	public Keys(final Notes notes) {
		System.loadLibrary("JXGrabKey");
		JXGrabKey gk = JXGrabKey.getInstance();
		gk.addHotkeyListener(new HotkeyListener() {
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
		});
		try {
			gk.registerAwtHotkey(LIST_ID, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK, KeyEvent.VK_O);
			gk.registerAwtHotkey(NEW_ID, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK, KeyEvent.VK_N);
		} catch(HotkeyConflictException ex) {ex.printStackTrace();}
	}
}