import org.gnome.gtk.*;
import org.gnome.gdk.Pixbuf;

public class Editor extends Window {
	private Note note;
	private NotesList notesList;
	private Pixbuf edit;
	private String initialName, initialContent;
	// JTextField name;
	// JTextPane content;

	public Editor(Note noteParam, NotesList listParam) {
		note = noteParam;
		notesList = listParam;
		saveInitialValues();
		note.startEditing();

		setTitle("Editor"); //later do it with editing note title
		try {
			edit = new Pixbuf("ico/edit.png");
		} catch(Exception ex) {ex.printStackTrace();}

		// addWindowListener(new WindowAdapter() {
		// 	public void windowClosing(WindowEvent e) {
		// 		save();
		// 		finishEditing();
		// 	}
		// });
		// setLayout(new BorderLayout());
		// initName();
		// initContent();
		// pack();
		// setLocationRelativeTo(null);
		// setVisible(true);
		// if(note.fresh) name.requestFocusInWindow();
		// else content.requestFocusInWindow();
	}

	private void saveInitialValues() {
		initialName = note.getName();
		initialContent = note.getContent();
	}

	// private void initName() {
	// 	name = new JTextField(note.name);
	// 	name.setFont(new Font("Tahoma", Font.PLAIN, 18));
	// 	add(name, BorderLayout.PAGE_START);
	// 	name.addKeyListener(new KeyListener() {
	// 		public void keyPressed(KeyEvent e) {}
	// 		public void keyTyped(KeyEvent e) {}
	// 		public void keyReleased(KeyEvent e) {
	// 			int keyCode = e.getKeyCode();
	// 			if(keyCode == KeyEvent.VK_ENTER) {
	// 				content.requestFocusInWindow();
	// 			}
	// 		}
	// 	});
	// }

	// private void initContent() {
	// 	content = new JTextPane(); //view JTextPane class; set a font and other styles
	// 	content.setPreferredSize(new Dimension(700, 400));
	// 	content.setText(note.content);
	// 	content.setCaretPosition(0);
	// 	content.setFont(new Font("Tahoma", Font.PLAIN, 14));
	// 	add(content, BorderLayout.CENTER);

	// 	JScrollPane pane = new JScrollPane(content);
	// 	add(pane, BorderLayout.CENTER);
	// }

	// private boolean changed() {
	// 	if(!initialName.equals(note.name)) return true;
	// 	if(!initialContent.equals(note.content)) return true;
	// 	return false;
	// }

	// private void writeName() {
	// 	note.name = name.getText();
	// }

	// private void writeContent() {
	// 	note.content = content.getText();
	// }

	// private void save() {
	// 	writeName(); //shield the value
	// 	writeContent(); //this also
	// 	if(!changed()) return;
	// 	if(note.fresh) {
	// 		notes.notesDatabase.newNote(note);
	// 		note.fresh = false;
	// 	} else {
	// 		notes.notesDatabase.updateNote(note);
	// 	}
	// 	notes.updateList();
	// }
}