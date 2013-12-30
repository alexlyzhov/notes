import org.gnome.gtk.*;

public abstract class ListTree extends TreeView {
	public ListTree(ListStore model) {
		super(model);
		setHeadersVisible(false);
		TreeViewColumn nameViewColumn = appendColumn();
		new CellRendererText(nameViewColumn).setText(nameColumn);
		connectToAction();
	}

	protected abstract void connectToAction();
}