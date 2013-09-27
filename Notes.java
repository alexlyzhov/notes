// import javax.swing.*;
// import javax.swing.event.*;
// import java.awt.*;
// import java.awt.event.*;
// import javax.swing.border.*;

import com.trolltech.qt.gui.*; //exact classes
import java.util.*;

public class Notes extends QWidget {
	// private JList<Note> notesList;
	private Vector<Note> notesData;
	public NotesDatabase notesDatabase;

	public static void main(String args[]) {
		QApplication.initialize(args);
		QApplication.setQuitOnLastWindowClosed(true);
		Notes notes = new Notes();
		QApplication.exec();
		new Keys(notes);
	}

	public Notes() {
		notesDatabase = new NotesDatabase();
		setWindowTitle("Notes");
		setWindowIcon(new QIcon("ico/sun.png"));
		QDesktopWidget screen = new QDesktopWidget();
		move(200, (int) (screen.height() * 0.2));
		resize(300, (int) (screen.height() * 0.6));
		// initNewButton();
		// initList();
		show();
	}

	// private void initNewButton() {
	// 	QPushButton button = new JButton("New note", this);
	// 	//Main.getImageIcon(Main.EDIT_ICO, 16, 16)
	// 	button.setGeometry(30, 30, 75, 30);
	// 	button.clicked.connect() //to createNote()
	// }

	// private void initList() {
	// 	notesList = new JList<Note>();
	// 	notesData = new Vector<Note>();

	// 	class NoteListMouseListener extends MouseAdapter {
	// 		public void mouseClicked(MouseEvent e) {
	// 			Point point = e.getPoint();
	// 			int index = notesList.locationToIndex(e.getPoint());
	// 			if(notesList.getCellBounds(index, index).contains(point)) {
	// 				Note element = (Note) notesList.getModel().getElementAt(index);
	// 				switch(e.getButton()) {
	// 					case MouseEvent.BUTTON1:
	// 						if(!element.editing) newEditor(element);
	// 						break;
	// 					case MouseEvent.BUTTON2:
	// 						if(!element.editing) {
	// 							notesDatabase.removeNote(element); //only if it is available to remove
	// 							updateList();
	// 						}
	// 						break;
	// 				}
	// 			}
	// 		}
	// 	}

	// 	class NoteCellRenderer<Note> extends JLabel implements ListCellRenderer<Note> {
	// 		public Component getListCellRendererComponent(JList<? extends Note> list, Note value, int index, boolean isSelected, boolean cellHasFocus) {
	// 			String text = value.toString();
	// 			if(text.equals("")) text = "Nameless";
	// 			setText(text);
	// 			if (isSelected) setBackground(list.getSelectionBackground());
	// 			else setBackground(list.getBackground());
	// 			setFont(list.getFont());
	// 			setOpaque(true);
	// 			return this;
	// 		}
	// 	}

	// 	notesList.addMouseListener(new NoteListMouseListener());
	// 	notesList.setCellRenderer(new NoteCellRenderer<Note>());

	// 	updateList();
	// 	add(notesList, BorderLayout.CENTER);

	// 	JScrollPane scrollPane = new JScrollPane(notesList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	// 	add(scrollPane, BorderLayout.CENTER);
	// }

	public void toggleVisible() {
		// setVisible(!isVisible());
		System.out.println("toggleVisible(): not yet supported");
	}

	public void createNote() {
		newEditor(null);
	}

	private void newEditor(Note noteParam) {
		// new Editor(this, noteParam);
		System.out.println("Editor class is still in transition to qt");
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

		// notesList.setListData(notesDatabase.getNotesList()); //consider the problem with Note duplicates
	}

	private void closeDatabases() {
		notesDatabase.closeQueue();
	}

	public void cleanUp() {
		closeDatabases();
	}
}