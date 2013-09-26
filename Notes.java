import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;

public class Notes extends JFrame {
	private JList<Note> notesList;
	private Vector<Note> notesData;
	public NotesDatabase notesDatabase;

	public Notes() {
		notesDatabase = new NotesDatabase();
		String title = "Notes";
		setTitle(title);
		setIconImage(new ImageIcon(Main.SUN_ICO).getImage());
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
			}
		});
		setDefaultCloseOperation(HIDE_ON_CLOSE); //change it to HIDE_ON_CLOSE as soon as I will develop hotkeys and tray
		setNewBounds();
		setLayout(new BorderLayout());
		initNewButton();
		initList();
		// setVisible(true);
	}

	private void setNewBounds() { //it does not work here
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(0, (int) (screen.getHeight() * 0.2));
		setSize(new Dimension(500, (int) (screen.getHeight() * 0.6)));
	}

	private void initNewButton() {
		JButton newButton = new JButton("New note", Main.getImageIcon(Main.EDIT_ICO, 16, 16));
		newButton.setFocusPainted(false);
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNote();
			}
		});
		add(newButton, BorderLayout.PAGE_START);
	}

	public void createNote() {
		newEditor(null);
	}

	private void initList() {
		notesList = new JList<Note>();
		notesData = new Vector<Note>();

		class NoteListMouseListener extends MouseAdapter {
			public void mouseClicked(MouseEvent e) {
				Point point = e.getPoint();
				int index = notesList.locationToIndex(e.getPoint());
				if(notesList.getCellBounds(index, index).contains(point)) {
					Note element = (Note) notesList.getModel().getElementAt(index);
					switch(e.getButton()) {
						case MouseEvent.BUTTON1:
							if(!element.editing) newEditor(element);
							break;
						case MouseEvent.BUTTON2:
							if(!element.editing) {
								notesDatabase.removeNote(element); //only if it is available to remove
								updateList();
							}
							break;
					}
				}
			}
		}

		class NoteCellRenderer<Note> extends JLabel implements ListCellRenderer<Note> {
			public Component getListCellRendererComponent(JList<? extends Note> list, Note value, int index, boolean isSelected, boolean cellHasFocus) {
				String text = value.toString();
				if(text.equals("")) text = "Nameless";
				setText(text);
				if (isSelected) setBackground(list.getSelectionBackground());
				else setBackground(list.getBackground());
				setFont(list.getFont());
				setOpaque(true);
				return this;
			}
		}

		notesList.addMouseListener(new NoteListMouseListener());
		notesList.setCellRenderer(new NoteCellRenderer<Note>());

		updateList();
		add(notesList, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane(notesList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
	}

	private void newEditor(Note noteParam) {
		new Editor(this, noteParam);
	}

	public void updateList() {
		// Note[] newNotesData = notesDatabase.getNotesList();
		// Vector<Note> notesDataClone = null;
		// try {
		// 	notesDataClone = (Vector<Note>) notesData.clone(); //get rid of clone method, it's unsafe
		// } catch(ClassCastException ex) {ex.printStackTrace();}
		// int index;
		// for(Note newNote: newNotesData) {
		// 	index = newNote.findIndex(notesData);
		// 	if(index != -1) {
		// 		System.out.println(newNote.name + " index is not -1");
		// 		Note noteToUpdate = notesData.get(index);
		// 		notesDataClone.remove(notesDataClone.indexOf(noteToUpdate));
		// 		noteToUpdate.update(newNote);
		// 	} else {
		// 		System.out.println(newNote.name + " index is -1");
		// 		notesData.add(newNote);
		// 	}
		// }
		// for(Note oldNote: notesDataClone) {
		// 	notesData.remove(notesData.indexOf(oldNote));
		// }
		// notesList.setListData(notesData); //one time isn't enough, huh?
		notesList.setListData(notesDatabase.getNotesList()); //consider the problem with Note duplicates
	}

	private void closeDatabases() {
		notesDatabase.closeQueue();
	}

	public void cleanUp() {
		closeDatabases();
	}
}