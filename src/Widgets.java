import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import java.io.*;

public class Widgets {
	Window w;

	public Widgets(Window w) {
		this.w = w;
	}

	public void destroyOnDelete() {
		w.connect(new Window.DeleteEvent() {
			public boolean onDeleteEvent(Widget source, Event event) {
				source.destroy();
				return false;
			}
		});
	}

	public void closeOnDelete(final Note note) {
		w.connect(new Window.Destroy() {
		    public void onDestroy(Widget source) {
		    	Notes.getInstance().finishEditing(note);
		    }
		});
	}

	public void setNameTitle(Note note) {
		String name = note.getFilledName();
		w.setTitle(name);
	}

	public void setIcon(String name) {
		Pixbuf pixbuf = getPixbuf(name);
		setIcon(pixbuf);
	}

	public void setIcon(Pixbuf pixbuf) {
		w.setIcon(pixbuf);
	}

	public Pixbuf getPixbuf(String image) {
		Pixbuf pixbuf = null;
		try {
			InputStream inputStream = getClass().getResourceAsStream("ico/" + image);
		    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		    for (int readBytes = inputStream.read(); readBytes >= 0; readBytes = inputStream.read()) {
		    	outputStream.write(readBytes);
		    }
		    byte[] bytes = outputStream.toByteArray();
		    inputStream.close();
		    outputStream.close();
			pixbuf = new Pixbuf(bytes);
		} catch(Exception ex) {ex.printStackTrace();}
		return pixbuf;
	}

	public void placeInNotesCenter() {
		NotesWindow notesWindow = NotesWindow.getInstance();
		int centerX = notesWindow.getPositionX() + notesWindow.getWidth() / 2;
		int centerY = notesWindow.getPositionY() + notesWindow.getHeight() / 2;
		int newX = centerX - w.getWidth() / 2;
		int newY = centerY - w.getHeight() / 2;
		w.move(newX, newY);
	}
}