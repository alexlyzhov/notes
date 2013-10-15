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

		try {
			edit = new Pixbuf("ico/edit.png");
		} catch(Exception ex) {ex.printStackTrace();}
		setTitle();
		setIcon(edit);

		nameEntry = new Entry(note.getTrueName());
		nameEntry.connect(new Entry.Changed() {
			public void onChanged(Entry entry) {
				note.setName(entry.getText());
				list.setName(row);
				setTitle();
			}
		});

		buffer = new TextBuffer(); //focus on text if name is not empty
		buffer.setText(note.getContent());
		buffer.connect(new TextBuffer.Changed() {
			public void onChanged(TextBuffer buffer) {
				note.setContent(buffer.getText()); //it may be optimized to one-time write, though it does not consume too much memory
			}
		});
		text = new TextView(buffer);
		// text.setWrapMode(WrapMode.WORD);
		scroll = new ScrolledWindow();
		scroll.setPolicy(PolicyType.NEVER, PolicyType.ALWAYS);
		scroll.add(text);

		vbox = new VBox(false, 0);
		vbox.packStart(nameEntry, false, false, 0);
		vbox.packStart(scroll, true, true, 0);
		add(vbox);
		setDefaultSize(350, 350);

		connect(new Window.DeleteEvent() {
		    public boolean onDeleteEvent(Widget source, Event event) {
		    	note.finishEditing();
				if(changed()) list.updateNote(row);
		        return false;
		    }
		});

		showAll();
	}

	private void setTitle() {
		super.setTitle(note.getOutputName());
	}

	private void saveInitialValues() {
		initialName = note.getTrueName();
		initialContent = note.getContent();
	}

	private boolean changed() {
		if((note.getTrueName().equals(initialName)) && (note.getContent().equals(initialContent))) return false;
		return true;
	}
}