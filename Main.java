import javax.swing.*;
import java.awt.*;

public class Main {
	private static final String ICO_DIR = "ico/";
	public static final String SUN_ICO = ICO_DIR + "sun.png";
	public static final String EDIT_ICO = ICO_DIR + "edit.png";

	public static void main(String[] args) {
		Notes notes = new Notes();
		new Keys(notes);
	}

	public static ImageIcon getImageIcon(String name) {
		return new ImageIcon(name);
	}

	public static ImageIcon getImageIcon(String name, int newWidth, int newHeight) {
		ImageIcon imageIcon = getImageIcon(name);
		Image image = imageIcon.getImage();
		image = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		return new ImageIcon(image);
	}
}