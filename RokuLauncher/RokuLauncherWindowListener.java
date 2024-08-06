package RokuLauncher;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RokuLauncherWindowListener extends WindowAdapter 
{
	private RokuLauncherWindow launcherWindow = null;

	public RokuLauncherWindowListener(RokuLauncherWindow rlWindow)
	{
		this.launcherWindow = rlWindow;
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		launcherWindow.initialSizeDetect();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		RokuLauncher.closeRokuVideo();
	}
}
