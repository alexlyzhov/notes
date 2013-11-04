import org.gnome.gtk.*;
import org.gnome.gdk.EventButton;
import java.util.ArrayList;

public class TagsList {
	private final DataColumnString nameColumn = new DataColumnString();
	private final DataColumn[] columns = {nameColumn};
	private TagsListModel model;
	private TagsListTree tree;
	private boolean trashShown;

	public TagsList() {
		model = new TagsListModel(columns);
		tree = new TagsListTree(model);
	}

	private class TagsListModel extends ListStore {
		private TagsListModel(DataColumn[] columns) {
			super(columns);
		}

		private TreeIter getAllRow() {
			return model.getIterFirst();
		}

		private void addNoteTags(Note note) {
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
			TreeIter row = appendRow();
			setValue(row, nameColumn, newTag);
		}

		private String getTag(TreeIter row) {
			return getValue(row, nameColumn);
		}

		public TreeIter getRow(String tag) {
			if(tag != null) {
				TreeIter row = getIterFirst();
				while(row.iterNext()) {
					if(getTag(row).equals(tag)) return row;
				}
			}
			return null;
		}

		public void addTrash() {
			TreeIter row = appendRow();
			setValue(row, nameColumn, "Trash");
			trashShown = true;
		}

		private boolean tagExists(String tag) {
			TreeIter row = getIterFirst();
			if(row != null) {
				while(row.iterNext()) {
					if(tag.equals(getTag(row))) return true;
				}
			}
			return false;
		}

		public void clear() {
			super.clear();
			appendAllRow();
		}

		private void appendAllRow() {
			TreeIter all = appendRow();
			setValue(all, nameColumn, "All notes");
		}
	}

	private class TagsListTree extends TreeView { //pick selection subclass out
		private TagsListModel model;
		private TagsListTree(TagsListModel model) {
			super(model);
			this.model = model;
			setHeadersVisible(false);
			TreeViewColumn nameViewColumn = appendColumn();
			nameViewColumn.setTitle("Tag");
			new CellRendererText(nameViewColumn).setText(nameColumn);
			connectToAction();
		}

		private void connectToAction() {
			getSelection().connect(new TreeSelection.Changed() {
				public void onChanged(TreeSelection selection) {
					if(!nothingSelected()) {
						Notes.getInstance().updateNotesList();
					}
				}
			});
		}

		public String getSelectedTag() {
			if(nothingSelected()) {
				System.out.println("Error: call getSelectedTag() when nothing selected");
				return null;
			}
			if(allRowSelected()) return null;
			TreePath[] paths = getSelection().getSelectedRows();
			return model.getTag(getModel().getIter(paths[0]));
		}

		public boolean nothingSelected() {
			TreePath[] paths = getSelection().getSelectedRows();
			if(paths.length == 0) {
				return true;
			}
			return false;
		}

		private boolean allRowSelected() {
			TreePath[] paths = getSelection().getSelectedRows();
			if(nothingSelected()) return false;
			if(paths[0].getIndices()[0] == 0) return true;
			return false;
		}

		public boolean lastSelected() {
			TreeIter selected = tree.getSelection().getSelected();
			if(selected == null) return false; //logically?
			return tree.getSelection().getSelected().iterNext() == false;
		}

		public void selectAllRow() {
			selectRow(model.getAllRow());
		}

		public void selectRow(TreeIter row) {
			getSelection().selectRow(row);
		}

		private boolean trashTagExists(ArrayList<Note> notesData) {
			for(Note note: notesData) {
				for(String tag: note.getTags().split(",")) {
					if(tag.equals("Trash")) {
						return true;
					}
				}
			}
			return false;
		}

		private void update(ArrayList<Note> notesData) {
			String selected = null;
			if(!nothingSelected()) {
				selected = getSelectedTag();
			}
			model.clear();
			for(Note note: notesData) {
				model.addNoteTags(note);
			}
			if(trashTagExists(notesData)) {
				model.addTrash();
			}
			TreeIter selectedRow = model.getRow(selected);
			if(selectedRow != null) {
				selectRow(selectedRow);
			} else {
				selectAllRow();
			}
		}
	}

	private class NotesListScrolled extends ScrolledWindow {
		private NotesListScrolled(TagsListTree tree) {
			setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
			getVAdjustment().connect(new Adjustment.Changed() {
				public void onChanged(Adjustment source) {
					source.setValue(0);
				}
			});
			add(tree);
		} 
	}

	public ScrolledWindow getScrolledWindow() {
		return new NotesListScrolled(tree);
	}

	public void update(ArrayList<Note> notesData) {
		tree.update(notesData);
	}

	public boolean trashSelected() {
		if(trashShown && tree.lastSelected()) return true;
		return false;
	}

	public void selectAllRow() {
		tree.selectAllRow();
	}

	public String getSelectedTag() {
		return tree.getSelectedTag();
	}

	public boolean nothingSelected() {
		return tree.nothingSelected();
	}

	public boolean noTags() {
		return !model.getIterFirst().iterNext();
	}
}