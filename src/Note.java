import java.io.Serializable;

public class Note implements Serializable {
	private String name = "";
	public String content = "";
	private long time;
	public boolean trash = false;
	public int quick = 0;
	public int id = -1;
	public transient boolean editing;

	public Note() {
		updateTime();
	}
	
	public void setName(String newName) {
		this.name = newName;
	}

	public String getPureName() {
		return name;
	}

	public String getViewableName() {
		return (name.equals("")) ? "Nameless" : name;
	}

	public String getMarkdownName() {
		return editing ? ("<b>" + getViewableName() + "</b>") : getViewableName();
	}

	public void updateTime() {
		time = System.currentTimeMillis();
	}

	public long getTime() {
		return time;
	}

	public boolean empty() {
		if(getPureName().equals("") && content.equals("")) return true;
		return false;
	}
}