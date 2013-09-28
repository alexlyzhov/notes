import java.util.AbstractList;

public class Note {
	private int id;
	private boolean idSet;
	public String name;
	public String content;
	public boolean editing;
	public boolean fresh; //make some use

	public Note(int id, String name, String content) {
		this(name, content);
		setID(id);
	}

	public Note(String name, String content) {
		this.name = name;
		this.content = content;
	}

	public Note() {}

	public void update(Note another) {
		this.name = another.name;
		this.content = another.content;
	}

	public String toString() {
		if((name == null) || (name.equals(""))) return "Nameless";
		else return name;
	}

	public int getID() {
		if(idSet = true) {
			return id;
		} else {
			System.out.println("ID was not set");
			return -1;
		}
	}

	public void setID(int id) {
		this.id = id;
		idSet = true;
	}

	public int findIndex(AbstractList<Note> data) {
		for(Note note: data) {
			if(note.getID() == getID()) return data.indexOf(note);
		}
		return -1;
	}
}