import org.gnome.gtk.Gtk;

public class Expression {
	Notes notes;
	Keys keys;

	public Expression(String[] args, Base base, App app) {
		Gtk.init(args);
		notes = new Notes(args, base, app);
		keys = new Keys(notes);
	}

	public void express() {
		Gtk.main();
	}

	public void exit() {
		keys.cleanUp();
		Gtk.mainQuit();
	}
}