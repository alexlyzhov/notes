import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Tray {
	private SystemTray tray;
	private TrayIcon icon;

	public Tray() {
		if(SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();

			icon = new TrayIcon(new ImageIcon(Main.SUN_ICO).getImage());
			icon.setImageAutoSize(true);

			icon.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if(e.getButton() == MouseEvent.BUTTON1) {
						getMainFrame().setVisible(true);
					}
				}
			});

			PopupMenu popup = new PopupMenu();
			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					exitApplication();
				}
			});
			popup.add(exitItem);
			icon.setPopupMenu(popup);

			try {
	            tray.add(icon);
	        } catch (AWTException ex) {
	            System.out.println("TrayIcon could not be added");
	        }
		} else System.out.println("Tray is not supported");
	}

	private Notes getMainFrame() {
		for(Frame i: Frame.getFrames()) {
			if(i instanceof Notes) {
				return (Notes) i;
			}
		}
		return null;
	}

	private void exitApplication() {
		getMainFrame().cleanUp();
		for(Frame i: Frame.getFrames()) i.dispose();
		dispose();
	}

	private void dispose() {
		if(tray != null) {
			tray.remove(icon);
		}
	}
}