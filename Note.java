public class Note {
	private String name;
	private String content;
	private long id;
	private String time;

	private boolean usable;
	private boolean editing;

	public Note(String name, String content, long id, String time) {
		this(name, content);
		this.id = id;
		this.time = time;
		usable = true;
	}

	public Note(String name, String content) {
		this.name = name;
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public String getContent() {
		return content;
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