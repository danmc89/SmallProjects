package RokuLauncher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClosePopupMenu implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		RokuLauncher.closeRokuVideo();
		System.exit(0);
	}

}
