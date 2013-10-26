import org.gnome.gtk.*;
import org.gnome.gdk.EventButton;
import org.gnome.gdk.MouseButton;
import java.util.ArrayList;

public class NotesList extends ScrolledWindow {
	private final Notes notes;
	private final DataColumnString nameColumn = new DataColumnString();
	private final DataColumnString timeColumn = new DataColumnString();
	private final DataColumnReference<Note> noteColumn = new DataColumnReference<Note>();
	private ListStore model;
	private TreeView tree;

	public NotesList(Notes notesParam) {
		this.notes = notesParam;

		setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
		getVAdjustment().connect(new Adjustment.Changed() {
			public void onChanged(Adjustment source) {
				source.setValue(0);
			}
		});

		model = new ListStore(new DataColumn[] {nameColumn, timeColumn, noteColumn});
		tree = new TreeView(model);
		add(tree);

		tree.setHeadersVisible(false);
		TreeViewColumn nameViewColumn = tree.appendColumn();
		nameViewColumn.setTitle("Name");
		new CellRendererText(nameViewColumn).setText(nameColumn);
		model.setSortColumn(timeColumn, SortType.DESCENDING);

		tree.connect(new Widget.ButtonPressEvent() {
			public boolean onButtonPressEvent(Widget source, EventButton event) {
				TreePath path = tree.getPathAtPos((int) event.getX(), (int) event.getY());
				if(path != null) {
					TreeIter row = model.getIter(path);
					Note note = getNote(row);
					if(!note.isEditing()) {
						if(event.getButton() == MouseButton.LEFT) {
							notes.openNote(note);
						} else if(event.getButton() == MouseButton.RIGHT) {
							notes.removeNote(note);
						}
					}
				}
				return true;
			}
		});
	}

	public void clear() {
		model.clear();
	}

	public void update(ArrayList<Note> notesData, TagsList tagsList) {
		if(!tagsList.nothingSelected()) {
			String tag = tagsList.getSelectedTag();
			clear();
			for(Note note: notesData) {
				if(noteInTag(note, tag)) {
					addNote(note);
				}
			}
			if(empty() && tag != null) {
				tagsList.selectAllRow();
			}
		}
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

	public void addNote(Note note) {
		TreeIter row = model.appendRow();
		model.setValue(row, noteColumn, note);
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
		if(note.isEditing()) name = name + " *";
		model.setValue(row, nameColumn, name);
	}

	private void updateTime(TreeIter row) {
		Note note = getNote(row);
		String time = note.getTime();
		model.setValue(row, timeColumn, time);
	}

	private Note getNote(TreeIter row) {
		return model.getValue(row, noteColumn);
	}

	private TreeIter getRow(Note note) {
		TreeIter row = model.getIterFirst();
		do {
			if(getNote(row).equals(note)) return row;
		} while(row.iterNext());
		return null;
	}
}