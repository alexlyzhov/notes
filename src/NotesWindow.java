import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import java.util.Arrays;
import java.util.ArrayList;

public class NotesWindow extends Window {
	private Notes notes;
	private NotesList notesList;
	private TagsList tagsList;
	private ScrolledWindow notesListWindow, tagsListWindow;
	private boolean visible;
	private boolean showTagsInfo = true;
	private NotesVBox vbox;
	private ArrayList<Editor> editors = new ArrayList<Editor>();

	public NotesWindow(String[] args, Notes notes, NotesList notesList, TagsList tagsList) {
		this.notes = notes;
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
		Editor editor = new Editor(note, notes, showTagsInfo);
		editors.add(editor);
		editor.removeOnDelete(editors);
	}

	public void toggleTags() {
		showTagsInfo = !showTagsInfo;
		vbox.togglePack();
		for(Editor editor: editors) {
			editor.toggleTags();
		}
	}

	public void toggleTrash() {
		tagsList.toggleTrash();
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
		    	notes.exit();
		        return false;
		    }
		});
	}

	private class NewNoteButton extends Button {
		private NewNoteButton() {
			super("New note");

			Pixbuf edit = null;
			try {
				edit = new Pixbuf("ico/edit.png");
			} catch(Exception ex) {ex.printStackTrace();}
			setImage(new Image(edit));

			connect(new Button.Clicked() {
				public void onClicked(Button button) {
					notes.createNote();
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
		}

		private void togglePack() {
			Widget[] elems = getChildren();
			if(Arrays.asList(elems).contains(paned)) {
				remove(paned);
				for(Widget widget: paned.getChildren()) {
					if(widget.equals(notesListWindow)) {
						paned.remove(notesListWindow);
					}
				}
				packEnd(notesListWindow, true, true, 0);
			} else if(Arrays.asList(elems).contains(notesListWindow)) {
				boolean listInPaned = false;
				for(Widget widget: paned.getChildren()) {
					if(widget.equals(notesListWindow)) listInPaned = true;
				}
				remove(notesListWindow);
				if(listInPaned == false) {
					paned.add1(notesListWindow);
				}
				packEnd(paned, true, true, 0);
			} else System.out.println("Nothing to toggle and pack about");
		}
	}
}