import jxgrabkey.*;
import java.util.ArrayList;

public class Keys {
	private enum Key {
		//Default hotkeys:
		//enter on keypad to show notes list
		//plus on keypad to create new note
		//Alt+Shift+Q to exit the application
		LIST_ID("list", 0, X11KeysymDefinitions.KP_ENTER),
		NEW_ID("new", 0, X11KeysymDefinitions.KP_ADD),
		EXIT_ID("exit", X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.Q);

		public final String name;
		public int mask;
		public int code;

		Key(String name, int mask, int code) {
			this.name = name;
			this.mask = mask;
			this.code = code;
		}
	};

	private JXGrabKey gk;
	private HotkeyListener listener;

	public Keys() {
		final Notes notes = Notes.getInstance();
		System.loadLibrary("JXGrabKey");
		gk = JXGrabKey.getInstance();
		int state = 0;
		Key c = null;
		for(Key key: Key.values()) {
			String reply = Args.getInstance().getNamedArgument(key.name);
			if(reply != null) {
				key.mask = Integer.parseInt(reply.substring(0, reply.indexOf(":")));
				key.code = Integer.parseInt(reply.substring(reply.indexOf(":") + 1, reply.length()));
			}
		}

		try {
			for(int i = 0; i < Key.values().length; i++) {
				gk.registerX11Hotkey(i, Key.values()[i].mask, Key.values()[i].code);
			}
		} catch(HotkeyConflictException ex) {ex.printStackTrace();}

		listener = new HotkeyListener() {
			public void onHotkey(int id) {
				switch(Key.values()[id].name) {
					case "list":
						notes.toggleVisible();
						break;
					case "new":
						notes.openNewNote();
						break;
					case "exit":
						notes.exit();
						break;
				}
			}
		};
		gk.addHotkeyListener(listener);
	}

	public void cleanUp() {
		new Thread() {
			public void run() {
				for(int i = 0; i < Key.values().length; i++) {
					gk.unregisterHotKey(i);
				}
				gk.removeHotkeyListener(listener);
				gk.cleanUp();
			} 
		}.start();
	}
}