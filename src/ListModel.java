import org.gnome.gtk.*;

public class ListModel extends ListStore {
	DataColumn[] columns;

	public ListModel(DataColumn[] columns) {
		super(columns);
		this.columns = columns;
	}

	public DataColumnString getNameColumn() {
		return (DataColumnString) columns[0];
	}
}