import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.EventKey;

public class Editor extends Window {
	private Note note;
	private Notes notes;

	private NameEntry nameEntry;
	private ScrolledText text;
	private EditorVBox vbox;

	public Editor(Note noteParam) {
		note = noteParam;
		notes = Notes.getInstance();
		notes.startEditing(note);

		setNameTitle();
		setEditIcon();
		setCenterLocation();
		destroyOnDelete();
		closeOnDelete();

		nameEntry = new NameEntry(note.getName());
		text = new ScrolledText(note.getContent());
		vbox = new EditorVBox(nameEntry, text);
		add(vbox);
		showAll();
	}

	private void setNameTitle() {
		String name = note.getName();
		if(name.equals("")) name = "Nameless";
		setTitle(name);
	}

	private void setEditIcon() {
		try {
			Pixbuf edit = new Pixbuf("ico/edit.png");
			setIcon(edit);
		} catch(Exception ex) {ex.printStackTrace();}
	}

	private void setCenterLocation() {
		setDefaultSize(getScreen().getWidth() / 2, getScreen().getHeight() / 2);
		move(getScreen().getWidth() / 8 * 3, getScreen().getHeight() / 4);
	}

	private void destroyOnDelete() {
		connect(new Window.DeleteEvent() {
			public boolean onDeleteEvent(Widget source, Event event) {
				source.destroy();
				return false;
			}
		});
	}

	private void closeOnDelete() {
		connect(new Widget.Destroy() {
		    public void onDestroy(Widget source) {
		    	notes.finishEditing(note);
		    }
		});
	}

	public int getNoteID() {
		return (int) note.getID();
	}

	private void updateNoteData() {
		note.setName(nameEntry.getText());
		note.setContent(text.getText());
		if(note.isUsable()) {
			notes.updateNote(note);
			notes.updateNotesList(); //notesList.updateView()
		}
	}

	private class NameEntry extends Entry {
		private NameEntry(String name) {
			super(name);
			connect(new Entry.Changed() {
				public void onChanged(Entry entry) {
					updateNoteData();
					setNameTitle();
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
}