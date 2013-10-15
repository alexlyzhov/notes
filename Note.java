public class Note {
	private String name;
	private String content;
	private long id;
	private String time;

	private boolean usable;
	private boolean editing;

	public Note(String name, String content, long id, String time) {
		this(name, content);
		initiate(id, time);
	}

	public Note(String name, String content) {
		this.name = name;
		this.content = content;
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

	public String getTrueName() {
		return name;
	}

	public String getOutputName() {
		if(name.equals("")) return "Nameless";
		else return name;
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