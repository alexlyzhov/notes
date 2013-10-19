import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;
import org.gnome.gdk.Event;

public class Notes extends Window {
	private Base base;
	private Pixbuf sun, edit;
	private VBox vbox;
	public NotesList list;
	private TagsList tagsList;
	private Keys keys;
	private boolean visible;
	private HPaned paned;
	private ScrolledWindow scroll, tagsScroll;

	public static void main(String args[]) {
		Gtk.init(args);
		new Notes();
		Gtk.main();
	}

	public void exit() {
		if(System.getProperty("os.name").equals("Linux")) {
			keys.cleanUp();
		}
		base.closeQueue();
        Gtk.mainQuit();
	}

	private Notes() {
		base = new Base();
		try {
			sun = new Pixbuf("ico/sun.png");
			edit = new Pixbuf("ico/edit.png");
		} catch(Exception ex) {ex.printStackTrace();}
		setTitle("Notes");
		setIcon(sun);

		int width = getScreen().getWidth() * 2 / 10;
		int height = getScreen().getHeight() * 7 / 10;
		int xOffset = getScreen().getWidth() / 10;
		int yOffset = (getScreen().getHeight() - height) / 2;
		setDefaultSize(width, height);
		move(xOffset, yOffset);

		vbox = new VBox(false, 0);
		add(vbox);
		Button button = new Button("New note");
		button.setImage(new Image(edit));
		button.connect(new Button.Clicked() {
			public void onClicked(Button button) {
				createNote();
			}
		});
		vbox.packStart(button, false, false, 0);

		tagsList = new TagsList(base, this);

		list = new NotesList(base, tagsList);
		scroll = new ScrolledWindow();
		scroll.setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
		scroll.getVAdjustment().connect(new Adjustment.Changed() {
			public void onChanged(Adjustment source) {
				source.setValue(0);
			}
		});
		scroll.add(list.getTreeView());

		tagsScroll = new ScrolledWindow();
		tagsScroll.setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
		tagsScroll.add(tagsList.getTreeView());

		paned = new HPaned(scroll, tagsScroll);
		paned.setPosition(width * 2 / 3);
		vbox.packEnd(paned, true, true, 0);

		connect(new Window.DeleteEvent() {
		    public boolean onDeleteEvent(Widget source, Event event) {
		    	exit();
		        return false;
		    }
		});
		toggleVisible();
		if(System.getProperty("os.name").equals("Linux")) {
			keys = new Keys(this);
		}
	}

	public void updateList(String tag) {
		list.updateList(tag);
	}

	public void toggleVisible() {
		if(visible) hide();
		else showAll();
		visible = !visible; 
	}

	public void createNote() {
		new Editor(list.newNote(tagsList.getTag()), list, tagsList);
	}
}