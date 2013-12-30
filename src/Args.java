public class Args {
	public static Args args;
	private String[] argsArr;

	public static void init(String[] argsArr) {
		args = new Args(argsArr);
	}

	private Args(String[] argsArr) {
		this.argsArr = argsArr;
	}

	public static Args getInstance() {
		return args;
	}

	public String getNamedArgument(String tag) {
		boolean returnNext = false;
		for(String i: argsArr) {
			if(i.startsWith("-" + tag)) {
				returnNext = true;
			} else if(returnNext) {
				return i;
			}
		}
		return null;
	}

	public Boolean getBooleanArgument(String tag) {
		String namedArgument = getNamedArgument(tag);
		if(namedArgument == null) return null;
		if(namedArgument.equals("true")) return true;
		return false;
	}
}