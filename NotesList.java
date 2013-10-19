import org.gnome.gtk.*;
import org.gnome.gdk.EventButton;
import org.gnome.gdk.MouseButton;
import java.util.ArrayList;

public class NotesList { //divide it to several new classes
	private Base base;
	private final TagsList tagsList;
	public static final DataColumnString nameColumn = new DataColumnString();
	public static final DataColumnString timeColumn = new DataColumnString();
	public static final DataColumnReference<Note> noteColumn = new DataColumnReference<Note>();
	private final ListStore model;
	private TreeView tree;
	private ArrayList<Note> initNotes;

	public NotesList(Base base, final TagsList tagsListParam) {
		this.base = base;
		this.tagsList = tagsListParam;
		model = new ListStore(new DataColumn[] {nameColumn, timeColumn, noteColumn});
		tree = new TreeView(model);
		initNotes = base.getNotes();
		updateTags();
		updateList(null);

		TreeViewColumn vertical = tree.appendColumn(); //setting visible structure
		vertical.setTitle("Name");
		CellRendererText text = new CellRendererText(vertical);
		text.setText(nameColumn);
		tree.setHeadersVisible(false);
		model.setSortColumn(timeColumn, SortType.DESCENDING);

		tree.connect(new Widget.ButtonPressEvent() { //grabbing events
			public boolean onButtonPressEvent(Widget source, EventButton event) {
				TreePath path = tree.getPathAtPos((int) event.getX(), (int) event.getY());
				if(path != null) {
					TreeIter row = model.getIter(path);
					if(!getNote(row).isEditing()) {
						if(event.getButton() == MouseButton.LEFT) {
							new Editor(model.getValue(row, noteColumn), NotesList.this, tagsList);
						} else if(event.getButton() == MouseButton.RIGHT) {
							removeNote(row);
						}
					}
				}
				return true;
			}
		});
	}

	public void updateTags() {
		tagsList.init();
		for(Note note: initNotes) {
			tagsList.updateTags(note);
		}
		tagsList.selectFirst();
		updateList(null);
	}

	public void updateList(String tag) {
		model.clear();
		boolean fl;
		for(Note note: initNotes) {
			String[] noteTags = note.getTags().split(",");
			for(String noteTag: noteTags) {
				fl = false;
				if(tag == null) fl = true;
				else if(tag.equals(noteTag)) fl = true;
				if(fl) {
					addNote(note);
					break;
				}
			}
		}
	}

	public Note getNote(TreeIter row) {
		return model.getValue(row, noteColumn);
	}

	private TreeIter addNote(Note note) {
		TreeIter row = model.appendRow();
		model.setValue(row, noteColumn, note);
		setData(row);
		return row;
	}

	public void setData(TreeIter row) {
		setName(row);
		setTime(row);
	}

	public void setData(Note note) {
		TreeIter row = findRow(note);
		if(row != null) setData(row);
	}

	public void setName(Note note) {
		TreeIter row = findRow(note);
		if(row != null) setName(row);
	}

	public TreeIter findRow(Note note) {
		TreeIter row = model.getIterFirst();
		do {
			if(model.getValue(row, noteColumn).equals(note)) return row;
		} while(row.iterNext());
		return null;
	}

	public void setName(TreeIter row) {
		Note note = getNote(row);
		String name = note.getName();
		if(name.equals("")) name = "Nameless";
		if(note.isEditing()) name = name + " *";
		model.setValue(row, nameColumn, name);
	}

	public void setTime(TreeIter row) {
		Note note = getNote(row);
		model.setValue(row, timeColumn, note.getTime());
	}

	public void onExit() {
		base.closeQueue();
	}

	public TreeView getTreeView() { //transform
		return tree;
	}

	public TreeModel getModel() { //get rid
		return getTreeView().getModel();
	}

	public Note newNote(String tag) {
		Note note;
		if(tag == null) note = new Note();
		else note = new Note(tag);
		base.newNote(note);
		initNotes.add(note);
		addNote(note);
		return note;
	}

	public void updateNote(Note note) {
		base.updateNote(note, this, findRow(note));
	}

	public void removeNote(TreeIter row) {
		if(row != null) {
			Note note = getNote(row);
			base.removeNote(note);
			initNotes.remove(note);
			model.removeRow(row);
			updateTags();
		}
	}
}