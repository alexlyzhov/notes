import javax.swing.*;
import java.awt.*;

//optimize startup and note opening time
//add new class for a native kde tray icon; add support for kde global shortcuts
//exclude exsessive code for saving frame coordinates; write native kde code for this purpose
//improve Notes and Editor frames appearance
//improve linux/kde system integration, create daemons, installators, scripts, packages for convenience; do something with classpath and library path; pack jar
//create git repository, export on github; move this issue list to github issues
//autosave after each note change
//trash can feature
//move interface to qt; in further also provide gtk version
//add note tags feature
//support for notifications and checklists
//windows support
//move from jdbc sqlite driver to sqlite4java; optimize database classes; write some sort of protocol

public class Main {
	private static final String ICO_DIR = "ico/";
	public static final String SUN_ICO = ICO_DIR + "sun.png";
	public static final String EDIT_ICO = ICO_DIR + "edit.png";

	public static void main(String[] args) {
		Notes notes = new Notes();
		new Keys(notes);
		// new Tray();
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