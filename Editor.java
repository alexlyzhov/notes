import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;

public class Editor extends Window {
	private Note note;
	private NotesList list;
	private TreeIter row;
	private Pixbuf edit;
	private String initialName, initialContent;

	private VBox vbox;
	private Entry nameEntry;
	private TextBuffer buffer;
	private TextView text;
	private ScrolledWindow scroll;

	public Editor(NotesList listParam, TreeIter rowParam) {
		list = listParam;
		row = rowParam;
		note = list.getNote(row);

		saveInitialValues();
		note.startEditing();
		list.setData(row);

		try {
			edit = new Pixbuf("ico/edit.png");
		} catch(Exception ex) {ex.printStackTrace();}
		setTitle();
		setIcon(edit);
		setDefaultSize(350, 350);

		nameEntry = new Entry(note.getName());
		nameEntry.connect(new Entry.Changed() {
			public void onChanged(Entry entry) {
				note.setName(entry.getText());
				list.setName(row);
				setTitle();
			}
		});

		buffer = new TextBuffer();
		buffer.setText(note.getContent());
		buffer.connect(new TextBuffer.Changed() {
			public void onChanged(TextBuffer buffer) {
				note.setContent(buffer.getText());
			}
		});
		text = new TextView(buffer);
		text.setWrapMode(WrapMode.WORD);
		scroll = new ScrolledWindow();
		scroll.setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
		scroll.add(text);

		vbox = new VBox(false, 0);
		vbox.packStart(nameEntry, false, false, 0);
		vbox.packStart(scroll, true, true, 0);
		add(vbox);

		if(!nameEntry.getText().equals("")) text.grabFocus();

		connect(new Window.DeleteEvent() {
		    public boolean onDeleteEvent(Widget source, Event event) {
		    	note.finishEditing();
		    	list.setData(row);
				if(changed()) list.updateNote(row);
		        return false;
		    }
		});

		showAll();
	}

	private void setTitle() {
		String name = note.getName();
		if(name.equals("")) name = "Nameless";
		super.setTitle(name);
	}

	private void saveInitialValues() {
		initialName = note.getName();
		initialContent = note.getContent();
	}

	private boolean changed() {
		if((note.getName().equals(initialName)) && (note.getContent().equals(initialContent))) return false;
		return true;
	}
}