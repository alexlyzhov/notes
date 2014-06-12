import org.gnome.gtk.*;
import org.gnome.gdk.EventKey;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.*;

public class Properties extends Dialog {
	private Data data;
	private Note note;

	private MainWindow mainWindow;
	private Entry tagsEntry, nameEntry;
	private RemoveButton removeButton;
	private String originalName, originalTags;
	private int originalQuick;
	private ComboBoxText quickCombo;

	public Properties(final Note note, final MainWindow mainWindow, final Data data) {
		this.data = data;
		this.mainWindow = mainWindow;
		hide();
		this.note = note;
		originalName = note.getPureName();
		originalTags = "";
		originalQuick = note.quick;

		setIcon();
		connect(new Window.DeleteEvent() {
			public boolean onDeleteEvent(Widget source, Event event) {
				source.destroy();
				return false;
			}
		});
		setTransientFor(mainWindow);

		setTitle(note.getViewableName());
		mainWindow.startEditing(note);
		connect(new Window.Destroy() {
		    public void onDestroy(Widget source) {
		    	mainWindow.finishEditing(note);
		    }
		});

		add(new Label("Note name:"));
		add(nameEntry = new Entry(note.getPureName()));

		add(new Label("Tags separated by comma:"));
		add(tagsEntry = new Entry());
		// add(tagsEntry = new Entry(tagsOutput(note.getTags())));

		HBox quickBox = new HBox(false, 0);
		quickBox.add(new Label("Quick access slot: "));
		quickBox.add(quickCombo = new ComboBoxText());
		add(quickBox);

		quickCombo.appendText("none");
		for(int i = 1; i <= 9; i++) {
			quickCombo.appendText(String.valueOf(i));
		}
		quickCombo.setActive(note.quick);

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
		int centerX = mainWindow.getPositionX() + mainWindow.getWidth() / 2;
		int centerY = mainWindow.getPositionY() + mainWindow.getHeight() / 2;
		int newX = centerX - getWidth() / 2;
		int newY = centerY - getHeight() / 2;
		move(newX, newY);
		present();
	}

	private void setIcon() {
		Pixbuf pixbuf = null;
		String name = "properties.png";
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
			mainWindow.updateNote(note);
		}
	}

	private void saveQuick() {
		if(originalQuick != quickCombo.getActive()) {
			note.quick = quickCombo.getActive();
			mainWindow.updateNote(note);
		}
	}

	private void removeNote() {
		mainWindow.removeNoteAndUpdate(note);
	}

	private void closeDialog(boolean saveProperties) {
		if(saveProperties) {
			// saveTags();
			saveName();
			saveQuick();
		}
		mainWindow.updateStores();
		destroy();
	}

	public int getNoteID() {
		return note.id;
	}
}