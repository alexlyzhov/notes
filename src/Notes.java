import org.gnome.gtk.Gtk;
import java.util.ArrayList;

public class Notes {
	private Base base;
	private Keys keys;
	private NotesWindow window;
	private ArrayList<Note> notesData;
	private NotesList notesList;
	private TagsList tagsList;

	public static void main(String[] args) {new Notes(args);}

	public Notes(String[] args) {
		base = new Base();
		notesData = base.getNotes();
		Gtk.init(args);
		notesList = new NotesList(this); //send notesData as a parameter
		tagsList = new TagsList(this);
		updateTagsList();
		window = new NotesWindow(args, this, notesList, tagsList);
		keys = new Keys(this, window);
		Gtk.main();
	}

	public void exit() {
		base.closeQueue();
		keys.cleanUp();
		Gtk.mainQuit();
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
		updateTagsList();
		updateNotesList();
	}

	public void removeNoteCompletely(Note note) {
		notesData.remove(note);
		base.removeNote(note);
	}

	public void removeNoteToTrash(Note note) {
		note.removeToTrash();
		updateNote(note);
	}

	public void updateTagsList() {
		tagsList.update(notesData);
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
		notesList.updateView(note);
	}
}