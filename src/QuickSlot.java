import java.io.Serializable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "quicks")
public class QuickSlot {
	@DatabaseField(id = true)
	public int slotNumber;
	@DatabaseField
	public int noteID;

	public QuickSlot(int slotNumber, int noteID) {
		this.slotNumber = slotNumber;
		this.noteID = noteID;
	}

	public QuickSlot() {}
}