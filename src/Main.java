import org.gnome.gtk.Gtk;

public class Main {
	private Data data;
	private MainWindow window;
	private Keys keys;

	public static void main(String[] args) {
		Gtk.init(args);
		new Main();
	}

	private Main() {
		data = new Data();
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
				data.quit();
			}
		});
	}
}