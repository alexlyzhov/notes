import org.gnome.gtk.*;
import org.gnome.gdk.EventButton;

public class TagsList extends ScrolledWindow {
	private final Notes notes;
	private final DataColumnString nameColumn = new DataColumnString();
	private ListStore model;
	public TreeView tree;

	public TagsList(Notes notesParam) {
		this.notes = notesParam;

		setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
		getVAdjustment().connect(new Adjustment.Changed() {
			public void onChanged(Adjustment source) {
				source.setValue(0);
			}
		});

		model = new ListStore(new DataColumn[] {nameColumn});
		tree = new TreeView(model);
		add(tree);

		tree.setHeadersVisible(false);
		TreeViewColumn nameViewColumn = tree.appendColumn();
		nameViewColumn.setTitle("Tag");
		new CellRendererText(nameViewColumn).setText(nameColumn);

		tree.getSelection().connect(new TreeSelection.Changed() {
			public void onChanged(TreeSelection selection) {
				if(!nothingSelected()) {
					notes.updateNotesList();
				}
			}
		});
	}

	public void clear() {
		model.clear();
		appendAllRow();
	}

	private void appendAllRow() {
		TreeIter all = model.appendRow();
		model.setValue(all, nameColumn, "All notes");
		selectAllRow();
	}

	public void addNoteTags(Note note) {
		String[] newTags = note.getTags().split(",");
		for(String newTag: newTags) {
			if(newTag.equals("Trash")) {
				return;
			}
		}
		for(String newTag: newTags) {
			if(!newTag.equals("")) {
				if(!tagExists(newTag)) {
					addTag(newTag);
				}
			}
		}
	}

	private void addTag(String newTag) {
		TreeIter row = model.appendRow();
		model.setValue(row, nameColumn, newTag);
	}

	private boolean tagExists(String tag) {
		TreeIter row = model.getIterFirst();
		while(row.iterNext()) {
			if(tag.equals(getTag(row))) return true;
		}
		return false;
	}

	public void addTrash() {
		TreeIter row = model.appendRow();
		model.setValue(row, nameColumn, "Trash");
	}

	public boolean lastSelected() {
		TreeIter selected = tree.getSelection().getSelected();
		if(selected == null) return false; //logically?
		return tree.getSelection().getSelected().iterNext() == false;
	}

	public void selectRow(TreeIter row) {
		tree.getSelection().selectRow(row);
	}

	public void selectAllRow() {
		selectRow(model.getIterFirst());
	}

	private String getTag(TreeIter row) {
		return model.getValue(row, nameColumn);
	}

	public TreeIter getRow(String tag) {
		if(tag != null) {
			TreeIter row = model.getIterFirst();
			while(row.iterNext()) {
				if(getTag(row).equals(tag)) return row;
			}
		}
		return null;
	}

	public String getSelectedTag() {
		if(nothingSelected()) {
			System.out.println("Error: call getSelectedTag() when nothing selected");
			return null;
		}
		if(allRowSelected()) return null;
		TreePath[] paths = tree.getSelection().getSelectedRows();
		return getTag(model.getIter(paths[0]));
	}

	public boolean nothingSelected() {
		TreePath[] paths = tree.getSelection().getSelectedRows();
		if(paths.length == 0) {
			return true;
		}
		return false;
	}

	private boolean allRowSelected() {
		TreePath[] paths = tree.getSelection().getSelectedRows();
		if(nothingSelected()) return false;
		if(paths[0].getIndices()[0] == 0) return true;
		return false;
	}
}