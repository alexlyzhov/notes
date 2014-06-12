import java.io.Serializable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "notes")
public class Note implements Serializable {
	@DatabaseField(generatedId = true)
	public int id = -1;
	@DatabaseField(canBeNull = false)
	private String name = "";
	@DatabaseField(canBeNull = false)
	public String content = "";
	@DatabaseField
	private long time;
	@DatabaseField(canBeNull = false)
	public boolean trash = false;
	@DatabaseField(canBeNull = false)
	public int quick = 0;
	public transient boolean editing;

	public Note(long time) {
		this.time = time;
	}

	public Note() {}
	
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