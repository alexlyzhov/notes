import com.trolltech.qt.core.*;
import java.util.List;
import java.util.Vector;
import java.util.Arrays;

public class NotesData extends QAbstractListModel {
	public NotesDatabase notesDatabase;
	private List<Note> list;

	public NotesData() {
		notesDatabase = new NotesDatabase();
		list = new Vector<Note>();
		update();
	}

	public int rowCount(QModelIndex index) {
		return list.size();
	}

	public Object data(QModelIndex index, int role) {
		if(role == Qt.ItemDataRole.DisplayRole) {
			return get(index);
		}
		return null;
	}

	public void update() {
		list.clear();
		list.addAll(Arrays.asList(notesDatabase.getNotesList()));
	}

	public void exit() {
		notesDatabase.closeQueue();
	}

	public Note get(QModelIndex index) {
		return list.get(index.row());
	}

	public void remove(Note note) {
		notesDatabase.removeNote(note);
	}
}