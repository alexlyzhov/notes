import org.gnome.gtk.Gtk;
import java.util.ArrayList;

public class Notes {
	private static Notes notes;
	private NotesWindow window;
	private Base base;
	private Keys keys;
	private NotesList notesList;
	private TagsList tagsList;

	private ArrayList<Note> notesData;

	public static void main(String[] args) {
		Gtk.init(args);
		Args.init(args);
		notes = new Notes();
		notes.init();
	}

	private Notes() {}

	public static Notes getInstance() {
		return notes;
	}

	private void init() {
		base = new Base();
		notesData = base.getNotes();
		notesList = new NotesList();
		tagsList = new TagsList();
		updateTagsList();
		window = new NotesWindow(notesList, tagsList);
		keys = new Keys();
		Gtk.main();
	}

	public void exit() {
		window.destroyChildren();
		base.closeQueue();
		Gtk.mainQuit();
		keys.cleanUp();
	}

	private Note newNote() {
		Note note = new Note(tagsList.getSelectedTag());
		addNote(note);
		return note;
	}

	private void addNote(Note note) {
		base.newNote(note, notesList);
		notesData.add(note);
		updateNotesList();
	}

	public void openNote(Note note) {
		window.newEditor(note);
		startEditing(note);
	}

	public void openNewNote() {
		openNote(newNote());
	}

	public void closeNote(Note note) {
		window.closeEditor(note);
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
		updateInfo();
	}

	public void removeNoteToTrash(Note note) {
		note.removeToTrash();
		base.updateNoteTags(note);
		updateInfo();
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

	public void updateInfo() {
		updateTagsList();
		updateNotesList();
	}

	public void updateNote(Note note) {
		base.updateNote(note, notesList);
		updateInfo();
	}

	public void updateNoteTags(Note note) {
		base.updateNoteTags(note);
	}

	public void startEditing(Note note) {
		note.startEditing();
		notesList.updateView(note);
	}

	public void finishEditing(Note note) {
		note.finishEditing();
    	if(note.empty()) {
    		removeNoteCompletely(note);
    	} else {
    		notesList.updateView(note);
    	}
	}

	public void invokeProperties(Note note) {
		window.newProperties(note);
	}

	public void toggleVisible() {
		window.toggleVisible();
	}
}