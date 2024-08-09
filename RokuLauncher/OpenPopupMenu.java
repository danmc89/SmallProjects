package RokuLauncher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OpenPopupMenu implements ActionListener{
	
	private RokuLauncherWindow rlWindow;
	
	public OpenPopupMenu(RokuLauncherWindow rlWindow)
	{
		this.rlWindow = rlWindow;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		rlWindow.setVisible(true);
		WidgetCreator.destroySystemTray(rlWindow.getTrayIcon());
	}

}
