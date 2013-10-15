import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;

public class Notes extends Window {
	private Pixbuf sun, edit;
	private VBox vbox;
	private NotesList list;
	private Keys keys;

	public static void main(String args[]) {
		Gtk.init(args);
		new Notes();
		Gtk.main();
	}

	public void exit() {
		// keys.cleanUp();
		list.onExit();
        Gtk.mainQuit();
	}

	private Notes() { //clean project folder after installing thunar trash plugin
		try {
			sun = new Pixbuf("ico/sun.png");
			edit = new Pixbuf("ico/edit.png");
		} catch(Exception ex) {ex.printStackTrace();}
		setTitle("Notes");
		setIcon(sun);
		vbox = new VBox(false, 0);
		add(vbox);
		Button button = new Button("New note");
		button.setImage(new Image(edit));
		button.connect(new Button.Clicked() {
			public void onClicked(Button button) {
				new Editor(list.newNote(), list);
			}
		});
		vbox.packStart(button, false, false, 0);
		list = new NotesList();
		vbox.packStart(list.getTreeView(), true, true, 0);
		connect(new Window.DeleteEvent() {
		    public boolean onDeleteEvent(Widget source, Event event) {
		    	exit();
		        return false;
		    }
		});
		setDefaultSize(250, 550);
		// keys = new Keys(this);
		showAll();
	}



	public void toggleVisible() {
		System.out.println("toggleVisible");
	}

	public void createNote() {
		System.out.println("createNote");
	}
}