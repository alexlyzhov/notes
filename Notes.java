import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import java.util.Arrays;
import java.util.ArrayList;

public class Notes extends Window {
	private Base base;
	private Keys keys;
	private ArrayList<Editor> editors = new ArrayList<Editor>();
	private boolean visible;
	public boolean showTags = true;
	private boolean showTrash;

	private NotesList notesList;
	private TagsList tagsList;
	private NotesVBox vbox;

	private ArrayList<Note> notesData;

	public static void main(String args[]) {
		Gtk.init(args);
		new Notes(args);
	}

	private Notes(String args[]) {
		initBase();
		initHotkeys();

		setTitle("Notes");
		setSunIcon();
		setLeftLocation();
		exitOnDelete();

		notesList = new NotesList(this);
		tagsList = new TagsList(this);
		updateTagsList();
		updateNotesList();

		vbox = new NotesVBox(notesList, tagsList);
		add(vbox);
		if(!runHidden(args)) {
			toggleVisible();
		}
		Gtk.main();
	}

	private void exit() {
		if(keys != null) keys.cleanUp();
		base.closeQueue();
        Gtk.mainQuit();
	}

	private boolean runHidden(String args[]) {
		if(Arrays.asList(args).contains("hide")) return true;
		return false;
	}

	private void initBase() {
		base = new Base();
		notesData = base.getNotes();
	}

	private void initHotkeys() {
		if(System.getProperty("os.name").equals("Linux")) keys = new Keys(this);
		else System.out.println("Global hotkeys with JXGrabKey are not supported on your platform");
	}

	public void toggleVisible() {
		if(visible) hide();
		else showAll();
		visible = !visible; 
	}

	private void setSunIcon() {
		try {
			Pixbuf sun = new Pixbuf("ico/sun.png");
			setIcon(sun);
		} catch(Exception ex) {ex.printStackTrace();}
	}

	private void setLeftLocation() {
		int sw = getScreen().getWidth();
		int sh = getScreen().getHeight();
		int w = sw * 2 / 10;
		int h = sh * 7 / 10;
		int x = sw / 10;
		int y = (sh - h) / 2;
		setDefaultSize(w, h);
		move(x, y);
	}

	private void exitOnDelete() {
		connect(new Window.DeleteEvent() {
		    public boolean onDeleteEvent(Widget source, Event event) {
		    	exit();
		        return false;
		    }
		});
	}

	public void toggleTags() {
		showTags = !showTags;
		vbox.togglePack();
		for(Editor editor: editors) {
			editor.toggleTags();
		}
	}

	public void toggleTrash() {
		if(showTrash && tagsList.lastSelected()) tagsList.selectAllRow();
		showTrash = !showTrash; //show trash only if there is something inside
		updateTagsList();
	}

	private class NewNoteButton extends Button {
		private NewNoteButton() {
			super("New note");

			Pixbuf edit = null;
			try {
				edit = new Pixbuf("ico/edit.png");
			} catch(Exception ex) {ex.printStackTrace();}
			setImage(new Image(edit));

			connect(new Button.Clicked() {
				public void onClicked(Button button) {
					createNote();
				}
			});
		}
	}

	private class PanedLists extends HPaned {
		private PanedLists(NotesList notesList, TagsList tagsList) {
			super(notesList, tagsList);
			setPosition(getWidth() * 2 / 3);
		}
	}

	private class NotesVBox extends VBox {
		private NewNoteButton button;
		private PanedLists paned;
		private NotesVBox(NotesList notesList, TagsList tagsList) {
			super(false, 0);
			button = new NewNoteButton();
			paned = new PanedLists(notesList, tagsList);
			packStart(button, false, false, 0);
			packEnd(paned, true, true, 0);
		}

		private void togglePack() {
			Widget[] elems = getChildren();
			if(Arrays.asList(elems).contains(paned)) {
				remove(paned);
				for(Widget widget: paned.getChildren()) {
					if(widget.equals(notesList)) {
						paned.remove(notesList);
					}
				}
				packEnd(notesList, true, true, 0);
			} else if(Arrays.asList(elems).contains(notesList)) {
				boolean listInPaned = false;
				for(Widget widget: paned.getChildren()) {
					if(widget.equals(notesList)) listInPaned = true;
				}
				remove(notesList);
				if(listInPaned == false) {
					paned.add1(notesList);
				}
				packEnd(paned, true, true, 0);
			} else System.out.println("Nothing to toggle and pack about");
		}
	}


    //================================================================================
    // Dealing with notes, database and lists
    //================================================================================


	public void openNote(Note note) {
		new Editor(note, this);
	}

	public void createNote() {
		openNote(newNote());
	}

	public void updateTagsList() {
		String selected = tagsList.getSelectedTag();
		tagsList.clear();
		for(Note note: notesData) {
			tagsList.addNoteTags(note);
		}
		if(showTrash) {
			tagsList.addTrash();
		} 
		TreeIter selectedRow = tagsList.getRow(selected);
		if(selectedRow != null) tagsList.selectRow(selectedRow);
	}

	public void updateNotesList() {
		String tag = tagsList.getSelectedTag();
		notesList.clear();
		for(Note note: notesData) {
			if(noteInTag(note, tag)) {
				notesList.addNote(note);
			}
		}
		if(notesList.empty() && tag != null) {
			tagsList.selectAllRow();
			updateNotesList();
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

	public Note newNote() {
		String tag = tagsList.getSelectedTag();
		Note note = null;
		if(tag == null) note = new Note();
		else note = new Note(tag);
		base.newNote(note);
		notesData.add(note);
		updateNotesList();
		return note;
	}

	public void updateNote(Note note) {
		base.updateNote(note, this);
	}

	public void updateView(Note note) {
		notesList.updateView(note);
	}

	public void removeNote(Note note) {
		if(showTrash && tagsList.lastSelected()) {
			notesData.remove(note);
			base.removeNote(note);
		} else {
			String tags = note.getTags();
			if(tags.equals("")) tags = "Trash";
			else tags = tags + ",Trash";
			note.setTags(tags);
		}
		updateNotesList();
		updateTagsList();
	}

	public void startEditing(Note note) {
		note.startEditing();
		notesList.updateView(note);
	}

	public void finishEditing(Note note) {
		note.finishEditing();
		notesList.updateView(note);
	}

	public ArrayList<Editor> getEditors() {
		return editors;
	}
}