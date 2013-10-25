public class App {
	Base base;
	Expression expression;

	public static void main(String args[]) {
		new App(args);
	}

	public App(String args[]) {
		base = new Base();
		expression = new Expression(args, base, this);
		expression.express();
	}

	public void exit() {
		base.closeQueue();
		expression.exit();
	}
}