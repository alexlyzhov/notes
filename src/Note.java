import java.io.Serializable;

public class Note implements Serializable {
	private String name = "";
	private String content = "";
	private String tags = "";
	private long time = 0;
	private int id;

	private boolean usable; //remove?
	private transient boolean editing;

	public Note(String tags) {
		if(tags != null) setTags(tags);
		updateTime();
	}

	public void initiate(int id) {
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
			System.out.println("ID was not set");
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
		String tags = getTags();
		if(tags.equals("")) tags = "Trash";
		else tags = tags + ",Trash";
		setTags(tags);
	}

	public boolean removedToTrash() {
		for(String tag: getTags().split(",")) {
			if(tag.equals("Trash")) {
				return true;
			}
		}
		return false;
	}
}