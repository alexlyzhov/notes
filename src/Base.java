import com.almworks.sqlite4java.*;
import java.util.ArrayList;

public class Base {
	private SQLiteQueue queue;

	public Base() {
		java.util.logging.Logger.getLogger("com.almworks.sqlite4java").setLevel(java.util.logging.Level.OFF);
		initQueue();
		createTable();
	}

	private void initQueue() {
		String absPath = "";
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
		        	st = con.prepare("CREATE TABLE IF NOT EXISTS Notes (id INTEGER PRIMARY KEY, name VARCHAR, content TEXT, time TEXT, tags TEXT)");
		        	st.step();
		        } catch(SQLiteException ex) {ex.printStackTrace();}
		        finally {if(st != null) st.dispose();}
		        return null;
		    }
		}).complete();
	}

	public void newNote(final Note note, final NotesList notesList) {
		queue.execute(new SQLiteJob<Object>() {
			protected Object job(SQLiteConnection con) {
				SQLiteStatement st = null;
	    		try {
	    			st = con.prepare("SELECT datetime('now')");
	    			st.step();
	    			note.setTime(st.columnString(0));
	    		} catch(SQLiteException ex) {ex.printStackTrace();}
	    		finally {if(st != null) st.dispose();}
		        return null;
			}
		}).complete();
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
	    		try {
	    			st = con.prepare("INSERT INTO Notes (name, content, time, tags) VALUES (?, ?, ?, ?)");
	    			st.bind(1, note.getName()); st.bind(2, note.getContent()); st.bind(3, note.getTime()); st.bind(4, note.getTags());
	    			st.step();
	    			note.initiate(con.getLastInsertId());
	    		} catch(SQLiteException ex) {ex.printStackTrace();}
	    		finally {if(st != null) st.dispose();}
		        return null;
		    }
		    protected void jobFinished(Object nullObject) {
		    	updateNote(note, notesList);
		    }
		});
	}

	public void updateNote(final Note note, final NotesList notesList) {
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
	    		try {
	    			st = con.prepare("SELECT datetime('now')");
	    			st.step();
	    			note.updateTime(st.columnString(0));
	    			st = con.prepare("UPDATE Notes SET name = ?, content = ?, time = ? WHERE id = ?");
	    			st.bind(1, note.getName()); st.bind(2, note.getContent()); st.bind(3, note.getTime()); st.bind(4, note.getID());
	    			st.step();
	    		} catch(SQLiteException ex) {ex.printStackTrace();}
	    		finally {if(st != null) st.dispose();}
		        return null;
		    }
		    protected void jobFinished(Object nullObject) {
		    	notesList.updateView(note);
		    }
		});
	}

	public void updateNoteTags(final Note note) {
		queue.execute(new SQLiteJob<Object>() {
		    protected Object job(SQLiteConnection con) {
		        SQLiteStatement st = null;
	    		try {
	    			st = con.prepare("UPDATE Notes SET tags = ? WHERE id = ?");
	    			st.bind(1, note.getTags()); st.bind(2, note.getID());
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
				    		result.add(new Note(st.columnString(1), st.columnString(2), st.columnString(4), st.columnInt(0), st.columnString(3)));
			    		} else break;
			    	}
			    } catch(SQLiteException ex) {ex.printStackTrace();}
			    finally {if(st != null) st.dispose();}
			    return result;
			}
		}).complete();
	}
}