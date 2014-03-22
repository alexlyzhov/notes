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
	public int[] quickIDs;

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
		Note note = new Note(tagsList.getSelectedTag());
		addNote(note);
		return note;
	}

	private void addNote(Note note) {
		base.newNote(note, notesList);
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
		base.updateNote(note, notesList, false);
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

	public void updateNote(Note note, boolean updateTime) {
		updateQuickIDs();
		base.updateNote(note, notesList, updateTime);
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
		int[] quickIDs = new int[9];
		for(int i = 0; i <= 8; i++) {
			quickIDs[i] = -1;
		}
		for(Note note: notesData) {
			int quickNum = getNoteQuickNum(note);
			if(quickNum != -1) {
				quickIDs[quickNum - 1] = note.getID();
			}
		}
		this.quickIDs = quickIDs;
	}

	public int getNoteQuickNum(Note note) {
		if(note.removedToTrash()) {
			return -1;
		}
		String[] tags = note.getTags().split(",");
		for(String tag: tags) {
			int num = getTagQuickNum(tag);
			if(num != -1) {
				return num;
			}
		}
		return -1;
	}

	public int getTagQuickNum(String tag) {
		if(tag.startsWith("Quick")) {
			int result = -1;
			try {
				result = Integer.parseInt(tag.substring(5));
				if((result < 1) || (result > 9)) {
					result = -1;
				}
			} catch(Exception ex) {}
			return result;
		}
		return -1;
	}

	public void openQuick(int num) {
		if(quickIDs[num - 1] != -1) {
			try {
				Note note = findNote(quickIDs[num - 1]);
				window.openNote(note);
			} catch(Exception ex) {ex.printStackTrace();}
		}
	}

	public Note findNote(int id) throws Exception {
		for(Note note: notesData) {
			if(note.getID() == id) {
				return note;
			}
		}
		throw new Exception();
	}
}