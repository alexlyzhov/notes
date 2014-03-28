import org.gnome.gtk.*;
import org.gnome.gdk.EventButton;
import org.gnome.gdk.MouseButton;

public class ListsView extends TreeView {
	private ListsStore store;

	public ListsView(ListsStore store) {
		setModel(store);
		store.setSelection(getSelection());
		this.store = store;

		setHeadersVisible(false);
		TreeViewColumn nameViewColumn = appendColumn();
		DataColumnString nameColumn = store.getNameColumn();
		new CellRendererText(nameViewColumn).setMarkup(nameColumn);

		connect(new MouseClicksHandler());
	}

	private class MouseClicksHandler implements Widget.ButtonPressEvent {
		private long lastClickTime;

		public boolean onButtonPressEvent(Widget source, EventButton event) { //try without this piece of code
			TreePath path = getPathAtPos((int) event.getX(), (int) event.getY());
			if(path != null && (System.currentTimeMillis() - lastClickTime > 10)) {
				MouseButton button = event.getButton();
				if(button == MouseButton.LEFT) {
					store.select(path);
				}
				lastClickTime = System.currentTimeMillis();
			}
			return true;
		}
	}
}