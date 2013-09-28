import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class Notes extends QWidget {
	private NotesData notesData;
	private Keys keys;

	private QListView list;
	private QVBoxLayout layout;

	public static void main(String args[]) {
		QApplication.initialize(args);
		new Notes();
		QApplication.exec();
	}

	private Notes() {
		notesData = new NotesData();
		QApplication.setQuitOnLastWindowClosed(true);
		init();
		keys = new Keys(this);
	}

	public void init() {
		setWindowTitle("Notes");
		setWindowIcon(new QIcon("ico/sun.png"));

		QDesktopWidget screen = new QDesktopWidget(); //replace it with system position/size determination
		move(200, (int) (screen.height() * 0.2));
		resize(300, (int) (screen.height() * 0.6));

		QApplication.instance().lastWindowClosed.connect(this, "exit()");
		layout = new QVBoxLayout(this);
		initNewButton();
		initList();

		toggleVisible();
	}

	public void exit() {
		notesData.exit();
		keys.cleanUp();
	}

	private void initNewButton() {
		QPushButton button = new QPushButton(new QIcon("ico/edit.png"), "New note", this);
		button.setFixedHeight(30);
		layout.addWidget(button);
		button.clicked.connect(this, "createNote()");
	}

	private void initList() {
		list = new QListView(this);
		list.setModel(notesData);
		// list.addScrollBarWidget(new QScrollBar(), Qt.AlignmentFlag.AlignTop);
		list.activated.connect(this, "openNote(QModelIndex)");
		// list.pressed.connect(this, "checkDelete()");
		layout.addWidget(list);
	}

	// private void checkDelete() {
	// 	if(QApplication.mouseButtons() & Qt.MouseButton.MidButton) {
	// 		Note note = notesData.get(index);
	// 		if(!note.editing) {
	// 			notesData.remove(note);
	// 			notesData.update();
	// 		}
	// 	}
	// }

	public void toggleVisible() {
		setVisible(!isVisible());
	}

	public void createNote() {
		newEditor(null);
	}

	public void openNote(QModelIndex index) {
		Note note = notesData.get(index);
		if(!note.editing) newEditor(notesData.get(index));
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
}