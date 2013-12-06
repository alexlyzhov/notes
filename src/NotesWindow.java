import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import java.util.Arrays;
import java.util.ArrayList;

public class NotesWindow extends Window {
	private ArrayList<Window> children = new ArrayList<Window>();
	final private NotesList notesList;
	final private TagsList tagsList;
	private ScrolledWindow notesListWindow, tagsListWindow;
	private boolean visible;
	private NotesVBox vbox;

	public NotesWindow(String[] args, NotesList notesList, TagsList tagsList) {
		this.notesList = notesList;
		this.tagsList = tagsList;
		this.notesListWindow = notesList.getScrolledWindow();
		this.tagsListWindow = tagsList.getScrolledWindow();
		setTitle("Notes");
		setSunIcon();
		setLeftLocation();
		exitOnDelete();
		vbox = new NotesVBox(notesListWindow, tagsListWindow);
		add(vbox);
		if(!runHidden(args)) {
			toggleVisible();
		}
	}

	public void newEditor(Note note) {
		Editor editor = new Editor(note);
		children.add(editor);
		editor.removeOnDelete(children);
		Notes.getInstance().startEditing(note);
	}

	public void closeEditor(Note note) {
		for(Window i: children) {
			if(i instanceof Editor) {
				if(((Editor)i).getNoteID() == note.getID()) {
					((Editor)i).destroy();
				}
			}
		}
	}

	public void newProperties(Note note) {
		Properties properties = new Properties(note);
		children.add(properties);
		properties.removeOnDelete(children);
	}

	public void destroyChildren() {
		for(Window window: children) {
			window.destroy();
		}
	}

	private boolean runHidden(String args[]) {
		if(Arrays.asList(args).contains("hide")) return true;
		return false;
	}

	public void toggleVisible() {
		if(visible) hide();
		else showAll();
		visible = !visible;
	}

	private void setSunIcon() {
		try {
			// String dir = System.getProperty("user.dir");
			// Pixbuf sun = new Pixbuf(dir + "/ico/sun.png");
			Pixbuf sun = new Pixbuf("ico/sun.png");
			setIcon(sun);
		} catch(Exception ex) {ex.printStackTrace();}
	}

	private void setLeftLocation() {
		int sw = getScreen().getWidth();
		int sh = getScreen().getHeight();
		int w = sw * 2 / 10;
		int h = sh * 7 / 10;
		int x = sw / 10;
		int y = (sh - h) / 2;
		setDefaultSize(w, h);
		move(x, y);
	}

	private void exitOnDelete() {
		connect(new Window.DeleteEvent() {
		    public boolean onDeleteEvent(Widget source, Event event) {
		    	toggleVisible();
		    	return true;
		    }
		});
	}

	private class NewNoteButton extends Button {
		private NewNoteButton() {
			super("New note");

			Pixbuf edit = null;
			try {
				edit = new Pixbuf("ico/edit.png");
				// edit = new Pixbuf(getClass().getResource("ico/edit.png").getPath());
			} catch(Exception ex) {ex.printStackTrace();}
			setImage(new Image(edit));

			connect(new Button.Clicked() {
				public void onClicked(Button button) {
					Notes.getInstance().createNote();
				}
			});
		}
	}

	private class PanedLists extends HPaned {
		private PanedLists(ScrolledWindow notesListWindow, ScrolledWindow tagsListWindow) {
			super(notesListWindow, tagsListWindow);
			setPosition(getWidth() * 2 / 3);
		}
	}

	private class NotesVBox extends VBox {
		private NewNoteButton button;
		private PanedLists paned;

		private NotesVBox(ScrolledWindow notesListWindow, ScrolledWindow tagsListWindow) {
			super(false, 0);
			button = new NewNoteButton();
			paned = new PanedLists(notesListWindow, tagsListWindow);
			packStart(button, false, false, 0);
			packEnd(paned, true, true, 0);
			if(tagsList.noTags()) {
				showNotesList();
			}
		}

		private void showNotesList() {
			Widget[] elems = getChildren();
			if(Arrays.asList(elems).contains(paned)) {
				remove(paned);
				for(Widget widget: paned.getChildren()) {
					if(widget.equals(notesListWindow)) {
						paned.remove(notesListWindow);
					}
				}
				packEnd(notesListWindow, true, true, 0);
				showAll();
			}
		}

		private void showPaned() {
			Widget[] elems = getChildren();
			if(Arrays.asList(elems).contains(notesListWindow)) {
				boolean listInPaned = false;
				for(Widget widget: paned.getChildren()) {
					if(widget.equals(notesListWindow)) {
						listInPaned = true;
					}
				}
				remove(notesListWindow);
				if(listInPaned == false) {
					paned.add1(notesListWindow);
				}
				packEnd(paned, true, true, 0);
				showAll();
			}
		}
	}

	public void showNotesList() {
		vbox.showNotesList();
	}

	public void showPaned() {
		vbox.showPaned();
	}
}