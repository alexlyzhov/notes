import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.EventKey;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

public class Editor extends Window {
	private Note note;
	private Notes notes;
	private boolean changed;

	private NameEntry nameEntry;
	private TagsEntry tagsEntry;
	private ScrolledText text;
	private EditorVBox vbox;

	public Editor(Note noteParam, Notes notesParam) {
		note = noteParam;
		notes = notesParam;
		notes.startEditing(note);
		notes.getEditors().add(this);

		setNameTitle();
		setEditIcon();
		setCenterLocation();
		saveOnDelete();

		nameEntry = new NameEntry(note.getName());
		tagsEntry = new TagsEntry(tagsOutput(note.getTags()));
		text = new ScrolledText(note.getContent());
		vbox = new EditorVBox(nameEntry, tagsEntry, text);
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
		setPosition(WindowPosition.CENTER);
	}

	private void saveOnDelete() {
		connect(new Window.DeleteEvent() {
		    public boolean onDeleteEvent(Widget source, Event event) {
		    	notes.finishEditing(note);
		    	notes.getEditors().remove(this);
		        return false;
		    }
		});
	}

	private void updateNoteData() {
		changed = true;
		note.setName(nameEntry.getText());
		note.setContent(text.getText());
		saveTags();
		notes.updateNote(note);
		notes.updateTagsList();
		notes.updateNotesList();
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
						text.grabFocus();
						return true;
					}
					return false;
				}
			});
		}
	}

	private class TagsEntry extends Entry {
		private TagsEntry(String tags) {
			super(tags);
			connect(new Entry.Changed() {
				public void onChanged(Entry entry) {
					updateNoteData();
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

			if(!nameEntry.getText().equals("")) view.grabFocus();
		}
		private String getText() {
			return buffer.getText();
		}
		private void grabTextFocus() {
			view.grabFocus();
		}
	}

	private class EditorVBox extends VBox {
		private TagsEntry tagsEntry = null;
		private EditorVBox(NameEntry nameEntry, TagsEntry tagsEntry, ScrolledText text) {
			super(false, 0);
			packStart(nameEntry, false, false, 0);
			packEnd(text, true, true, 0);
			this.tagsEntry = tagsEntry;
			tagsEntry.show();
			if(notes.tags) {
				packTags();
			}
		}

		private void toggleTags() {
			if(Arrays.asList(getChildren()).contains(tagsEntry)) {
				remove(tagsEntry);
			} else {
				packTags();
			}
		}

		private void packTags() {
			packStart(tagsEntry, false, false, 0);
		}
	}

	public void toggleTags() {
		vbox.toggleTags();
	}
}