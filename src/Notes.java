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
	public Note[] quickNotes;

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
		updateQuickIDs();
		notesList = new NotesList();
		tagsList = new TagsList();
		updateLists();
		NotesWindow.init(notesList, tagsList);
		window = NotesWindow.getInstance();
		keys = new Keys();
		Gtk.main();
	}

	public void exit() {
		window.destroyChildren();
		base.closeQueue();
		Gtk.mainQuit();
		keys.cleanUp();
	}

	public Note newNote() {
		if(tagsList.trashSelected()) {
			tagsList.selectAllRow();
			updateLists();
		}
		Note note = new Note(tagsList.getSelectedTag());
		addNote(note);
		return note;
	}

	private void addNote(Note note) {
		base.newNote(note);
		notesData.add(note);
		updateNotesList();
	}

	public void removeNoteAndUpdate(Note note) {
		if(tagsList.trashSelected()) {
			removeNoteCompletelyAndUpdate(note);
		} else {
			removeNoteToTrashAndUpdate(note);
		}
	}

	public void removeNoteCompletelyAndUpdate(Note note) {
		notesData.remove(note);
		updateLists();
		base.removeNote(note);
	}

	public void removeNoteToTrashAndUpdate(Note note) {
		note.removeToTrash();
		updateLists();
		notesList.updateView(note);
		base.updateNote(note);
	}

	public void updateLists() {
		tagsList.update(notesData);
		if(window != null) {
			window.updateListsWindowView();
		}
	}

	public void updateNotesList() {
		notesList.update(notesData, tagsList);
	}

	public void updateNoteView(Note note) {
		notesList.updateView(note);
	}

	public void updateNote(Note note) {
		updateQuickIDs(note);
		notesList.updateView(note);
		base.updateNote(note);
		updateLists();
	}

	public void startEditing(Note note) {
		note.startEditing();
		notesList.updateView(note);
	}

	public void finishEditing(Note note) {
		note.finishEditing();
    	if(note.empty()) {
    		removeNoteCompletelyAndUpdate(note);
    	} else {
    		notesList.updateView(note);
    	}
	}

	public void updateQuickIDs() {
		quickNotes = new Note[9];
		for(int i = 0; i <= 8; i++) {
			quickNotes[i] = null;
		}
		for(Note note: notesData) {
			int quick = note.getQuick();
			if(quick != 0) {
				quickNotes[quick - 1] = note;
			}
		}
	}

	public void updateQuickIDs(Note note) {
		int quick = note.getQuick();
		for(int i = 0; i <= 8; i++) {
			if((quickNotes[i] != null) && (quickNotes[i].equals(note))) {
				quickNotes[i] = null;
			}
		}
		if(quick != 0) {
			quickNotes[quick - 1] = note;
		}
	}

	public void openQuick(int num) {
		// if(quickIDs[num - 1] != -1) {
		// 	try {
		// 		Note note = findNote(quickIDs[num - 1]);
		// 		window.openNote(note);
		// 	} catch(Exception ex) {ex.printStackTrace();}
		// }
		// for(Note note: notesData) {
		// 	if(note.getQuick() == num) {
		if(quickNotes[num - 1] != null) {
			Note note = quickNotes[num - 1];
			if(!window.closeNote(note)) {
				window.openNote(note);
			}
		}
		// break;
		// 	}
		// }
	}

	public void clearQuick(int num) {
		if(quickNotes[num - 1] != null) {
			quickNotes[num - 1].setQuick(0);
			quickNotes[num - 1] = null;
		}
	}
}