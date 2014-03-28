import org.gnome.gtk.Gtk;

public class Main {
	private Base base;
	private Data data;
	private MainWindow window;
	private Keys keys;

	public static void main(String[] args) {
		Gtk.init(args);
		new Main();
	}

	private Main() {
		base = new Base();
		data = new Data(base);
		window = new MainWindow(data);
		keys = new Keys(window);
		addMainShutdownHook();
		Gtk.main();
	}

	public void addMainShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				keys.quit();
				window.quit();
				Gtk.mainQuit();
				base.quit();
			}
		});
	}
}