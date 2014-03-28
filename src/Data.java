import java.util.ArrayList;

public class Data {
	private Base base;
	public ArrayList<Note> notesData;
	public ArrayList<List> listsData;
	public Note[] quickNotes;

	private NotesStore notesStore;
	private ListsStore listsStore;

	public Data(Base base) {
		this.base = base;
		notesData = base.getNotes();
		listsData = base.getLists();
		createQuickAccess();
	}

	private void createQuickAccess() { //quick access to a separate class
		quickNotes = new Note[9];
		for(int i = 0; i <= 8; i++) {
			quickNotes[i] = null;
		}
		for(Note note: notesData) {
			int quick = note.quick;
			if(quick != 0) {
				quickNotes[quick - 1] = note;
			}
		}
	}

	public void updateQuickAccess(Note note) {
		int num = note.quick;
		if((num != 0) && (quickNotes[num - 1] != null)) {
			quickNotes[num - 1].quick = 0;
			quickNotes[num - 1] = null;
		}
		for(int i = 0; i <= 8; i++) {
			if((quickNotes[i] != null) && (quickNotes[i].equals(note))) { //try == comparison and everywhere
				quickNotes[i] = null;
			}
		}
		if(num != 0) {
			quickNotes[num - 1] = note;
		}
	}

	public void setStores(NotesStore notesStore, ListsStore listsStore) {
		this.notesStore = notesStore;
		this.listsStore = listsStore;
	}

	public void updateStores() {
		notesStore.update(listsStore);
		listsStore.update();
	}

	public Note getQuickNote(int num) {
		return quickNotes[num - 1];
	}

	public boolean trashExists() {
		for(Note note: notesData) {
			if(note.trash) {
				return true;
			}
		}
		return false;
	}

	public Note newNote(List list) {
		if(listsStore.trashListSelected()) {
			listsStore.selectGeneralList();
			listsStore.update();
		}
		Note note = new Note();
		if(listsStore.actualList(list)) {
			list.notes.add(note);
		}
		base.newNote(note);
		notesData.add(note);
		notesStore.update(listsStore); //updateStores()?
		return note;
	}

	public void removeNoteAndUpdate(Note note) {
		if(listsStore.trashListSelected()) {
			removeNoteCompletelyAndUpdate(note);
		} else {
			removeNoteToTrashAndUpdate(note);
		}
	}

	public void removeNoteCompletelyAndUpdate(Note note) {
		notesData.remove(note);
		updateStores();
		base.removeNote(note);
	}

	public void removeNoteToTrashAndUpdate(Note note) {
		note.trash = true;
		updateStores();
		base.updateNote(note);
	}

	public void updateNoteView(Note note) {
		notesStore.updateInfo(note);
	}

	public void updateNote(Note note) {
		updateNoteView(note);
		updateQuickAccess(note);
		base.updateNote(note);
		updateStores();
	}

	public void startEditing(Note note) {
		note.editing = true;
		updateNoteView(note);
	}

	public void finishEditing(Note note) {
		note.editing = false;
    	if(note.empty()) {
    		removeNoteCompletelyAndUpdate(note);
    	} else {
    		updateNoteView(note);
    	}
	}
}