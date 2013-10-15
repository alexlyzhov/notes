import com.almworks.sqlite4java.*;
import org.gnome.gtk.*;

public class Base {
	private SQLiteQueue queue;

	public Base() {
		java.util.logging.Logger.getLogger("com.almworks.sqlite4java").setLevel(java.util.logging.Level.OFF);
		initQueue();
		createTable();
	}

	private void initQueue() {
		queue = new SQLiteQueue(new java.io.File("notes.db"));
		queue.start();
	}

	public void closeQueue() {
		if(queue != null) {
			try {
				queue.stop(true).join();
			} catch(InterruptedException ex) {ex.printStackTrace();}
		}
	}

	private void createTable() {
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
		        try {
		        	st = con.prepare("CREATE TABLE IF NOT EXISTS Notes (id INTEGER PRIMARY KEY, name VARCHAR, content TEXT, time TEXT)");
		        	st.step();
		        } catch(SQLiteException ex) {ex.printStackTrace();}
		        finally {if(st != null) st.dispose();}
		        return null;
		    }
		}).complete();
	}

	public void newNote(final Note note) { //profile
		queue.execute(new SQLiteJob<Object>() {
		    protected Long job(SQLiteConnection con) {
		        SQLiteStatement st = null;
	    		try {
	    			st = con.prepare("SELECT datetime('now')");
	    			st.step();
	    			String newDateTime = st.columnString(0);
	    			st = con.prepare("INSERT INTO Notes (name, content, time) VALUES (?, ?, ?)");
	    			st.bind(1, note.getTrueName()); st.bind(2, note.getContent()); st.bind(2, newDateTime);
	    			st.step();
	    			note.initiate(con.getLastInsertId(), newDateTime);
	    		} catch(SQLiteException ex) {ex.printStackTrace();}
	    		finally {if(st != null) st.dispose();}
		        return null;
		    }
		}).complete();		
	}

	public void updateNote(final Note note, final NotesList list, final TreeIter row) { //one method for updating name, and another for updating content
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
	    		try {
	    			st = con.prepare("SELECT datetime('now')");
	    			st.step();
	    			note.updateTime(st.columnString(0));
	    			st = con.prepare("UPDATE Notes SET name = ?, content = ?, time = ? WHERE id = ?");
	    			st.bind(1, note.getTrueName()); st.bind(2, note.getContent()); st.bind(3, note.getTime()); st.bind(4, note.getID());
	    			st.step();
	    		} catch(SQLiteException ex) {ex.printStackTrace();}
	    		finally {if(st != null) st.dispose();}
		        return null;
		    }
		    protected void jobFinished(Object nullObject) {
		    	list.setData(row);
		    }
		});
	}

	public void removeNote(final Note note) {
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
		        try {
		        	st = con.prepare("DELETE FROM Notes WHERE id = ?");
		        	st.bind(1, note.getID());
		        	st.step();
		        } catch(SQLiteException ex) {ex.printStackTrace();}
		        finally {if(st != null) st.dispose();}
		        return null;
		    }
		}).complete();		
	}

	public Note getNote(final long id) {
		return queue.execute(new SQLiteJob<Note>() {
		    protected Note job(SQLiteConnection con) {
		    	Note result = null;
		        SQLiteStatement st = null;
		        try {
		        	st = con.prepare("SELECT * FROM Notes WHERE id = ?");
		        	st.bind(1, id);
		        	st.step();
		        	result = new Note(st.columnString(1), st.columnString(2), st.columnInt(0), st.columnString(3)); //columnLong(0)?
		        } catch(SQLiteException ex) {ex.printStackTrace();}
		        finally {if(st != null) st.dispose();}
		        return result;
		    }
		}).complete();
	}

	public int getCount() {
		return queue.execute(new SQLiteJob<Integer>() {
		    protected Integer job(SQLiteConnection con) {
		    	int result = -1;
		        SQLiteStatement st = null;
		        try {
		        	st = con.prepare("SELECT Count(*) FROM Notes");
		        	st.step();
	        		result = st.columnInt(0);
		        } catch(SQLiteException ex) {ex.printStackTrace();}
		        finally {if(st != null) st.dispose();}
		        return result;
		    }
		}).complete();
	}
}