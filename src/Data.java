import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;

public class Data {
	private final static String DATABASE_URL = "jdbc:sqlite:notes.db";
	private JdbcConnectionSource connectionSource;
	private Dao<Note, Integer> noteDao;
	private Dao<List, Integer> listDao;

	public java.util.List<Note> notesData; //how data and dao should interact? maybe these variables are not needed and interface can reflect db directly?
	public java.util.List<List> listsData; //make it public
	private Note[] quickNotes; //

	public Data() {
		System.setProperty(com.j256.ormlite.logger.LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "fatal");
		try {
			connectionSource = new JdbcConnectionSource(DATABASE_URL); //establishing db connection
			noteDao = DaoManager.createDao(connectionSource, Note.class);
			listDao = DaoManager.createDao(connectionSource, List.class);

			TableUtils.createTableIfNotExists(connectionSource, Note.class); //creating tables
			TableUtils.createTableIfNotExists(connectionSource, List.class);

			notesData = noteDao.queryForAll(); //fetching data
			listsData = listDao.queryForAll();
		} catch(SQLException ex) {ex.printStackTrace();}
		// createQuickAccess();
	}

	public void quit() {
		try {
			connectionSource.close();
		} catch(SQLException ex) {ex.printStackTrace();}
	}

	// private void createQuickAccess() { //quick access to a separate class
	// 	quickNotes = new Note[9];
	// 	for(int i = 0; i <= 8; i++) {
	// 		quickNotes[i] = null;
	// 	}
	// 	for(Note note: notesData) {
	// 		int quick = note.quick;
	// 		if(quick != 0) {
	// 			quickNotes[quick - 1] = note;
	// 		}
	// 	}
	// }

	public void updateQuickAccess(Note note) {
	// 	int num = note.quick;
	// 	if((num != 0) && (quickNotes[num - 1] != null)) {
	// 		quickNotes[num - 1].quick = 0;
	// 		quickNotes[num - 1] = null;
	// 	}
	// 	for(int i = 0; i <= 8; i++) {
	// 		if((quickNotes[i] != null) && (quickNotes[i].equals(note))) { //try == comparison and everywhere
	// 			quickNotes[i] = null;
	// 		}
	// 	}
	// 	if(num != 0) {
	// 		quickNotes[num - 1] = note;
	// 	}
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

	public Note newNote() {
		Note note = new Note(System.currentTimeMillis());
		try {
			noteDao.create(note);
		} catch(SQLException ex) {ex.printStackTrace();}
		notesData.add(note);
		return note;
	}

	public void removeNoteCompletelyAndUpdate(Note note) {
		notesData.remove(note);
		try {
			noteDao.delete(note);
		} catch(SQLException ex) {ex.printStackTrace();}
	}

	public void removeNoteToTrashAndUpdate(Note note) {
		note.trash = true;
		try {
			noteDao.update(note);
		} catch(SQLException ex) {ex.printStackTrace();}
	}

	public void updateNote(Note note) {
		updateQuickAccess(note);
		try {
			noteDao.update(note);
		} catch(SQLException ex) {ex.printStackTrace();}
	}

	public void startEditing(Note note) {
		note.editing = true;
	}

	public boolean finishEditing(Note note) { //make it void and check for existence on the upper level
		note.editing = false;
    	if(note.empty()) {
    		removeNoteCompletelyAndUpdate(note);
    		return false;
    	}
    	return true;
	}
}