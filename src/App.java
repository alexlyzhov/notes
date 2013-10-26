public class App {
	Base base;
	View view;

	public static void main(String args[]) {
		new App(args);
	}

	public App(String args[]) {
		base = new Base();
		view = new View(args, base, this);
		view.start();
	}

	public void exit() {
		base.closeQueue();
		view.exit();
	}
}