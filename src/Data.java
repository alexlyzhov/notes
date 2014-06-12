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
	private java.util.List<QuickSlot> quickData;

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
			quickData = quickDao.queryForAll();
		} catch(SQLException ex) {ex.printStackTrace();}
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

	public void registerQuickSlot(Note note, int slotNumber) {
		QuickSlot noteSlot = findSlotByNote(note);
		if(slotNumber == 0) {
			if(noteSlot != null) {
				quickData.remove(noteSlot);
				(new Thread(new DeleteQuickSlotRunnable(noteSlot))).start();
			}
		} else {
			if(noteSlot != null) {
				QuickSlot existingSlot = findSlotByNumber(slotNumber);
				if(existingSlot != null) {
					quickData.remove(existingSlot);
				}
				noteSlot.slotNumber = slotNumber;
				(new Thread(new UpdateQuickSlotRunnable(noteSlot))).start();
			} else {
				QuickSlot newSlot = new QuickSlot(slotNumber, note.id);
				quickData.add(newSlot);
				(new Thread(new CreateQuickSlotRunnable(newSlot))).start();
			}
		}
	}

	public Note findQuickNote(int slotNumber) {
		QuickSlot slot = findSlotByNumber(slotNumber);
		if(slot != null) {
			for(Note note: notesData) {
				if(note.id == slot.noteID) {
					return note;
				}
			}
		}
		return null;
	}

	private QuickSlot findSlotByNote(Note note) {
		for(QuickSlot slot: quickData) {
			if(slot.noteID == note.id) {
				return slot;
			}
		}
		return null;
	}

	public int findSlotNumberByNote(Note note) {
		QuickSlot slot = findSlotByNote(note);
		if(slot != null) {
			return slot.slotNumber;
		} else {
			return 0;
		}
	}

	private QuickSlot findSlotByNumber(int slotNumber) {
		for(QuickSlot slot: quickData) {
			if(slot.slotNumber == slotNumber) {
				return slot;
			}
		}
		return null;
	}

	private class CreateQuickSlotRunnable implements Runnable {
		private QuickSlot slot;

		public CreateQuickSlotRunnable(QuickSlot slot) {
			this.slot = slot;
		}

		public void run() {
			try {
				quickDao.create(slot);
			} catch(SQLException ex) {ex.printStackTrace();}
		}
	}

	private class UpdateQuickSlotRunnable implements Runnable {
		private QuickSlot slot;

		public UpdateQuickSlotRunnable(QuickSlot slot) {
			this.slot = slot;
		}

		public void run() {
			try {
				quickDao.update(slot);
			} catch(SQLException ex) {ex.printStackTrace();}
		}
	}

	private class DeleteQuickSlotRunnable implements Runnable {
		private QuickSlot slot;

		public DeleteQuickSlotRunnable(QuickSlot slot) {
			this.slot = slot;
		}

		public void run() {
			try {
				quickDao.delete(slot);
			} catch(SQLException ex) {ex.printStackTrace();}
		}
	}
}