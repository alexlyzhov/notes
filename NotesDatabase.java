import java.sql.*;
import java.util.ArrayList;

public class NotesDatabase extends Database {

	public NotesDatabase() {
		connect("notes.db");
		initTable();
	}

	private void initTable() {
		executeUpdate("CREATE TABLE IF NOT EXISTS Notes (id INTEGER PRIMARY KEY, name VARCHAR, content TEXT, time TEXT)");
	}

	public void newNote(Note note) {
		try {
			PreparedStatement prst = con.prepareStatement("INSERT INTO Notes (name, content, time) VALUES (?, ?, datetime('now'))");
			// executeUpdate("INSERT INTO Notes (name, content, time) VALUES ('" + note.name + "', '" + note.content + "', datetime('now'))");
			prst.setString(1, note.name); prst.setString(2, note.content);
			prst.executeUpdate();
			ResultSet result = executeQuery("SELECT last_insert_rowid() as id FROM Notes");
			result.next();
			note.setID(result.getInt("id"));
		} catch(SQLException ex) {ex.printStackTrace();}
	}

	public void updateNote(Note note) {
		try {
			PreparedStatement prst = con.prepareStatement("UPDATE Notes SET name = ?, content = ?, time = datetime('now') WHERE id = ?");
			prst.setString(1, note.name); prst.setString(2, note.content); prst.setInt(3, note.getID());
			prst.executeUpdate();
		} catch(SQLException ex) {ex.printStackTrace();}
		// executeUpdate("UPDATE Notes SET name = '" + note.name + "', content = '" + note.content + "', time = datetime('now') WHERE id = " + note.getID());
	}

	public void removeNote(Note note) {
		executeUpdate("DELETE FROM Notes WHERE id = " + note.getID());
	}

	public Note[] getNotesList() {
		ArrayList<Note> notes = new ArrayList<Note>();
		ResultSet notesSet = null;
		notesSet = executeQuery("SELECT * FROM Notes ORDER BY time DESC");
		try {
			while(notesSet.next()) {
				notes.add(readNote(notesSet));
			}
		} catch(SQLException ex) {ex.printStackTrace();}
		return notes.toArray(new Note[notes.size()]);
	}

	private Note readNote(ResultSet set) throws SQLException { //serialization/deserialization of Note objects - implement
		Note note = new Note(set.getInt("id"), set.getString("name"), set.getString("content"));
		return note;
	}
}