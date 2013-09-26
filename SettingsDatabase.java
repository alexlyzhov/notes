import java.sql.*;
import java.awt.Rectangle;

public class SettingsDatabase extends Database {

	public SettingsDatabase() {
		connect("settings.db");
		initTable();
	}

	private void initTable() {
		executeUpdate("CREATE TABLE IF NOT EXISTS Settings (name VARCHAR PRIMARY KEY, value VARCHAR)");
	}

	public void writeBounds(Rectangle rect) {
		String finalString = (int) rect.getX() + " " + (int) rect.getY() + " " + (int) rect.getWidth() + " " + (int) rect.getHeight();
		set("Bounds", finalString);
	}

	public Rectangle readBounds() {
		String string = get("Bounds");
		if(string == null) return null;
		String[] paramStrings = string.split(" ");
		int[] params = new int[paramStrings.length];
		for(int i = 0; i < paramStrings.length; i++) params[i] = Integer.parseInt(paramStrings[i]);
		Rectangle result = new Rectangle(params[0], params[1], params[2], params[3]);
		return result;
	}

	public void set(String name, String value) {
		executeUpdate("INSERT OR REPLACE INTO Settings (name, value) VALUES (\"" + name + "\", \"" + value + "\")");
	}

	public String get(String name) {
		ResultSet result = executeQuery("SELECT value FROM Settings WHERE name = \"" + name + "\"");
		try {
			if(result.next()) {
				String stringResult = result.getString("value");
				return stringResult;
			}
		}
		catch(SQLException ex) {ex.printStackTrace();}
		catch(NullPointerException ex) {}
		return null;
	}
}