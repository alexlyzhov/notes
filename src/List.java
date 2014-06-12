import java.io.Serializable;
import java.util.ArrayList;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DataType;

@DatabaseTable(tableName = "lists")
public class List implements Serializable {
	@DatabaseField(canBeNull = false)
	public String name;
	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField(canBeNull = false, dataType = DataType.SERIALIZABLE)
	public ArrayList<Note> notes = new ArrayList<Note>();

	public List(String name) {
		this.name = name;
	}

	public List() {}
}