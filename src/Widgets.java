import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;

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

	public void setIcon(String ico) {
		try {
			Pixbuf icon = new Pixbuf("ico/" + ico);
			w.setIcon(icon);
		} catch(Exception ex) {ex.printStackTrace();}
	}

	public void placeInNotesCenter(NotesWindow notesWindow) {
		Notes notes = Notes.getInstance();
		int centerX = notesWindow.getPositionX() + notesWindow.getWidth() / 2;
		int centerY = notesWindow.getPositionY() + notesWindow.getHeight() / 2;
		int newX = centerX - w.getWidth() / 2;
		int newY = centerY - w.getHeight() / 2;
		w.move(newX, newY);
	}
}