import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;
import org.gnome.gdk.EventFocus;
import java.util.*;
import java.io.*;

public class MainWindow extends Window {
	private Data data;
	private QuickAccess quickAccess;
	private ArrayList<Widget> children = new ArrayList<Widget>(); //is it a good idea?
	private Stack<Editor> activeEditors = new Stack<Editor>();
	private boolean visible = false;
	private NotesStore notesStore;
	private ListsStore listsStore;
	private NotesView notesView;
	private ListsView listsView;

	public MainWindow(Data data) {
		this.data = data;
		this.quickAccess = data.getQuickAccess();
		hide();

		notesStore = new NotesStore(data);
		listsStore = new ListsStore(data, notesStore);
		notesView = new NotesView(notesStore, this, data);
		listsView = new ListsView(listsStore);
		listsStore.setSelection(listsView.getSelection());
		listsStore.update();

		setTitle("Notes");
		setIcon();
		setLeftLocation();
		hideOnDelete();

		ScrolledWindow notesScrolledWindow = new ScrolledWindow();
		notesScrolledWindow.add(notesView);
		notesScrolledWindow.setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);

		VBox notesVBox = new VBox(false, 0);
		notesVBox.packStart(new NewNoteButton(), false, false, 0);
		notesVBox.packEnd(notesScrolledWindow, true, true, 0);

		ScrolledWindow listsScrolledWindow = new ScrolledWindow();
		listsScrolledWindow.add(listsView);
		listsScrolledWindow.setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);

		VBox listsVBox = new VBox(false, 0);
		listsVBox.packStart(new NewListButton(), false, false, 0);
		listsVBox.packEnd(listsScrolledWindow, true, true, 0);

		HPaned paned = new HPaned(notesVBox, listsVBox);
		paned.setPosition(getWidth() * 2 / 3);
		add(paned);

		toggleVisible();
	}

	private class NewNoteButton extends Button {
		private NewNoteButton() {
			super("New note");

			Pixbuf edit = null;
			try {
				InputStream inputStream = getClass().getResourceAsStream("ico/" + "edit.png");
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				for (int readBytes = inputStream.read(); readBytes >= 0; readBytes = inputStream.read()) {
					outputStream.write(readBytes);
				}
				byte[] bytes = outputStream.toByteArray();
				inputStream.close();
				outputStream.close();
				edit = new Pixbuf(bytes);
			} catch(Exception ex) {ex.printStackTrace();}
			setImage(new Image(edit));

			connect(new Button.Clicked() {
				public void onClicked(Button button) {
					openNewNote(); //quantify
				}
			});
		}
	}

	private class NewListButton extends Button {
		private NewListButton() {
			super("New list");

			connect(new Button.Clicked() {
				public void onClicked(Button button) {

				}
			});
		}
	}

	private void setIcon() {
		Pixbuf pixbuf = null;
		String name = "sun.png";
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

	public void toggleVisible() {
		if(visible) hide();
		else showAll();
		visible = !visible;
	}

	private void addChild(Widget widget) { //check if editors really line up the way they need to
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
			widget.connect(new Widget.Destroy() {
				public void onDestroy(Widget widget) {
					activeEditors.removeElement((Editor) widget);
				}
			});
		}
	}

	public void quit() {
		destroyChildren();
		destroy();
	}

	private void destroyChildren() {
		for(Widget widget: children) {
			widget.destroy();
		}
	}

	public Editor findEditor(Note note) {
		// for(Widget i: children) {
		// 	if(i instanceof Editor) {
		// 		if(((Editor)i).getNoteID() == note.getID()) {
		// 			return (Editor)i;
		// 		}
		// 	}
		// }
		for(Editor i: activeEditors) {
			if(i.getNoteID() == note.id) {
				return i;
			}
		}
		return null;
	}

	public Properties findProperties(Note note) {
		for(Widget i: children) {
			if(i instanceof Properties) {
				if(((Properties)i).getNoteID() == note.id) {
					return (Properties)i;
				}
			}
		}
		return null;
	}

	public void openNote(Note note) {
		if(!note.editing) {
			Editor editor = new Editor(note, data, this);
			addChild(editor);
		}
	}

	public void openNewNote() {
		Note newNote = newNote(listsStore.getSelectedList());
		openNote(newNote);
	}

	public void updateNoteView(Note note) {
		notesStore.updateInfo(note);
	}

	public void updateStores() {
		notesStore.update(listsStore);
		listsStore.update();
	}

	public void updateNote(Note note) {
		data.updateNote(note);
		updateStores();
		// updateNoteView(note);
	}

	public boolean closeNote(Note note) {
		Editor noteEditor = findEditor(note);
		if(noteEditor != null) {
			noteEditor.destroy();
			return true;
		}
		Properties noteProperties = findProperties(note);
		if(noteProperties != null) {
			noteProperties.destroy();
			return true;
		}
		return false;
	}

	public void invokeProperties(Note note) {
		Properties properties = new Properties(note, this, quickAccess);
		addChild(properties);
	}

	public Editor popActiveEditor() {
		Editor editor = null;
		try {
			editor = activeEditors.pop();
		} catch(EmptyStackException ex) {}
		return editor;
	}

	public void openQuick(int slotNumber) {
		Note note = quickAccess.findQuickNote(slotNumber);
		if(note != null) {
			if(!closeNote(note)) { //check if already opened by a separate method! remove boolean closeNote()
				openNote(note);
			}
		}
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
				removeNoteAndUpdate(note);
			}
		}
	}

	public void exit() {
		Gtk.mainQuit();
	}

	public void startEditing(Note note) {
		data.startEditing(note);
		updateNoteView(note);
	}

	public void finishEditing(Note note) {
		data.finishEditing(note);
		if(note.empty()) {
    		data.removeNoteCompletelyAndUpdate(note);
    	} else {
    		updateNoteView(note);
    	}
	}

	public void removeNoteAndUpdate(Note note) {
		if(listsStore.trashListSelected()) {
			data.removeNoteCompletelyAndUpdate(note);
		} else {
			data.removeNoteToTrashAndUpdate(note);
		}
	}

	public Note newNote(List list) {
		if(listsStore.trashListSelected()) {
			listsStore.selectGeneralList();
			listsStore.update();
		}
		Note note = data.newNote();
		if(listsStore.actualList(list)) {
			list.notes.add(note);
		}
		updateStores();
		return note;
	}
}