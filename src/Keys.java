import jxgrabkey.*;
import java.util.ArrayList;
import java.util.Arrays;

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
		//1..9 on keypad to quickly open the chosen note
		LIST_ID("list", new Call[] {new Call(0, X11KeysymDefinitions.KP_ENTER)}),
		NEW_ID("new", new Call[] {new Call(0, X11KeysymDefinitions.KP_ADD)}),
		EXIT_ID("exit", new Call[] {new Call(X11MaskDefinitions.X11_MOD1_MASK | X11MaskDefinitions.X11_SHIFT_MASK, X11KeysymDefinitions.Q)}),
		CLOSE_ID("close", new Call[] {new Call(0, X11KeysymDefinitions.KP_SUBTRACT)}),
		REMOVE_ID("remove", new Call[] {new Call(0, X11KeysymDefinitions.KP_DELETE), new Call(0, X11KeysymDefinitions.KP_DECIMAL)}),
		QUICK1_ID("quick1", new Call[] {new Call(0, X11KeysymDefinitions.KP_END), new Call(0, X11KeysymDefinitions.KP_1)}),
		QUICK2_ID("quick2", new Call[] {new Call(0, X11KeysymDefinitions.KP_DOWN), new Call(0, X11KeysymDefinitions.KP_2)}),
		QUICK3_ID("quick3", new Call[] {new Call(0, X11KeysymDefinitions.KP_NEXT), new Call(0, X11KeysymDefinitions.KP_3)}),
		QUICK4_ID("quick4", new Call[] {new Call(0, X11KeysymDefinitions.KP_LEFT), new Call(0, X11KeysymDefinitions.KP_4)}),
		QUICK5_ID("quick5", new Call[] {new Call(0, X11KeysymDefinitions.KP_BEGIN), new Call(0, X11KeysymDefinitions.KP_5)}),
		QUICK6_ID("quick6", new Call[] {new Call(0, X11KeysymDefinitions.KP_RIGHT), new Call(0, X11KeysymDefinitions.KP_6)}),
		QUICK7_ID("quick7", new Call[] {new Call(0, X11KeysymDefinitions.KP_HOME), new Call(0, X11KeysymDefinitions.KP_7)}),
		QUICK8_ID("quick8", new Call[] {new Call(0, X11KeysymDefinitions.KP_UP), new Call(0, X11KeysymDefinitions.KP_8)}),
		QUICK9_ID("quick9", new Call[] {new Call(0, X11KeysymDefinitions.KP_PRIOR), new Call(0, X11KeysymDefinitions.KP_9)});

		public final String name;
		public Call[] calls;

		Key(String name, Call[] calls) {
			this.name = name;
			this.calls = calls;
		}

		public void execute() {
			Notes notes = Notes.getInstance();
			NotesWindow notesWindow = NotesWindow.getInstance();
			switch(this) {
				case LIST_ID:
					notesWindow.toggleVisible();
					break;
				case NEW_ID:
					notesWindow.openNewNote();
					break;
				case EXIT_ID:
					notes.exit();
					break;
				case CLOSE_ID:
					notesWindow.closeCurrentNote();
					break;
				case REMOVE_ID:
					notesWindow.removeCurrentNote();
					break;
				default:
					if(this.name.startsWith("quick")) {
						int num = -1;
						try {
							num = Integer.parseInt(name.substring(5));
						} catch(Exception ex) {ex.printStackTrace();}
						if((num >= 1) && (num <= 9)) {
							notes.openQuick(num);
						}
					}
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
					if(!strCall.equals("nil")) {
						newMask = Integer.parseInt(strCall.substring(0, strCall.indexOf(":")));
						newCode = Integer.parseInt(strCall.substring(strCall.indexOf(":") + 1, strCall.length()));
						newCalls.add(new Call(newMask, newCode));
					}
				} catch(NumberFormatException ex) {ex.printStackTrace();}
				if(strCall.equals("def")) {
					newCalls.addAll(Arrays.asList(key.calls));
				}
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