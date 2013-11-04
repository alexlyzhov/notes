import org.gnome.gtk.Gtk;
import java.util.ArrayList;

public class Notes {
	private static Notes notes;
	private String[] args;
	private Base base;
	private Keys keys;
	private NotesWindow window;
	private ArrayList<Note> notesData;
	private NotesList notesList;
	private TagsList tagsList;

	public static void main(String[] args) {
		notes = new Notes(args);
		notes.init();
	}

	public static Notes getInstance() {
		return notes;
	}

	private Notes(String[] args) {
		this.args = args;
	}

	private void init() {
		base = new Base(args);
		Gtk.init(args);
		notesData = base.getNotes();
		notesList = new NotesList();
		tagsList = new TagsList();
		updateTagsList();
		window = new NotesWindow(args, notesList, tagsList);
		keys = new Keys();
		Gtk.main();
	}

	public void exit() {
		window.destroyEditors();
		base.closeQueue();
		Gtk.mainQuit();
		keys.cleanUp();
	}

	public void createNote() {
		Note note = newNote();
		addNote(note);
		openNote(note);
	}

	private Note newNote() {
		String tag = tagsList.getSelectedTag();
		return tag == null ? new Note() : new Note(tag);
	}

	private void addNote(Note note) {
		base.newNote(note);
		notesData.add(note);
		updateNotesList();
	}

	public void openNote(Note note) {
		window.newEditor(note);
	}

	public void removeNote(Note note) {
		if(tagsList.trashSelected()) {
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
		note.removeToTrash();
		updateNote(note);
		updateTagsList();
		updateNotesList();
	}

	public void updateTagsList() {
		tagsList.update(notesData);
		if(window != null) {
			if(tagsList.noTags()) {
				window.showNotesList();
			} else {
				window.showPaned();
			}
		}
	}

	public void updateNotesList() {
		notesList.update(notesData, tagsList);
	}

	public void updateNote(Note note) {
		base.updateNote(note, notesList);
	}

	public void startEditing(Note note) {
		note.startEditing();
		notesList.updateView(note);
	}

	public void finishEditing(Note note) {
		note.finishEditing();
    	if(note.empty()) {
    		removeNoteCompletely(note);
    	}
		notesList.updateView(note);
	}

	public NotesWindow getWindow() {
		return window;
	}
}