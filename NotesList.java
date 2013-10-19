import org.gnome.gtk.*;
import org.gnome.gdk.EventButton;
import org.gnome.gdk.MouseButton;

public class NotesList {
	private Base base;

	public static final DataColumnString nameColumn = new DataColumnString();
	public static final DataColumnString timeColumn = new DataColumnString();
	public static final DataColumnReference<Note> noteColumn = new DataColumnReference<Note>();
	private final ListStore model = new ListStore(new DataColumn[] {nameColumn, timeColumn, noteColumn});
	private TreeView tree;

	public NotesList() {
		base = new Base();
		tree = new TreeView(model);
		for(Note note: base.getNotes()) addNote(note);

		TreeViewColumn vertical = tree.appendColumn(); //setting visible structure
		vertical.setTitle("Name");
		CellRendererText text = new CellRendererText(vertical);
		text.setText(nameColumn);
		tree.setHeadersVisible(false);
		model.setSortColumn(timeColumn, SortType.DESCENDING);

		tree.connect(new Widget.ButtonPressEvent() { //grabbing events
			public boolean onButtonPressEvent(Widget source, EventButton event) {
				TreePath path = tree.getPathAtPos((int) event.getX(), (int) event.getY());
				TreeIter row = model.getIter(path);
				if(path != null && !getNote(row).isEditing()) {
					if(event.getButton() == MouseButton.LEFT) {
						new Editor(NotesList.this, row);
					} else if(event.getButton() == MouseButton.RIGHT) {
						removeNote(row);
					}
				}
				return true;
			}
		});
	}

	public Note getNote(TreeIter row) {
		return model.getValue(row, noteColumn);
	}

	private TreeIter addNote(Note note) {
		TreeIter row = model.appendRow();
		model.setValue(row, noteColumn, note);
		setData(row);
		return row;
	}

	public void setData(TreeIter row) {
		setName(row);
		setTime(row);
	}

	public void setName(TreeIter row) {
		Note note = getNote(row);
		String name = note.getName();
		if(name.equals("")) name = "Nameless";
		if(note.isEditing()) name = name + " *";
		model.setValue(row, nameColumn, name);
	}

	public void setTime(TreeIter row) {
		Note note = getNote(row);
		model.setValue(row, timeColumn, note.getTime());
	}

	public void onExit() {
		base.closeQueue();
	}

	public TreeView getTreeView() {
		return tree;
	}

	public TreeModel getModel() {
		return getTreeView().getModel();
	}

	public TreeIter newNote() {
		Note note = new Note("", "");
		base.newNote(note);
		return addNote(note);
	}

	public void updateNote(TreeIter row) {
		base.updateNote(getNote(row), this, row);
	}

	public void removeNote(TreeIter row) {
		base.removeNote(getNote(row));
		model.removeRow(row);
	}
}