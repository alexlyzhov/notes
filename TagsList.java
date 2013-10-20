import org.gnome.gtk.*;
import org.gnome.gdk.EventButton;

public class TagsList extends ScrolledWindow {
	private Notes notes;
	private final DataColumnString nameColumn = new DataColumnString();
	private ListStore model;
	public TreeView tree;

	public TagsList(Notes notes) {
		this.notes = notes;

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

		tree.connect(new Widget.ButtonPressEvent() {
			public boolean onButtonPressEvent(Widget source, EventButton event) {
				TreePath path = tree.getPathAtPos((int) event.getX(), (int) event.getY());
				if(path != null) {
					TreeIter row = model.getIter(path);
					selectRow(row);
				}
				return false;
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

	public void selectRow(TreeIter row) {
		tree.getSelection().selectRow(row);
		notes.updateNotesList();
	}

	public void selectAllRow() {
		selectRow(model.getIterFirst());
	}

	private String getTag(TreeIter row) {
		return model.getValue(row, nameColumn);
	}

	public TreeIter getRow(String tag) {
		TreeIter row = model.getIterFirst();
		while(row.iterNext()) { //check other postcontidion places
			if(getTag(row).equals(tag)) return row;
		}
		return null;
	}

	public String getSelectedTag() {
		TreePath[] paths = tree.getSelection().getSelectedRows();
		if(paths.length == 0) {
			return null;
		}
		if(paths[0].getIndices()[0] == 0) return null;//check equals(model.getIterFirst()) yoso
		return getTag(model.getIter(paths[0]));
	}
}