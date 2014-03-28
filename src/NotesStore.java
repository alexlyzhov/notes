import org.gnome.gtk.*;

public class NotesStore extends ListStore {
	private final static DataColumnString nameColumn = new DataColumnString();
	private final static DataColumnLong timeColumn = new DataColumnLong();
	private final static DataColumnReference<Note> noteColumn = new DataColumnReference<Note>();
	private final static DataColumn[] columns = new DataColumn[] {nameColumn, timeColumn, noteColumn};
	private Data data;

	public NotesStore(Data data) {
		super(columns);
		this.data = data;
		setSortColumn(timeColumn, SortType.DESCENDING);
	}

	public DataColumnString getNameColumn() {
		return nameColumn;
	}

	public void update(ListsStore listsStore) {
		clear();
		if(listsStore.trashListSelected()) {
			for(Note note: data.notesData) {
				if(note.trash) {
					addNote(note);
				}
			}
		} else if(listsStore.generalListSelected()) {
			for(Note note: data.notesData) {
				if(!note.trash) {
					addNote(note);
				}
			}
		} else {
			List list = listsStore.getSelectedList();
			for(Note note: list.notes) {
				if(!note.trash) {
					addNote(note);
				}
			}
			if(empty()) {
				listsStore.selectGeneralList();
			}
		}
	}

	private boolean empty() {
		return (getIterFirst() == null);
	}

	public void addNote(Note note) {
		TreeIter row = appendRow();
		setValue(row, noteColumn, note);
		updateInfo(note);
	}

	public void updateInfo(Note note) {
		TreeIter row = getRow(note);
		if(row != null) {
			setValue(row, nameColumn, note.getMarkdownName());
			setValue(row, timeColumn, note.getTime());
		}
	}

	public Note getNote(TreePath path) {
		TreeIter row = getIter(path);
		Note note = getNote(row);
		return note;
	}

	public Note getNote(TreeIter row) {
		return getValue(row, noteColumn);
	}

	private TreeIter getRow(Note note) {
		TreeIter row = getIterFirst();
		if(row == null) return null;
		do {
			if(getNote(row).equals(note)) return row; //try == comparison
		} while(row.iterNext());
		return null;
	}
}