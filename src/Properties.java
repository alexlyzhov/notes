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

	private Entry tagsEntry, nameEntry;
	private RemoveButton removeButton;
	private String originalName, originalTags;
	private int originalQuick;
	private ComboBoxText quickCombo;

	public Properties(Note note) {
		hide();
		widgets = new Widgets(this);
		this.note = note;
		originalName = note.getName();
		originalTags = note.getTags();
		originalQuick = note.getQuick();

		widgets.setIcon("properties.png");
		widgets.destroyOnDelete();
		setTransientFor(NotesWindow.getInstance());

		widgets.setNameTitle(note);
		notes.startEditing(note);
		widgets.closeOnDelete(note);

		add(new Label("Note name:"));
		add(nameEntry = new Entry(note.getName()));

		add(new Label("Tags separated by comma:"));
		add(tagsEntry = new Entry(tagsOutput(note.getTags())));

		HBox quickBox = new HBox(false, 0);
		quickBox.add(new Label("Quick access slot: "));
		quickBox.add(quickCombo = new ComboBoxText());
		add(quickBox);

		quickCombo.appendText("none");
		for(int i = 1; i <= 9; i++) {
			quickCombo.appendText(String.valueOf(i));
		}
		quickCombo.setActive(note.getQuick());

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
					closeDialog(true);
				}
			}
		});

		connect(new Widget.KeyPressEvent() {
			public boolean onKeyPressEvent(Widget source, EventKey event) {
				if(event.getKeyval() == Keyval.Return) {
					closeDialog(true);
					return true;
				}
				return false;
			}
		});

		showAll();
		widgets.placeInNotesCenter();
		present();
	}

	private class RemoveButton extends Button {
		private RemoveButton() {
			super("Delete note");
			connect(new Button.Clicked() {
				public void onClicked(Button source) {
					removeNote();
					closeDialog(false);
				}
			});
		}
	}

	private void saveName() {
		String name = nameEntry.getText();
		if(!originalName.equals(name)) {
			note.setName(name);
			note.updateTime();
			notes.updateNote(note);
		}
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

	private void saveTags() {
		String tags = tagsToDB(tagsEntry.getText());
		if(!originalTags.equals(tags)) {
			note.setTags(tags);
			notes.updateNote(note);
		}
	}

	private void saveQuick() {
		if(originalQuick != quickCombo.getActive()) {
			notes.clearQuick(quickCombo.getActive());
			note.setQuick(quickCombo.getActive());
			notes.updateNote(note);
		}
	}

	private void removeNote() {
		notes.removeNoteAndUpdate(note);
	}

	private void closeDialog(boolean saveProperties) {
		if(saveProperties) {
			saveTags();
			saveName();
			saveQuick();
		}
		notes.updateLists();
		destroy();
	}

	public int getNoteID() {
		return note.getID();
	}
}