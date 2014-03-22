import com.almworks.sqlite4java.*;
import java.util.ArrayList;
import java.io.*;

public class Base {
	private SQLiteQueue queue;

	public Base() {
		java.util.logging.Logger.getLogger("com.almworks.sqlite4java").setLevel(java.util.logging.Level.OFF);
		initQueue();
		createTable();
	}

	private void initQueue() {
		String absPath = "";
		// returns the absolute path of class files (including jar directory)
		// try {
		// 	absPath = java.net.URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
		// } catch(Exception ex) {ex.printStackTrace();}
		String filePath = "notes.db";
		String tmpFilePath = Args.getInstance().getNamedArgument("db");
		if(tmpFilePath != null) filePath = tmpFilePath;
		if(queue == null) {
			queue = new SQLiteQueue(new java.io.File(absPath + filePath));
		}
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
		        	st = con.prepare("CREATE TABLE IF NOT EXISTS Notes (id INTEGER PRIMARY KEY, note BLOB)");
		        	st.step();
		        } catch(SQLiteException ex) {ex.printStackTrace();}
		        finally {if(st != null) st.dispose();}
		        return null;
		    }
		}).complete();
	}

	public void newNote(final Note note) {
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
	    		try {
	    			st = con.prepare("INSERT INTO Notes (note) VALUES (?)");
	    			st.bind(1, getBytesFromNote(note));
	    			st.step();
	    			note.initiate((int) con.getLastInsertId());
	    		} catch(SQLiteException ex) {ex.printStackTrace();}
	    		finally {if(st != null) st.dispose();}
		        return null;
		    }
		});
	}

	public void updateNote(final Note note, final NotesList notesList) {
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
	    		try {
	    			st = con.prepare("UPDATE Notes SET note = ? WHERE id = ?");
	    			st.bind(1, getBytesFromNote(note)); st.bind(2, note.getID());
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
		}).complete();		
	}

	public ArrayList<Note> getNotes() {
		return queue.execute(new SQLiteJob<ArrayList<Note>>() {
			protected ArrayList<Note> job(SQLiteConnection con) {
				ArrayList<Note> result = new ArrayList<Note>();
			    SQLiteStatement st = null;
			    try {
			    	st = con.prepare("SELECT * FROM Notes");
			    	while(true) {
			    		st.step();
			    		if(st.hasRow()) {
			    			byte[] noteBytes = st.columnBlob(1);
			    			Note note = getNoteFromBytes(noteBytes);
				    		result.add(note);
			    		} else break;
			    	}
			    } catch(SQLiteException ex) {ex.printStackTrace();}
			    finally {if(st != null) st.dispose();}
			    return result;
			}
		}).complete();
	}

	public byte[] getBytesFromNote(Note note) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(note);
		    return out.toByteArray();
		} catch(IOException ex) {ex.printStackTrace();}
		return null;
	}

	public Note getNoteFromBytes(byte[] noteBytes) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(noteBytes);
		    ObjectInputStream is = new ObjectInputStream(in);
		    Note note = (Note) is.readObject();
		    return note;
		} catch(IOException | ClassNotFoundException ex) {ex.printStackTrace();}
		return null;
	}
}