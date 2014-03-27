import org.gnome.gtk.*;
import org.gnome.gdk.EventButton;
import org.gnome.gdk.MouseButton;
import java.util.ArrayList;

public class NotesList {
	private final DataColumnString nameColumn = new DataColumnString();
	private final DataColumnLong timeColumn = new DataColumnLong();
	private final DataColumnReference<Note> noteColumn = new DataColumnReference<Note>();
	private final DataColumn[] columns = new DataColumn[] {nameColumn, timeColumn, noteColumn};
	private NotesListModel model;
	private NotesListTree tree;

	public NotesList() {
		model = new NotesListModel(columns);
		tree = new NotesListTree(model);
	}

	private class NotesListModel extends ListModel {
		private NotesListModel(DataColumn[] columns) {
			super(columns);
			setSortColumn(timeColumn, SortType.DESCENDING);
		}

		public void addNote(Note note) {
			TreeIter row = appendRow();
			setValue(row, noteColumn, note);
			updateView(note);
		}

		public boolean empty() {
			if(getIterFirst() == null) return true;
			return false;
		}

		public void updateView(Note note) {
			TreeIter row = getRow(note);
			if(row != null) {
				updateName(row);
				updateTime(row);
			}
		}

		private void updateName(TreeIter row) {
			Note note = getNote(row);
			String name = note.getFilledName();
			if(note.isEditing()) {
				name = "<b>" + name + "</b>";
			}
			setValue(row, nameColumn, name);
		}

		private void updateTime(TreeIter row) {
			Note note = getNote(row);
			long time = note.getTime();
			setValue(row, timeColumn, time);
		}

		private Note getNote(TreeIter row) {
			return getValue(row, noteColumn);
		}

		private TreeIter getRow(Note note) {
			TreeIter row = getIterFirst();
			if(row == null) return null;
			do {
				if(getNote(row).equals(note)) return row;
			} while(row.iterNext());
			return null;
		}
	}

	private class NotesListTree extends ListTree {
		private long lastClickTime;

		private NotesListTree(NotesListModel model) {
			super(model);
		}

		protected void connectToAction() {
			connect(new Widget.ButtonPressEvent() {
				public boolean onButtonPressEvent(Widget source, EventButton event) {
					TreePath path = getPathAtPos((int) event.getX(), (int) event.getY());
					if(path != null && (System.currentTimeMillis() - lastClickTime) > 10) {
						NotesListModel model = (NotesListModel) getModel();
						TreeIter row = model.getIter(path);
						Note note = model.getNote(row);
						MouseButton b = event.getButton();
						Notes notes = Notes.getInstance();
						NotesWindow notesWindow = NotesWindow.getInstance();
						if(b == MouseButton.LEFT) {
							if(!note.isEditing()) {
								notesWindow.openNote(note);
							} else {
								notesWindow.closeNote(note);
							}
						} else if(b == MouseButton.MIDDLE) {
							notesWindow.closeNote(note);
							notes.removeNoteAndUpdate(note);
						} else if(b == MouseButton.RIGHT) {
							if(!note.isEditing()) {
								notesWindow.invokeProperties(note);
							}
						}
						lastClickTime = System.currentTimeMillis();
					}
					return true;
				}
			});
		}
	}

	public ListTree getTree() {
		return tree;
	}

	private boolean noteInTag(Note note, String tag) {
		String[] noteTags = note.getTags().split(",");
		if(tag == null) return true;
		for(String noteTag: noteTags) {
			if(noteTag.equals(tag)) {
				return true;
			}
		}
		return false;
	}

	public void update(ArrayList<Note> notesData, TagsList tagsList) {
		if(!tagsList.nothingSelected()) {
			model.clear();
			if(tagsList.trashSelected()) {
				for(Note note: notesData) {
					if(note.inTrash()) {
						model.addNote(note);
					}
				}
			} else {
				String tag = tagsList.getSelectedTag();
				for(Note note: notesData) {
					if(noteInTag(note, tag)) {
						if(!note.inTrash()) {
							model.addNote(note);
						}
					}
				}
				if(model.empty() && tag != null) {
					tagsList.selectAllRow();
				}
			}
		}
	}

	public void updateView(Note note) {
		model.updateView(note);
	}
}