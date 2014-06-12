import org.gnome.gtk.*;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.EventKey;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import java.io.*;
import org.gnome.sourceview.*;

public class Editor extends Window {
	private Data data;
	private Note note;

	private MainWindow mainWindow;
	private NameEntry nameEntry;
	private ScrolledText text;
	private EditorVBox vbox;

	public Editor(final Note note, final Data data, final MainWindow mainWindow) {
		hide();
		this.data = data;
		this.note = note;
		this.mainWindow = mainWindow;

		setIcon();
		setCenterLocation();
		connect(new Window.DeleteEvent() {
			public boolean onDeleteEvent(Widget source, Event event) {
				source.destroy();
				return false;
			}
		});

		updateNameTitle();
		mainWindow.startEditing(note);
		connect(new Window.Destroy() {
		    public void onDestroy(Widget source) {
		    	mainWindow.finishEditing(note);
		    }
		});

		nameEntry = new NameEntry(note.getPureName());
		text = new ScrolledText(note.content);
		vbox = new EditorVBox(nameEntry, text);
		add(vbox);
		showAll();
	}

	private void setIcon() {
		Pixbuf pixbuf = null;
		String name = "edit.png";
		try {
			InputStream inputStream = getClass().getResourceAsStream("ico/" + name);
		    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		    for (int readBytes = inputStream.read(); readBytes >= 0; readBytes = inputStream.read()) {
		    	outputStream.write(readBytes);
		    }
		    byte[] bytes = outputStream.toByteArray();
		    inputStream.close();
		    outputStream.close();
			pixbuf = new Pixbuf(bytes);
		} catch(Exception ex) {ex.printStackTrace();}
		setIcon(pixbuf);
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
		String name = note.getViewableName();
		setTitle(name);
	}

	public int getNoteID() {
		return note.id;
	}

	public Note getNote() {
		return note;
	}

	private void updateNoteData() {
		note.setName(nameEntry.getText());
		note.content = text.getText();
		if(note.id != -1) {
			note.updateTime();
			mainWindow.updateNote(note);
		} else {
			mainWindow.updateNoteView(note);
		}
	}
}