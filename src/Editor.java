import org.gnome.gtk.*;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.EventKey;

public class Editor extends Window {
	private Widgets widgets;
	private Note note;
	private final Notes notes = Notes.getInstance();

	private NameEntry nameEntry;
	private ScrolledText text;
	private EditorVBox vbox;

	public Editor(Note note) {
		widgets = new Widgets(this);
		this.note = note;

		widgets.setIcon("edit.png");
		widgets.setCenterLocation();
		widgets.destroyOnDelete();

		updateNameTitle();
		notes.startEditing(note);
		widgets.closeOnDelete(note);

		nameEntry = new NameEntry(note.getName());
		text = new ScrolledText(note.getContent());
		vbox = new EditorVBox(nameEntry, text);
		add(vbox);
		showAll();
	}

	private class NameEntry extends Entry {
		private NameEntry(String name) {
			super(name);
			connect(new Entry.Changed() {
				public void onChanged(Entry entry) {
					updateNoteData();
					updateNameTitle();
				}
			});
			connect(new Widget.KeyPressEvent() {
				public boolean onKeyPressEvent(Widget source, EventKey event) {
					if(event.getKeyval() == Keyval.Return) {
						text.grabTextFocus();
						return true;
					}
					return false;
				}
			});
		}
	}

	private class ScrolledText extends ScrolledWindow {
		private TextBuffer buffer;
		private TextView view;

		private ScrolledText(String content) {
			buffer = new TextBuffer();
			buffer.setText(content);
			buffer.connect(new TextBuffer.Changed() {
				public void onChanged(TextBuffer buffer) {
					updateNoteData();
				}
			});

			view = new TextView(buffer);
			view.setWrapMode(WrapMode.WORD);

			setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
			add(view);
		}

		private String getText() {
			return buffer.getText();
		}

		private void grabTextFocus() {
			view.grabFocus();
		}
	}

	private class EditorVBox extends VBox {
		private EditorVBox(NameEntry nameEntry, ScrolledText text) {
			super(false, 0);
			packStart(nameEntry, false, false, 0);
			packEnd(text, true, true, 0);
			if(!nameEntry.getText().equals("")) {
				text.grabTextFocus();
			}
		}
	}

	private void updateNameTitle() {
		widgets.setNameTitle(note);
	}

	public int getNoteID() {
		return (int) note.getID();
	}

	private void updateNoteData() {
		note.setName(nameEntry.getText());
		note.setContent(text.getText());
		if(note.isUsable()) {
			notes.updateNote(note);
		}
		notes.updateNotesList(); //notesList.updateView()
	}
}