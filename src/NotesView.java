import org.gnome.gtk.*;
import org.gnome.gdk.EventButton;
import org.gnome.gdk.MouseButton;

public class NotesView extends TreeView {
	private MainWindow mainWindow;
	private Data data;
	private final NotesStore store;

	public NotesView(NotesStore store, MainWindow mainWindow, Data data) {
		setModel(store);
		this.store = store;
		this.data = data;
		this.mainWindow = mainWindow;

		setHeadersVisible(false);
		TreeViewColumn nameViewColumn = appendColumn();
		DataColumnString nameColumn = store.getNameColumn();
		new CellRendererText(nameViewColumn).setMarkup(nameColumn);

		connect(new MouseClicksHandler());
	}

	private class MouseClicksHandler implements Widget.ButtonPressEvent {
		private long lastClickTime;

		public boolean onButtonPressEvent(Widget source, EventButton event) {
			TreePath path = getPathAtPos((int) event.getX(), (int) event.getY());
			if(path != null && (System.currentTimeMillis() - lastClickTime) > 10) {
				MouseButton button = event.getButton();
				Note note = store.getNote(path);
				if(button == MouseButton.LEFT) {
					if(!note.editing) {
						mainWindow.openNote(note);
					} else {
						mainWindow.closeNote(note);
					}
				} else if(button == MouseButton.MIDDLE) {
					if(note.editing) {
						mainWindow.closeNote(note);
					}
					mainWindow.removeNoteAndUpdate(note);
				} else if(button == MouseButton.RIGHT) {
					if(!note.editing) {
						mainWindow.invokeProperties(note);
					}
				}
				lastClickTime = System.currentTimeMillis();
			}
			return true;
		}
	}
}