import org.gnome.gtk.*;

public abstract class ListTree extends TreeView {
	private final DataColumnString nameColumn = new DataColumnString();

	public ListTree(ListModel model) {
		super(model);
		setHeadersVisible(false);
		TreeViewColumn nameViewColumn = appendColumn();
		DataColumnString nameColumn = model.getNameColumn();
		new CellRendererText(nameViewColumn).setMarkup(nameColumn);
		connectToAction();
	}

	public ScrolledList getScrolledList() {
		return new ScrolledList(this);
	}

	protected abstract void connectToAction();
}