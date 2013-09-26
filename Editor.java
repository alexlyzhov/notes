import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

public class Editor extends JFrame {
	Notes notes;
	Note note;
	JTextField name;
	JTextPane content;
	String initialName, initialContent;

	public Editor(Notes notes, Note noteParam) {
		this.notes = notes;
		this.note = noteParam;
		if(note == null) newNote();
		else saveInitialValues();
		startEditing();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(Main.getImageIcon(Main.EDIT_ICO).getImage());
		setTitle("Editor");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				save();
				finishEditing();
			}
		});

		setLayout(new BorderLayout());
		initName();
		initContent();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		if(note.fresh) name.requestFocusInWindow();
		else content.requestFocusInWindow();
	}

	private void initName() {
		name = new JTextField(note.name);
		name.setFont(new Font("Tahoma", Font.PLAIN, 18));
		add(name, BorderLayout.PAGE_START);
		name.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if(keyCode == KeyEvent.VK_ENTER) {
					content.requestFocusInWindow();
				}
			}
		});
	}

	private void initContent() {
		content = new JTextPane(); //view JTextPane class; set a font and other styles
		content.setPreferredSize(new Dimension(700, 400));
		content.setText(note.content);
		content.setCaretPosition(0);
		content.setFont(new Font("Tahoma", Font.PLAIN, 14));
		add(content, BorderLayout.CENTER);

		JScrollPane pane = new JScrollPane(content);
		add(pane, BorderLayout.CENTER);
	}

	private void newNote() {
		note = new Note("", "");
		initialName = initialContent = "";
		note.fresh = true;
	}

	private void saveInitialValues() {
		initialName = note.name;
		initialContent = note.content;
	}

	private boolean changed() {
		boolean result = false;
		if(!initialName.equals(note.name)) result = true;
		if(!initialContent.equals(note.content)) result = true;
		return result;
	}

	private void writeName() {
		note.name = name.getText();
	}

	private void writeContent() {
		note.content = content.getText();
	}

	private void save() {
		writeName(); //shield the value
		writeContent(); //this also
		if(!changed()) return;
		if(note.fresh) {
			notes.notesDatabase.newNote(note);
			note.fresh = false;
		} else {
			notes.notesDatabase.updateNote(note);
		}
		notes.updateList();
	}

	private void startEditing() {
		note.editing = true;
	}

	private void finishEditing() {
		note.editing = false;
	}
}