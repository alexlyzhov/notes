import java.sql.*;

public class Database {
	protected Connection con;  //in further development I should make the work with connection safe by creating destructors
	private Statement st;

	protected void connect(String fileName) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch(ClassNotFoundException ex) {
			System.out.println("No driver class found");
		}
		try { //need to decide whether I will implement multiplatform support
			con = DriverManager.getConnection("jdbc:sqlite:" + fileName);
			st = con.createStatement();
			lock();
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void lock() {
		executeUpdate("PRAGMA locking_mode = EXCLUSIVE; BEGIN EXCLUSIVE; COMMIT");
	}

	protected void executeUpdate(String update) {
		try {
			st.executeUpdate(update);
		} catch(SQLException ex) {ex.printStackTrace();}
	}

	protected ResultSet executeQuery(String query) {
		ResultSet result = null;
		try {
			result = st.executeQuery(query);
		} catch(SQLException ex) {ex.printStackTrace();}
		return result;
	}

	public void close() {
		try {
			if(st != null) st.close();
		} catch(SQLException ex) {System.out.println("Closing databases failed");}
		try {
			if(con != null) con.close();
		} catch(SQLException ex) {System.out.println("Closing databases failed");}
	}
}