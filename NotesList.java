import org.gnome.gtk.*;

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
		for(int i = 1; i <= base.getCount(); i++) { //setting data
			addNote(base.getNote(i));
		}

		TreeViewColumn vertical = tree.appendColumn(); //setting visible structure
		vertical.setTitle("Name");
		CellRendererText text = new CellRendererText(vertical);
		text.setText(nameColumn);
		tree.setHeadersVisible(false);
		model.setSortColumn(timeColumn, SortType.DESCENDING);

		tree.getSelection().connect(new TreeSelection.Changed() { //grabbing events
			public void onChanged(TreeSelection selection) {                          //make it activating/multiple selection instead of single
				TreeIter row = selection.getSelected();                               //remove elements in context menu or with ctrl + click and delete
				Note note = getNote(row);
				if(!note.isEditing()) new Editor(NotesList.this, row);
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
		model.setValue(row, nameColumn, note.getOutputName());
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

	// public void removeNote(Note note) {
	// 	model.removeRow(getNoteRow(note));
	// 	base.removeNote(note);
	// }

	// private TreeIter getNoteRow(Note reqNote) {
	// 	TreeIter row = model.getIterFirst();
	// 	if(row == null) return null;
	// 	do {
	// 		Note currNote = model.getValue(row, noteColumn);
	// 		if(currNote.equals(reqNote)) return row;
	// 	} while(row.iterNext());
	// 	return null;
	// }
}