import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import java.util.Arrays;
import java.util.ArrayList;

public class NotesWindow extends Window {
	private ArrayList<Editor> editors = new ArrayList<Editor>();
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
		editors.add(editor);
		editor.removeOnDelete(editors);
	}

	public void destroyEditors() {
		for(Editor editor: editors) {
			editor.destroy();
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
			String dir = System.getProperty("user.dir");
			Pixbuf sun = new Pixbuf(dir + "/ico/sun.png");
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
				String dir = System.getProperty("user.dir");
				edit = new Pixbuf(dir + "/ico/edit.png");
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
			}
		}

		private void showPaned() {
			Widget[] elems = getChildren();
			if(Arrays.asList(elems).contains(notesListWindow)) {
				boolean listInPaned = false;
				for(Widget widget: paned.getChildren()) {
					if(widget.equals(notesListWindow)) listInPaned = true;
				}
				remove(notesListWindow);
				if(listInPaned == false) {
					paned.add1(notesListWindow);
				}
				packEnd(paned, true, true, 0);
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