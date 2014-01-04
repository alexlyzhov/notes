import org.gnome.gtk.*;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.EventKey;
import org.gnome.sourceview.*;

public class Editor extends Window {
	private Widgets widgets;
	private Note note;
	private final Notes notes = Notes.getInstance();

	private NameEntry nameEntry;
	private ScrolledText text;
	private EditorVBox vbox;

	public Editor(Note note) {
		hide();
		widgets = new Widgets(this);
		this.note = note;

		widgets.setIcon("edit.png");
		setCenterLocation();
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
		private SourceBuffer buffer;
		private SourceView view;

		private ScrolledText(String content) {
			buffer = new SourceBuffer();
			buffer.setText(content);
			buffer.connect(new SourceBuffer.Changed() {
				public void onChanged(TextBuffer buffer) {
					updateNoteData();
				}
			});

			view = new SourceView(buffer);
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

	public void setCenterLocation() {
		setDefaultSize(getScreen().getWidth() / 2, getScreen().getHeight() / 2);
		move(getScreen().getWidth() / 8 * 2, getScreen().getHeight() / 4);
	}

	private void updateNameTitle() {
		widgets.setNameTitle(note);
	}

	public int getNoteID() {
		return note.getID();
	}

	public Note getNote() {
		return note;
	}

	private void updateNoteData() {
		note.setName(nameEntry.getText());
		note.setContent(text.getText());
		if(note.isUsable()) {
			notes.updateNote(note);
		}
		notes.updateNoteView(note);
	}
}