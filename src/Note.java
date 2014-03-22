import java.io.Serializable;

public class Note implements Serializable { //note should be connected with a Base instance and update itself in base when needed
	private String name = "";
	private String content = "";
	private String tags = "";
	private long time;
	private boolean trash;
	private int quick;

	private int id;
	private boolean usable;
	private transient boolean editing;

	public Note(String tags) {
		if(tags != null) setTags(tags);
		updateTime();
	}

	public void setId(int id) {
		this.id = id;
		usable = true;
	}

	public boolean isUsable() {
		return usable;
	}

	public void updateTime() {
		time = System.currentTimeMillis();
	}

	public String getName() {
		return name;
	}

	public String getFilledName() {
		String name = getName();
		if(name.equals("")) {
			name = "Nameless";
		}
		return name;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String newContent) {
		this.content = newContent;
	}

	public boolean empty() {
		if(getName().equals("") && getContent().equals("")) return true;
		return false;
	}

	public int getID() {
		if(usable) {
			return id;
		} else {
			System.out.println("ID was not set. Name = " + name + ", content = " + content + ", tags = " + tags);
			return -1;
		}
	}

	public long getTime() {
		return time;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	} 

	public void startEditing() {
		editing = true;
	}

	public void finishEditing() {
		editing = false;
	}

	public boolean isEditing() {
		return editing;
	}

	public void removeToTrash() {
		trash = true;
	}

	public void recoverFromTrash() {
		trash = false;
	}

	public boolean inTrash() {
		return trash;
	}

	public int getQuick() { //remove getters and setters
		return quick;
	}

	public void setQuick(int quick) {
		this.quick = quick;
	}
}