import org.gnome.gtk.*;
import org.gnome.gdk.EventButton;
import java.util.ArrayList;

public class TagsList {
	private Base base;
	private Notes window;
	public static final DataColumnString nameColumn = new DataColumnString();
	private final ListStore model = new ListStore(new DataColumn[] {nameColumn});
	public TreeView tree;

	public TagsList(Base base, final Notes window) {
		this.base = base;
		this.window = window;
		tree = new TreeView(model);

		init();

		TreeViewColumn vertical = tree.appendColumn();
		vertical.setTitle("Tag");
		CellRendererText text = new CellRendererText(vertical);
		text.setText(nameColumn);
		tree.setHeadersVisible(false);
		tree.connect(new Widget.ButtonPressEvent() { //grabbing events
			public boolean onButtonPressEvent(Widget source, EventButton event) {
				TreePath path = tree.getPathAtPos((int) event.getX(), (int) event.getY());
				if(path != null) {
					TreeIter row = model.getIter(path);
					if(path.getIndices()[0] == 0) {
						window.updateList(null);
					} else {
						window.updateList(model.getValue(row, nameColumn));
					}
				}
				return false;
			}
		});
	}

	public void selectFirst() {
		tree.getSelection().selectRow(model.getIterFirst());
	}

	public void init() {
		model.clear();
		TreeIter all = model.appendRow();
		model.setValue(all, nameColumn, "All notes");
	}

	public void updateTags(Note note) {
		String[] newTags = note.getTags().split(",");
		boolean fl;
		for(String newTag: newTags) {
			if(!newTag.equals("")) {
				fl = false;
				TreeIter row = model.getIterFirst();
				do {
					if(newTag.equals(model.getValue(row, nameColumn))) fl = true;
				} while(fl == false && row.iterNext());
				if(!fl) {
					addTag(newTag);
				}
			}
		}
	}

	private void addTag(String newTag) {
		TreeIter row = model.appendRow();
		model.setValue(row, nameColumn, newTag);
	}

	public String getTag() {
		if(tree.getSelection().getSelectedRows()[0].getIndices()[0] != 0) {
			TreeIter row = tree.getSelection().getSelected();
			return model.getValue(row, nameColumn);
		} else return null;
	}

	public TreeView getTreeView() {
		return tree;
	}
}