import org.gnome.gtk.*;
// import java.util.ArrayList;

public class ListsStore extends ListStore {
	private final static DataColumnString nameColumn = new DataColumnString();
	private final static DataColumnReference<List> listColumn = new DataColumnReference<List>();
	private final static DataColumn[] columns = {nameColumn, listColumn};
	private Data data;
	private NotesStore notesStore;
	private TreeSelection selection;
	private boolean trashShown;
	private final List generalList = new List("All notes");
	private final List trashList = new List("Trash");

	public ListsStore(Data data, NotesStore notesStore) {
		super(columns);
		this.data = data;
		this.notesStore = notesStore;
	}

	public DataColumnString getNameColumn() {
		return nameColumn;
	}

	public void setSelection(TreeSelection selection) {
		this.selection = selection;
	}

	public void update() {
		List selected;
		if(nothingSelected()) {
			selected = generalList;
		} else {
			selected = getSelectedList();
		}

		clear();
		addList(generalList);
		for(List list: data.listsData) {
			addList(list);
		}
		if(data.trashExists()) {
			addList(trashList);
			trashShown = true;
		} else {
			trashShown = false;
		}

		TreeIter selectedRow = getRow(selected);
		if(selectedRow != null) {
			select(selectedRow);
		} else {
			select(generalList);
		}
	}

	private void addList(List list) {
		TreeIter row = appendRow();
		setValue(row, listColumn, list);
		setValue(row, nameColumn, list.name);
	}

	public void select(TreePath path) {
		TreeIter row = getIter(path);
		select(row);
	}

	public void select(TreeIter row) {
		selection.selectRow(row);
		notesStore.update(this);
	}

	public void select(List list) {
		TreeIter row = getRow(list);
		if(row != null) {
			select(row);
		}
	}

	public void selectGeneralList() {
		select(generalList);
	}

	private TreeIter getRow(List list) {
		if(list != null) {
			TreeIter row = getIterFirst();
			do {
				if(getList(row).equals(list)) {
					return row;
				}
			} while(row.iterNext());
		}
		return null;
	}

	private List getList(TreeIter row) {
		return getValue(row, listColumn);
	}

	public List getSelectedList() {
		TreePath[] paths = selection.getSelectedRows();
		return getList(getIter(paths[0]));
	}

	private boolean nothingSelected() {
		TreePath[] paths = selection.getSelectedRows();
		if(paths.length == 0) {
			return true;
		}
		return false;
	}

	public boolean trashListSelected() {
		return (getSelectedList() == trashList);
	}

	public boolean generalListSelected() {
		return (getSelectedList() == generalList);
	}

	public boolean actualList(List list) { //ambiguous
		return ((list != generalList) && (list != trashList));
	}
}