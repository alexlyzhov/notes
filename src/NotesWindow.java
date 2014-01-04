import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import java.util.*;
import org.gnome.gdk.EventFocus;

public class NotesWindow extends Window {
	public static NotesWindow notesWindow;
	public Notes notes;
	private NotesVBox vbox;
	final private NotesList notesList;
	final private TagsList tagsList;
	private ArrayList<Widget> children = new ArrayList<Widget>();
	private boolean visible = false;
	private Stack<Editor> activeEditors = new Stack<Editor>();

	public static void init(NotesList notesList, TagsList tagsList) {
		notesWindow = new NotesWindow(notesList, tagsList);
	}

	private NotesWindow(NotesList notesList, TagsList tagsList) {
		hide();
		this.notes = Notes.getInstance();
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

	public static NotesWindow getInstance() {
		return notesWindow;
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
		int x = sw / 40;
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
		if(widget instanceof Editor) {
			widget.connect(new Widget.FocusInEvent() {
				public boolean onFocusInEvent(Widget source, EventFocus event) {
					Editor curr = (Editor) source;
					if(activeEditors.search(curr) == -1) {
						activeEditors.push(curr);
					}
					return false;
				}
			});
		}
	}

	public void destroyChildren() {
		for(Widget widget: children) {
			widget.destroy();
		}
	}

	public Editor findEditor(Note note) {
		for(Widget i: children) {
			if(i instanceof Editor) {
				if(((Editor)i).getNoteID() == note.getID()) {
					return (Editor)i;
				}
			}
		}
		return null;
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
					openNewNote();
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
				packNotesList();
				showAll();
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
				packPaned();
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

	public void updateListsWindowView() {
		if(tagsList.noTags()) {
			vbox.showNotesList();
		} else {
			vbox.showPaned();
		}
	}

	public void openNote(Note note) {
		if(!note.isEditing()) {
			Editor editor = new Editor(note);
			addChild(editor);
		}
	}

	public void openNewNote() {
		openNote(notes.newNote());
	}

	public void closeNote(Note note) {
		Editor noteEditor = findEditor(note);
		if(noteEditor != null) {
			noteEditor.destroy();
		}
	}

	public void invokeProperties(Note note) {
		Properties properties = new Properties(note);
		addChild(properties);
	}

	public Editor popActiveEditor() {
		Editor editor = null;
		try {
			editor = activeEditors.pop();
		} catch(EmptyStackException ex) {}
		return editor;
	}

	public void closeCurrentNote() {
		Editor activeEditor = popActiveEditor();
		if(activeEditor != null) {
			activeEditor.destroy();
		}
	}

	public void removeCurrentNote() {
		Editor activeEditor = popActiveEditor();
		if(activeEditor != null) {
			Note note = activeEditor.getNote();
			activeEditor.destroy();
			if(!note.empty()) {
				notes.removeNoteAndUpdate(note);
			}
		}
	}
}