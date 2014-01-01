import org.gnome.gtk.*;

public class ScrolledList extends ScrolledWindow {
	public ScrolledList(ListTree tree) {
		setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
		getVAdjustment().connect(new Adjustment.Changed() {
			public void onChanged(Adjustment source) {
				source.setValue(0);
			}
		});
		add(tree);
	} 
}