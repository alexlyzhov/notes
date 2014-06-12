import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;

public class QuickAccess {
	private Data data;
	private Dao<QuickSlot, Integer> quickDao;
	private java.util.List<QuickSlot> quickData;

	public QuickAccess(Data data, Dao<QuickSlot, Integer> quickDao) {
		this.data = data;
		this.quickDao = quickDao;
		try {
			quickData = quickDao.queryForAll();
		} catch(SQLException ex) {ex.printStackTrace();}
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
			for(Note note: data.notesData) {
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