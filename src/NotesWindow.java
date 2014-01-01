import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import java.util.ArrayList;

public class NotesWindow extends Window {
	private NotesVBox vbox;
	final private NotesList notesList;
	final private TagsList tagsList;
	private ArrayList<Widget> children = new ArrayList<Widget>();
	private boolean visible = false;

	public NotesWindow(NotesList notesList, TagsList tagsList) {
		this.notesList = notesList;
		this.tagsList = tagsList;
		setTitle("Notes");
		setSunIcon();
		setLeftLocation();
		hideOnDelete();
		vbox = new NotesVBox(notesList, tagsList);
		add(vbox);
		if(!runHidden()) toggleVisible();
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

	private void hideOnDelete() {
		connect(new Window.DeleteEvent() {
		    public boolean onDeleteEvent(Widget source, Event event) {
		    	toggleVisible();
		    	return true;
		    }
		});
	}

	private boolean runHidden() {
		Boolean hideBoolean = Args.getInstance().getBooleanArgument("hide");
		if((hideBoolean == null) || (hideBoolean == false)) return false;
		return true;
	}

	public void toggleVisible() {
		if(visible) hide();
		else showAll();
		visible = !visible;
	}

	private void addChild(Widget widget) {
		children.add(widget);
		widget.connect(new Widget.Destroy() {
			public void onDestroy(Widget widget) {
		    	children.remove(widget);
		    }
		});
	}

	public void destroyChildren() {
		for(Widget widget: children) {
			widget.destroy();
		}
	}

	public void newEditor(Note note) {
		Editor editor = new Editor(note);
		addChild(editor);
	}

	public void closeEditor(Note note) {
		for(Widget i: children) {
			if(i instanceof Editor) {
				if(((Editor)i).getNoteID() == note.getID()) {
					((Editor)i).destroy();
				}
			}
		}
	}

	public void newProperties(Note note) {
		Properties properties = new Properties(note);
		addChild(properties);
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
					Notes.getInstance().openNewNote();
				}
			});
		}
	}

	private class PanedLists extends HPaned {
		private PanedLists(ScrolledList scrolledNotesList, ScrolledList scrolledTagsList) {
			super(scrolledNotesList, scrolledTagsList);
			setPosition(getWidth() * 2 / 3);
		}
	}

	private class NotesVBox extends VBox {
		private NewNoteButton button;
		private PanedLists paned;
		private ScrolledList scrolledNotesList, scrolledTagsList;
		private boolean tagsShown;

		private NotesVBox(NotesList notesList, TagsList tagsList) {
			super(false, 0);
			button = new NewNoteButton();
			scrolledNotesList = notesList.getTree().getScrolledList();
			scrolledTagsList = tagsList.getTree().getScrolledList();
			paned = new PanedLists(scrolledNotesList, scrolledTagsList);
			packStart(button, false, false, 0);
			packPaned();
			if(tagsList.noTags()) {
				showNotesList();
			}
		}

		private void showNotesList() {
			if(tagsShown) {
				remove(paned);
				for(Widget widget: paned.getChildren()) {
					if(widget.equals(scrolledNotesList)) {
						paned.remove(scrolledNotesList);
					}
				}
				showAll();
				tagsShown = !tagsShown;
			}
		}

		private void showPaned() {
			if(!tagsShown) {
				boolean listInPaned = false;
				for(Widget widget: paned.getChildren()) {
					if(widget.equals(scrolledNotesList)) {
						listInPaned = true;
					}
				}
				remove(scrolledNotesList);
				if(listInPaned == false) {
					paned.add1(scrolledNotesList);
				}
				tagsShown = !tagsShown;
				showAll();
			}
		}

		private void packPaned() {
			packEnd(paned, true, true, 0);
			tagsShown = true;
		}

		private void packNotesList() {
			packEnd(scrolledNotesList, true, true, 0);
			tagsShown = false;
		}
	}

	public void updateLists() {
		if(tagsList.noTags()) {
			vbox.showNotesList();
		} else {
			vbox.showPaned();
		}
	}
}