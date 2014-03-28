import com.almworks.sqlite4java.*;
import java.util.ArrayList;
import java.io.*;

public class Base {
	private SQLiteQueue queue;

	public Base() {
		java.util.logging.Logger.getLogger("com.almworks.sqlite4java").setLevel(java.util.logging.Level.OFF);
		initQueue();
		createTables();
	}

	public void quit() {
		closeQueue();
	}

	private void initQueue() {
		String absPath = "";
		// returns the absolute path of class files (including jar directory)
		// try {
		// 	absPath = java.net.URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
		// } catch(Exception ex) {ex.printStackTrace();}
		String filePath = "notes.db";
		// String tmpFilePath = Args.getInstance().getNamedArgument("db");
		// if(tmpFilePath != null) filePath = tmpFilePath;
		if(queue == null) {
			queue = new SQLiteQueue(new java.io.File(absPath + filePath));
		}
		queue.start();
	}

	private void closeQueue() {
		if(queue != null) {
			try {	
				queue.stop(true).join();
				//try queue.stop(true);
			} catch(InterruptedException ex) {ex.printStackTrace();}
		}
	}

	private void createTables() {
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
		        try {
		        	// SQLParts parts = new SQLParts("CREATE TABLE IF NOT EXISTS Notes (id INTEGER PRIMARY KEY AUTOINCREMENT, note BLOB)");
		        	// parts.append("CREATE TABLE IF NOT EXISTS Lists (id INTEGER PRIMARY KEY AUTOINCREMENT, list BLOB)");
		        	// st = con.prepare(parts);
		        	// st.step();
		        	st = con.prepare("CREATE TABLE IF NOT EXISTS Notes (id INTEGER PRIMARY KEY AUTOINCREMENT, note BLOB)");
		        	st.step();
		        	st = con.prepare("CREATE TABLE IF NOT EXISTS Lists (id INTEGER PRIMARY KEY AUTOINCREMENT, list BLOB)");
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
	    			note.id = (int) con.getLastInsertId();
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
	    			st = con.prepare("UPDATE Notes SET note = ? WHERE id = ?");
	    			st.bind(1, getBytesFromNote(note)); st.bind(2, note.id);
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
		        	st.bind(1, note.id);
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

	public void newList(final List list) {
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
	    		try {
	    			st = con.prepare("INSERT INTO Lists (list) VALUES (?)");
	    			st.bind(1, getBytesFromList(list));
	    			st.step();
	    			list.id = (int) con.getLastInsertId();
	    		} catch(SQLiteException ex) {ex.printStackTrace();}
	    		finally {if(st != null) st.dispose();}
		        return null;
		    }
		});
	}

	public void updateList(final List list) {
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
	    		try {
	    			st = con.prepare("UPDATE Lists SET list = ? WHERE id = ?");
	    			st.bind(1, getBytesFromList(list)); st.bind(2, list.id);
	    			st.step();
	    		} catch(SQLiteException ex) {ex.printStackTrace();}
	    		finally {if(st != null) st.dispose();}
		        return null;
		    }
		});
	}

	public void removeList(final List list) {
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
		        try {
		        	st = con.prepare("DELETE FROM Lists WHERE id = ?");
		        	st.bind(1, list.id);
		        	st.step();
		        } catch(SQLiteException ex) {ex.printStackTrace();}
		        finally {if(st != null) st.dispose();}
		        return null;
		    }
		}).complete();		
	}

	public ArrayList<List> getLists() {
		return queue.execute(new SQLiteJob<ArrayList<List>>() {
			protected ArrayList<List> job(SQLiteConnection con) {
				ArrayList<List> result = new ArrayList<List>();
			    SQLiteStatement st = null;
			    try {
			    	st = con.prepare("SELECT * FROM Lists");
			    	while(true) {
			    		st.step();
			    		if(st.hasRow()) {
			    			byte[] groupBytes = st.columnBlob(1);
			    			List list = getListFromBytes(groupBytes);
				    		result.add(list);
			    		} else break;
			    	}
			    } catch(SQLiteException ex) {ex.printStackTrace();}
			    finally {if(st != null) st.dispose();}
			    return result;
			}
		}).complete();
	}

	public byte[] getBytesFromList(List list) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(list);
		    return out.toByteArray();
		} catch(IOException ex) {ex.printStackTrace();}
		return null;
	}

	public List getListFromBytes(byte[] groupBytes) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(groupBytes);
		    ObjectInputStream is = new ObjectInputStream(in);
		    List list = (List) is.readObject();
		    return list;
		} catch(IOException | ClassNotFoundException ex) {ex.printStackTrace();}
		return null;
	}
}