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
	private Dao<QuickSlot, Integer> quickDao;

	public java.util.List<Note> notesData;
	public java.util.List<List> listsData;
	private QuickAccess quickAccess;

	public Data() {
		System.setProperty(com.j256.ormlite.logger.LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "fatal");
		try {
			connectionSource = new JdbcConnectionSource(DATABASE_URL);
			noteDao = DaoManager.createDao(connectionSource, Note.class);
			listDao = DaoManager.createDao(connectionSource, List.class);
			quickDao = DaoManager.createDao(connectionSource, QuickSlot.class);

			TableUtils.createTableIfNotExists(connectionSource, Note.class);
			TableUtils.createTableIfNotExists(connectionSource, List.class);
			TableUtils.createTableIfNotExists(connectionSource, QuickSlot.class);

			notesData = noteDao.queryForAll();
			listsData = listDao.queryForAll();
		} catch(SQLException ex) {ex.printStackTrace();}

		quickAccess = new QuickAccess(this, quickDao);
	}

	public QuickAccess getQuickAccess() {
		return quickAccess;
	}

	public void quit() {
		try {
			connectionSource.close();
		} catch(SQLException ex) {ex.printStackTrace();}
	}

	public Note newNote() {
		final Note note = new Note(true);
		notesData.add(note);
		(new Thread(new CreateNoteRunnable(note))).start();
		return note;
	}

	public void removeNoteCompletelyAndUpdate(Note note) {
		notesData.remove(note);
		(new Thread(new DeleteNoteRunnable(note))).start();
	}

	public void removeNoteToTrashAndUpdate(Note note) {
		note.trash = true;
		(new Thread(new UpdateNoteRunnable(note))).start();
	}

	public void updateNote(Note note) {
		(new Thread(new UpdateNoteRunnable(note))).start();
	}

	public void startEditing(Note note) {
		note.editing = true;
	}

	public void finishEditing(Note note) {
		note.editing = false;
	}

	public boolean trashExists() {
		for(Note note: notesData) {
			if(note.trash) {
				return true;
			}
		}
		return false;
	}

	private class CreateNoteRunnable implements Runnable {
		private Note note;

		public CreateNoteRunnable(Note note) {
			this.note = note;
		}

		public void run() {
			try {
				noteDao.create(note);
			} catch(SQLException ex) {ex.printStackTrace();}
		}
	}

	private class UpdateNoteRunnable implements Runnable {
		private Note note;

		public UpdateNoteRunnable(Note note) {
			this.note = note;
		}

		public void run() {
			try {
				noteDao.update(note);
			} catch(SQLException ex) {ex.printStackTrace();}
		}
	}

	private class DeleteNoteRunnable implements Runnable {
		private Note note;

		public DeleteNoteRunnable(Note note) {
			this.note = note;
		}

		public void run() {
			try {
				noteDao.delete(note);
			} catch(SQLException ex) {ex.printStackTrace();}
		}
	}
}