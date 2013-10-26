import org.gnome.gtk.Gtk;

public class View {
	Notes notes;
	Keys keys;

	public View(String[] args, Base base, App app) {
		Gtk.init(args);
		notes = new Notes(args, base, app);
		keys = new Keys(notes);
	}

	public void start() {
		Gtk.main();
	}

	public void exit() {
		keys.cleanUp();
		Gtk.mainQuit();
	}
}