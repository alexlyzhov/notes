import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import java.util.Arrays;
import java.util.ArrayList;

public class Notes extends Window {
	private App app;
	private Base base;
	private ArrayList<Note> notesData;

	private ArrayList<Editor> editors = new ArrayList<Editor>();
	private boolean visible;
	private boolean showTags = true;
	private boolean showTrash;
	private NotesVBox vbox;
	private NotesList notesList;
	private TagsList tagsList;

	public Notes(String args[], Base base, App app) {
		this.app = app;
		this.base = base;

		notesData = base.getNotes();
		notesList = new NotesList(this);
		tagsList = new TagsList(this);
		updateTagsList();

		setTitle("Notes");
		setSunIcon();
		setLeftLocation();
		exitOnDelete();
		vbox = new NotesVBox(notesList, tagsList);
		add(vbox);
		if(!runHidden(args)) toggleVisible();
	}

	private boolean runHidden(String args[]) {
		if(Arrays.asList(args).contains("hide")) return true;
		return false;
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
		    	app.exit();
		        return false;
		    }
		});
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


	public void toggleTags() {
		showTags = !showTags;
		vbox.togglePack();
		for(Editor editor: editors) {
			editor.toggleTags();
		}
	}

	public void toggleTrash() {
		if(showTrash) {
			tagsList.removeTrash();
			if(tagsList.lastSelected()) tagsList.selectAllRow();
		} else {
			tagsList.addTrash();
		}
		showTrash = !showTrash;
	}

	public boolean tagsShown() {
		return showTags;
	}

	public boolean trashShown() {
		return showTrash;
	}

	public void updateTagsList() {
		tagsList.update(notesData);
	}

	public void updateNotesList() {
		notesList.update(notesData, tagsList);
	}

	public void updateNote(Note note) {
		base.updateNote(note, this);
	}

	public void updateView(Note note) {
		notesList.updateView(note);
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



	public void openNote(Note note) {
		new Editor(note, this);
	}

	public void createNote() {
		openNote(newNote());
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

	public void removeNote(Note note) {
		if(showTrash && tagsList.lastSelected()) {
			removeNoteCompletely(note);
		} else {
			removeNoteToTrash(note);
		}
	}

	public void removeNoteCompletely(Note note) {
		notesData.remove(note);
		base.removeNote(note);
		updateTagsList();
		updateNotesList();
	}

	public void removeNoteToTrash(Note note) {
		String tags = note.getTags();
		if(tags.equals("")) tags = "Trash";
		else tags = tags + ",Trash";
		note.setTags(tags);
		updateNote(note);
		updateTagsList();
		updateNotesList();
	}
}