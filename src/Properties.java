import org.gnome.gtk.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import org.gnome.gdk.EventKey;
import org.gnome.gdk.Keyval;

public class Properties extends Dialog {
	private Note note;
	private Notes notes;
	private Entry tagsEntry;
	private Button removeButton;

	public Properties(Note note) {
		this.note = note;
		notes = Notes.getInstance();
		notes.startEditing(note);

		setNameTitle();
		setPropertiesIcon();

		destroyOnDelete();
		saveOnDelete();

		add(new Label("Tags separated by comma: "));
		tagsEntry = new Entry(tagsOutput(note.getTags()));
		tagsEntry.connect(new Widget.KeyPressEvent() {
			public boolean onKeyPressEvent(Widget source, EventKey event) {
				if(event.getKeyval() == Keyval.Return) {
					saveAndUpdate();
					destroy();
					return true;
				}
				return false;
			}
		});
		removeButton = new Button("Delete note");
		removeButton.connect(new Button.Clicked() {
			public void onClicked(Button source) {
				removeNote();
				notes.updateInfo();
				destroy();
			}
		});
		add(tagsEntry);
		add(removeButton);

		addButton("Cancel", ResponseType.CANCEL);
		addButton("OK", ResponseType.OK);
		setDefaultResponse(ResponseType.OK);
		connect(new Dialog.Response() {
			public void onResponse(Dialog dialog, ResponseType responseType) {
				if(responseType == ResponseType.CANCEL) {
					destroy();
				} else if(responseType == ResponseType.OK) {
					if(tagsChanged()) {
						saveAndUpdate();
					}
					destroy();
				}
			}
		});

		showAll();
		present();
	}

	private boolean tagsChanged() {
		return !note.getTags().equals(tagsOutput(tagsEntry.getText()));
	}

	private void destroyOnDelete() {
		connect(new Window.DeleteEvent() {
			public boolean onDeleteEvent(Widget source, Event event) {
				source.destroy();
				return false;
			}
		});
	}

	private void saveOnDelete() {
		connect(new Widget.Destroy() {
		    public void onDestroy(Widget source) {
		    	notes.finishEditing(note);
		    }
		});
	}

	public void removeOnDelete(final ArrayList<Window> children) {
		connect(new Widget.Destroy() {
		    public void onDestroy(Widget source) {
		    	children.remove(this);
		    }
		});
	}

	private void setNameTitle() {
		String name = note.getName();
		if(name.equals("")) name = "Nameless";
		setTitle(name);
	}

	private void setPropertiesIcon() {
		try {
			Pixbuf properties = new Pixbuf("ico/properties.png");
			setIcon(properties);
		} catch(Exception ex) {ex.printStackTrace();}
	}

	private void save() {
		saveTags();
	}

	private void saveAndUpdate() {
		save();
		notes.updateInfo();
	}

	private void removeNote() {
		notes.removeNote(note);
	}

	private String tagsOutput(String tags) {
		ArrayList<String> pieces = new ArrayList<String>(Arrays.asList(tags.split(",")));
		tags = "";
		boolean addComma = false;
		for(String piece: pieces) {
			if(!addComma) addComma = true;
			else tags = tags + ", ";
			tags = tags + piece;
		}
		return tags;
	}

	private void saveTags() {
		String tags = tagsEntry.getText();
		ArrayList<String> pieces = new ArrayList<String>(Arrays.asList(tags.split(",")));
		Iterator<String> iter = pieces.iterator();
		tags = "";
		boolean addComma = false;
		while(iter.hasNext()) {
			String piece = iter.next();
			piece = piece.trim();
			if(!piece.equals("")) {
				if(!addComma) addComma = true;
				else tags = tags += ",";
				tags = tags += piece;
			}	
		}
		note.setTags(tags);
		notes.updateNoteTags(note);
	}
}