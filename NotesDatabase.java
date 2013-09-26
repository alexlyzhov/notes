import com.almworks.sqlite4java.*;
import java.util.ArrayList;
import java.io.File;

public class NotesDatabase {
	private SQLiteQueue queue;
	// private SQLiteConnection con;

	public NotesDatabase() {
		initQueue();
		createTable();
	}

	private void initQueue() {
		queue = new SQLiteQueue(new File("notes.db"));
		queue.start();
	}

	// private void open() {
	// 	File file = new File("notes.db");
	// 	con = new SQLiteConnection(file);
	// 	try {
	// 		con.open();
	// 	} catch(SQLiteException ex) {ex.printStackTrace();}
	// }

	private void createTable() {
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
		        try {
		        	st = con.prepare("CREATE TABLE IF NOT EXISTS Notes (id INTEGER PRIMARY KEY, name VARCHAR, content TEXT, time TEXT)");
		        	st.step(); //stepThrough();
		        } catch(SQLiteException ex) {ex.printStackTrace();}
		        finally {if(st != null) st.dispose();}
		        //varchar/text difference, primary key usage
		        return null;
		    }
		});
	}

	public void newNote(final Note note) {
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
	    		try {
	    			st = con.prepare("INSERT INTO Notes (name, content, time) VALUES (?, ?, datetime('now'))");
	    			st.bind(1, note.name); st.bind(2, note.content);
	    			st.step(); //stepThrough();
	    			note.setID((int) con.getLastInsertId());
	    		} catch(SQLiteException ex) {ex.printStackTrace();}
	    		finally {if(st != null) st.dispose();}
		        return null;
		    }
		});		
	}

	public void updateNote(final Note note) {
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
	    		try {
	    			st = con.prepare("UPDATE Notes SET name = ?, content = ?, time = datetime('now') WHERE id = ?");
	    			st.bind(1, note.name); st.bind(2, note.content); st.bind(3, note.getID());
	    			st.step();
	    		} catch(SQLiteException ex) {ex.printStackTrace();}
	    		finally {if(st != null) st.dispose();}
		        return null;
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
		});		
	}

	public Note[] getNotesList() {
		final ArrayList<Note> notes = new ArrayList<Note>();
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
		        try {
		        	st = con.prepare("SELECT * FROM Notes ORDER BY time DESC");
		        	while(st.step()) {
		        		Note note = new Note();
		        		note.setID(st.columnInt(0)); //fix this private and public fields scructure
		        		note.name = st.columnString(1);
		        		note.content = st.columnString(2);
		        		notes.add(note);
		        	}
		        } catch(SQLiteException ex) {ex.printStackTrace();}
		        finally {if(st != null) st.dispose();}
		        return null;
		    }
		}).complete();
        return notes.toArray(new Note[notes.size()]);
	}

	public void closeQueue() {
		if(queue != null) {
			try {
				queue.stop(true).join();
			} catch(InterruptedException ex) {ex.printStackTrace();}
		}
	}
}