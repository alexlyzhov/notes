import org.gnome.gtk.*;
import org.gnome.gdk.EventButton;
import org.gnome.gdk.MouseButton;
import java.util.ArrayList;

public class NotesList {
	private NotesListModel model;
	private NotesListTree tree;
	private final DataColumnString nameColumn = new DataColumnString();
	private final DataColumnString timeColumn = new DataColumnString();
	private final DataColumnReference<Note> noteColumn = new DataColumnReference<Note>();
	private final DataColumn[] columns = new DataColumn[] {nameColumn, timeColumn, noteColumn};

	public NotesList() {
		model = new NotesListModel(columns);
		tree = new NotesListTree(model);
	}

	private class NotesListModel extends ListStore {

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
			if(model.getIterFirst() == null) return true;
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
			String name = note.getName();
			if(name.equals("")) name = "Nameless";
			if(note.isEditing()) {
				name = "<b>" + name + "</b>";
			}
			setValue(row, nameColumn, name);
		}

		private void updateTime(TreeIter row) {
			Note note = getNote(row);
			String time = note.getTime();
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

	private class NotesListTree extends TreeView {
		private NotesListTree(NotesListModel model) {
			super(model);
			setHeadersVisible(false);
			TreeViewColumn nameViewColumn = appendColumn();
			nameViewColumn.setTitle("Name");
			new CellRendererText(nameViewColumn).setMarkup(nameColumn);
			connectToAction();
		}

		private void connectToAction() {
			connect(new Widget.ButtonPressEvent() {
				public boolean onButtonPressEvent(Widget source, EventButton event) {
					TreePath path = getPathAtPos((int) event.getX(), (int) event.getY());
					if(path != null) {
						NotesListModel model = (NotesListModel) getModel();
						TreeIter row = model.getIter(path);
						Note note = model.getNote(row);
						MouseButton b = event.getButton();
						if(b == MouseButton.LEFT) {
							if(!note.isEditing()) {
								Notes.getInstance().getWindow().newEditor(note);
							} else {
								Notes.getInstance().getWindow().closeEditor(note);
							}
						} else if(!note.isEditing()) {
							if(b == MouseButton.MIDDLE) {
								Notes.getInstance().removeNote(note);
							} else if(b == MouseButton.RIGHT) {
								Notes.getInstance().getWindow().newProperties(note);
							}
						}
					}
					return true;
				}
			});
		}
	}

	private class NotesListScrolled extends ScrolledWindow {
		private NotesListScrolled(NotesListTree tree) {
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

	private boolean noteInTag(Note note, String tag) {
		String[] noteTags = note.getTags().split(",");
		if(tag == null) {
			for(String noteTag: noteTags) {
				if(noteTag.equals("Trash")) {
					return false;
				}
			}
		} else if(!tag.equals("Trash")) {
			for(String noteTag: noteTags) {
				if(noteTag.equals("Trash")) {
					return false;
				}
			}
		}
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
			String tag = tagsList.getSelectedTag();
			model.clear();
			for(Note note: notesData) {
				if(noteInTag(note, tag)) {
					model.addNote(note);
				}
			}
			if(model.empty() && tag != null) {
				tagsList.selectAllRow();
			}
		}
	}

	public void updateView(Note note) {
		model.updateView(note);
	}
}