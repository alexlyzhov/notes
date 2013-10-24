public class Note {
	private String name, content, tags;
	private long id; private String time;

	private boolean usable;
	private boolean editing;

	public Note(String name, String content, String tags, long id, String time) {
		this(name, content, tags);
		initiate(id, time);
	}

	public Note(String name, String content, String tags) {
		this.name = name;
		this.content = content;
		this.tags = tags;
	}

	public Note(String tags) {
		this("", "", tags);
	}

	public Note() {
		this("", "", "");
	}

	public void initiate(long id, String time) {
		this.id = id;
		this.time = time;
		usable = true;
	}

	public void updateTime(String newTime) {
		if(usable) {
			this.time = newTime;
		} else System.out.println("Error: time was not updated");
	}

	public String getName() {
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

	public long getID() {
		if(usable) {
			return id;
		} else {
			System.out.println("ID was not set");
			return -1;
		}
	}

	public String getTime() {
		if(usable) {
			return time;
		} else {
			System.out.println("Time was not set");
			return "";
		}
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
}