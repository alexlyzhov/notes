import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.EventKey;

public class Editor extends Window {
	private Note note;
	private NotesList list;
	private TagsList tagsList;
	private Pixbuf edit;
	private String initialName, initialContent, initialTags;

	private VBox vbox;
	private Entry nameEntry, tagsEntry;
	private TextBuffer buffer;
	private TextView text;
	private ScrolledWindow scroll;

	public Editor(Note noteParam, NotesList listParam, TagsList tagsListParam) {
		list = listParam;
		tagsList = tagsListParam;
		note = noteParam;

		saveInitialValues();
		note.startEditing();
		list.setData(note);

		try {
			edit = new Pixbuf("ico/edit.png");
		} catch(Exception ex) {ex.printStackTrace();}
		setTitle();
		setIcon(edit);
		setDefaultSize(getScreen().getWidth() / 2, getScreen().getHeight() / 2);
		setPosition(WindowPosition.CENTER);

		nameEntry = new Entry(note.getName());
		nameEntry.connect(new Entry.Changed() {
			public void onChanged(Entry entry) {
				note.setName(entry.getText());
				list.setData(note);
				if(changed()) list.updateNote(note);
				setTitle();
			}
		});
		nameEntry.connect(new Widget.KeyPressEvent() {
			public boolean onKeyPressEvent(Widget source, EventKey event) {
				if(event.getKeyval() == Keyval.Return) {
					text.grabFocus();
					return true;
				}
				return false;
			}
		});

		tagsEntry = new Entry(note.getTags());

		buffer = new TextBuffer();
		buffer.setText(note.getContent());
		buffer.connect(new TextBuffer.Changed() {
			public void onChanged(TextBuffer buffer) {
				note.setContent(buffer.getText());
		    	list.setData(note);
				if(changed()) list.updateNote(note);
			}
		});
		text = new TextView(buffer);
		text.setWrapMode(WrapMode.WORD);
		scroll = new ScrolledWindow();
		scroll.setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
		scroll.add(text);

		vbox = new VBox(false, 0);
		vbox.packStart(nameEntry, false, false, 0);
		showTagsEntry();
		vbox.packEnd(scroll, true, true, 0);
		add(vbox);

		if(!nameEntry.getText().equals("")) text.grabFocus();

		connect(new Window.DeleteEvent() {
		    public boolean onDeleteEvent(Widget source, Event event) {
		    	note.finishEditing();
		    	list.setData(note);
		    	note.setTags(tagsEntry.getText());
		    	if(tagsChanged()) {
		    		list.updateNote(note);
		    		list.updateTags();
		    	}
		        return false;
		    }
		});

		showAll();
	}

	private void showTagsEntry() {
		vbox.packStart(tagsEntry, false, false, 0);
	}

	private void hideTagsEntry() {
		vbox.remove(tagsEntry);
	}

	private void setTitle() {
		String name = note.getName();
		if(name.equals("")) name = "Nameless";
		super.setTitle(name);
	}

	private void saveInitialValues() {
		initialName = note.getName();
		initialContent = note.getContent();
		initialTags = note.getTags();
	}

	private boolean changed() {
		if((note.getName().equals(initialName)) && (note.getContent().equals(initialContent))) return false;
		return true;
	}

	private boolean tagsChanged() {
		if(note.getTags().equals(initialTags)) return false;
		return true;
	}
}