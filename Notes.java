import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;

public class Notes extends Window {
	private Pixbuf sun, edit;
	private VBox vbox;
	private NotesList list;
	private Keys keys;
	private boolean visible;
	private ScrolledWindow scroll;

	public static void main(String args[]) {
		Gtk.init(args);
		new Notes();
		Gtk.main();
	}

	public void exit() {
		keys.cleanUp();
		list.onExit();
        Gtk.mainQuit();
	}

	private Notes() {
		try {
			sun = new Pixbuf("ico/sun.png");
			edit = new Pixbuf("ico/edit.png");
		} catch(Exception ex) {ex.printStackTrace();}
		setTitle("Notes");
		setIcon(sun);
		setDefaultSize(250, 550);
		vbox = new VBox(false, 0);
		add(vbox);
		Button button = new Button("New note");
		button.setImage(new Image(edit));
		button.connect(new Button.Clicked() {
			public void onClicked(Button button) {
				createNote();
			}
		});
		vbox.packStart(button, false, false, 0);
		list = new NotesList();
		scroll = new ScrolledWindow();
		scroll.setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
		scroll.getVAdjustment().connect(new Adjustment.Changed() {
			public void onChanged(Adjustment source) {
				source.setValue(0);
			}
		});
		scroll.add(list.getTreeView());
		vbox.packStart(scroll, true, true, 0);
		connect(new Window.DeleteEvent() {
		    public boolean onDeleteEvent(Widget source, Event event) {
		    	exit();
		        return false;
		    }
		});
		toggleVisible();
		keys = new Keys(this);
	}

	public void toggleVisible() {
		if(visible) hide();
		else showAll();
		visible = !visible; 
	}

	public void createNote() {
		new Editor(list, list.newNote());
	}
}