import java.io.Serializable;
import java.util.ArrayList;

public class List implements Serializable {
	public String name;
	public int id;
	public ArrayList<Note> notes = new ArrayList<Note>();

	public List(String name) {
		this.name = name;
	}
}