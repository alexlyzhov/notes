import java.io.Serializable;

public class Group implements Serializable {
	private String name = "";
	private int id;

	public Group(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}
}