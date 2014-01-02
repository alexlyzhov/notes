import org.gnome.gtk.*;
import org.gnome.gdk.EventKey;
import org.gnome.gdk.Keyval;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

public class Properties extends Dialog {
	private Widgets widgets;
	private Note note;
	private final Notes notes = Notes.getInstance();

	private TagsEntry tagsEntry;
	private RemoveButton removeButton;

	public Properties(Note note) {
		hide();
		widgets = new Widgets(this);
		this.note = note;

		widgets.setIcon("properties.png");
		widgets.destroyOnDelete();

		widgets.setNameTitle(note);
		notes.startEditing(note);
		widgets.closeOnDelete(note);

		add(new Label("Note name:"));
		add(new Entry(note.getFilledName()));

		add(new Label("Tags separated by comma:"));
		tagsEntry = new TagsEntry(tagsOutput(note.getTags()));
		add(tagsEntry);

		removeButton = new RemoveButton();
		add(removeButton);

		addButton("Cancel", ResponseType.CANCEL);
		addButton("OK", ResponseType.OK);
		setDefaultResponse(ResponseType.OK);
		connect(new Dialog.Response() {
			public void onResponse(Dialog dialog, ResponseType responseType) {
				if(responseType == ResponseType.CANCEL) {
					destroy();
				} else if(responseType == ResponseType.OK) {
					// if(tagsChanged()) {
					// 	saveAndUpdate();
					// }
					save();
					updateAndDestroy();
				}
			}
		});

		showAll();
		widgets.placeInNotesCenter();
		present();
	}

	private class TagsEntry extends Entry {
		private TagsEntry(String tags) {
			super(tags);
			connect(new Widget.KeyPressEvent() {
				public boolean onKeyPressEvent(Widget source, EventKey event) {
					if(event.getKeyval() == Keyval.Return) {
						save();
						updateAndDestroy();
						return true;
					}
					return false;
				}
			});
		}
	}

	private class RemoveButton extends Button {
		private RemoveButton() {
			super("Delete note");
			connect(new Button.Clicked() {
				public void onClicked(Button source) {
					removeNote();
					updateAndDestroy();
				}
			});
		}
	}

	// private boolean tagsChanged() {
	// 	return !note.getTags().equals(tagsOutput(tagsEntry.getText()));
	// }

	private void save() {
		saveTags();
	}

	private void updateAndDestroy() {
		notes.updateLists();
		destroy();
	}

	private void saveTags() {
		String tags = tagsToDB(tagsEntry.getText());
		note.setTags(tags);
		notes.updateNoteTags(note);
	}

	private void removeNote() {
		notes.removeNoteAndUpdate(note);
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

	private String tagsToDB(String tags) {
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
		return tags;
	}
}