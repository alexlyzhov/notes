import org.gnome.gtk.*;
import org.gnome.gdk.EventButton;
import org.gnome.gdk.MouseButton;
import org.gnome.gdk.EventMask;
import org.gnome.gdk.EventMotion;
import java.util.ArrayList;

public class TagsList {
	private final DataColumnString nameColumn = new DataColumnString();
	private final DataColumn[] columns = {nameColumn};
	private TagsListModel model;
	private TagsListTree tree;
	private TreeSelection selection;
	private Notes notes;

	private boolean trashShown;

	public TagsList() {
		notes = Notes.getInstance();
		model = new TagsListModel(columns);
		tree = new TagsListTree(model);
		selection = tree.getSelection();
	}

	private class TagsListModel extends ListModel {
		TreeIter trashRow;
		private TagsListModel(DataColumn[] columns) {
			super(columns);
		}

		private TreeIter getAllRow() {
			return getIterFirst();
		}

		private TreeIter getTrashRow() {
			TreeIter row = getIterFirst();
			TreeIter clonedRow = getIterFirst();
			while(row.iterNext()) {
				clonedRow.iterNext();
			};
			return clonedRow;
		}

		private void addNoteTags(Note note) {
			if(!note.inTrash()) {
				String[] newTags = note.getTags().split(",");
				for(String newTag: newTags) {
					if((!newTag.equals("")) && (!tagExists(newTag))) {
					// if((notes.getTagQuickNum(newTag) == -1) && (!newTag.equals("")) && (!tagExists(newTag))) {
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

	private class TagsListTree extends ListTree {
		private long lastClickTime;

		private TagsListTree(TagsListModel model) {
			super(model);
		}

		protected void connectToAction() {
			connect(new Widget.ButtonPressEvent() {
				public boolean onButtonPressEvent(Widget source, EventButton event) {
					TreePath path = getPathAtPos((int) event.getX(), (int) event.getY());
					if(path != null && (System.currentTimeMillis() - lastClickTime > 10)) {
						TreeIter row = model.getIter(path);
						String tag = model.getTag(row);
						MouseButton b = event.getButton();
						if(b == MouseButton.LEFT) {
							selection.selectRow(row);
							Notes.getInstance().updateNotesList();	
						}
						lastClickTime = System.currentTimeMillis();
					}
					return true;
				}
			});

			// addEvents(EventMask.POINTER_MOTION);
			// connect(new Widget.MotionNotifyEvent() {
			// 	public boolean onMotionNotifyEvent(Widget source, EventMotion event) {
			// 		int x = (int) event.getX();
			// 		int y = (int) event.getY();
			// 		TreePath path = getPathAtPos(x, y);
			// 		if(path != null) {
			// 			TreeIter row = model.getIter(path);
			// 			if(!selection.getSelectedRows()[0].equals(path)) {
			// 				selection.selectRow(row);
			// 				Notes.getInstance().updateNotesList();
			// 			}
			// 		}
			// 		return true;
			// 	}
			// });
		}
	}

	public ListTree getTree() {
		return tree;
	}

	public void update(ArrayList<Note> notesData) {
		String selected = null;
		if(!nothingSelected()) {
			if(trashSelected()) selected = "";
			else selected = getSelectedTag();
		}
		model.clear();
		for(Note note: notesData) {
			model.addNoteTags(note);
		}
		if(trashExists(notesData)) {
			model.addTag("Trash");
			trashShown = true;
		} else {
			trashShown = false;
		}
		if(trashShown && (selected != null) && (selected.equals(""))) {
			selectRow(model.getTrashRow());
		} else {
			TreeIter selectedRow = model.getRow(selected);
			if(selectedRow != null) {
				selectRow(selectedRow);
			} else {
				selectAllRow();
			}
		}
		Notes.getInstance().updateNotesList();
	}

	private boolean trashExists(ArrayList<Note> notesData) {
		for(Note note: notesData) {
			if(note.inTrash()) {
				return true;
			}
		}
		return false;
	}

	public String getSelectedTag() {
		if(nothingSelected()) {
			System.out.println("Error: call getSelectedTag() when nothing selected");
			return null;
		}
		if(allRowSelected()) return null;
		TreePath[] paths = selection.getSelectedRows(); //paths and iters are not stable, check to use it right away
		return model.getTag(model.getIter(paths[0]));
	}

	public boolean nothingSelected() {
		TreePath[] paths = selection.getSelectedRows();
		if(paths.length == 0) {
			return true;
		}
		return false;
	}

	private boolean allRowSelected() {
		TreePath[] paths = selection.getSelectedRows();
		if(nothingSelected()) return false;
		if(paths[0].getIndices()[0] == 0) return true;
		return false;
	}

	public boolean lastSelected() {
		TreeIter selected = selection.getSelected();
		if(selected == null) return false;
		return selection.getSelected().iterNext() == false;
	}

	public void selectAllRow() {
		selectRow(model.getAllRow());
	}

	public void selectRow(TreeIter row) {
		selection.selectRow(row);
	}

	public boolean trashSelected() {
		if(trashShown && lastSelected()) return true;
		return false;
	}

	public boolean noTags() {
		return !model.getIterFirst().iterNext();
	}
}