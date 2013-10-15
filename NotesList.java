import org.gnome.gtk.*;

public class NotesList {
	private Base base;

	private final DataColumnString nameColumn = new DataColumnString();
	private final DataColumnString timeColumn = new DataColumnString();
	private final DataColumnReference<Note> noteColumn = new DataColumnReference<Note>();
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
			public void onChanged(TreeSelection selection) {
				Note note = model.getValue(selection.getSelected(), noteColumn);
				if(!note.isEditing()) new Editor(note, NotesList.this);
			}
		});
	}

	private void addNote(Note note) {
		TreeIter row = model.appendRow();
		if(note.getName().equals("")) model.setValue(row, nameColumn, "Nameless");
		else model.setValue(row, nameColumn, note.getName());
		model.setValue(row, timeColumn, note.getTime());
		model.setValue(row, noteColumn, note);
	}

	public void onExit() {
		base.closeQueue();
	}

	public TreeView getTreeView() {
		return tree;
	}

	public Note newNote() {
		long lastID = base.newNote(new Note("", ""));
		Note note = base.getNote(lastID);
		addNote(note);
		return note;
	}

	public void updateNote(Note note) {
		base.updateNote(note);
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