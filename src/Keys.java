import jxgrabkey.*;
import java.util.ArrayList;

public class Keys {
	private static class Call {
		private int mask;
		private int code;

		public Call(int mask, int code) {
			this.mask = mask;
			this.code = code;
		}

		public int getMask() {
			return mask;
		}

		public int getCode() {
			return code;
		}
	}

	private enum Key {
		//Default hotkeys:
		//enter on keypad to show notes list
		//plus on keypad to create new note
		//Alt+Shift+Q to exit the application
		//minus on keypad to close the active Editor window
		//del on keypad to remove the note in the active Editor window
		LIST_ID("list", new Call[] {new Call(0, X11KeysymDefinitions.KP_ENTER)}),
		NEW_ID("new", new Call[] {new Call(0, X11KeysymDefinitions.KP_ADD)}),
		EXIT_ID("exit", new Call[] {new Call(X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.Q)}),
		CLOSE_ID("close", new Call[] {new Call(0, X11KeysymDefinitions.KP_SUBTRACT)}),
		REMOVE_ID("remove", new Call[] {new Call(0, X11KeysymDefinitions.KP_DELETE), new Call(0, X11KeysymDefinitions.KP_DECIMAL)});

		public final String name;
		public Call[] calls;

		Key(String name, Call[] calls) {
			this.name = name;
			this.calls = calls;
		}

		public void execute() {
			Notes notes = Notes.getInstance();
			switch(this) {
				case LIST_ID:
					notes.toggleVisible();
					break;
				case NEW_ID:
					notes.openNewNote();
					break;
				case EXIT_ID:
					notes.exit();
					break;
				case CLOSE_ID:
					notes.closeCurrentNote();
					break;
				case REMOVE_ID:
					notes.removeCurrentNote();
					break;
			}
		}
	};

	private JXGrabKey gk;
	private HotkeyListener listener;
	private ArrayList<Key> registeredKeys = new ArrayList<Key>();

	public Keys() {
		System.loadLibrary("JXGrabKey");
		gk = JXGrabKey.getInstance();

		for(Key key: Key.values()) {
			readCallsFromArgs(key);
			register(key);
		}

		listener = new HotkeyListener() {
			public void onHotkey(int id) {
				registeredKeys.get(id).execute();
			}
		};
		gk.addHotkeyListener(listener);
	}

	private void readCallsFromArgs(Key key) {
		String reply = Args.getInstance().getNamedArgument(key.name);
		if(reply != null) {
			ArrayList<Call> newCalls = new ArrayList<Call>();
			String[] strCalls = reply.split(",");
			for(String strCall: strCalls) {
				int newMask, newCode;
				try {
					newMask = Integer.parseInt(reply.substring(0, reply.indexOf(":")));
					newCode = Integer.parseInt(reply.substring(reply.indexOf(":") + 1, reply.length()));
					newCalls.add(new Call(newMask, newCode));
				} catch(NumberFormatException ex) {ex.printStackTrace();}
			}
			key.calls = newCalls.toArray(new Call[newCalls.size()]);
		}
	}

	private void register(Key key) {
		for(Call call: key.calls) {
			try {
				gk.registerX11Hotkey(registeredKeys.size(), call.getMask(), call.getCode());
				registeredKeys.add(key);
			} catch(HotkeyConflictException ex) {
				ex.printStackTrace();
				registeredKeys.add(null);
			}
		}
	}

	public void cleanUp() {
		new Thread() {
			public void run() {
				for(int i = 0; i < registeredKeys.size(); i++) {
					if(registeredKeys.get(i) != null) {
						gk.unregisterHotKey(i);
					}
				}
				gk.removeHotkeyListener(listener);
				gk.cleanUp();
			} 
		}.start();
	}
}