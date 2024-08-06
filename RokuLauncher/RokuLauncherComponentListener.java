package RokuLauncher;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class RokuLauncherComponentListener extends ComponentAdapter{
	
	private RokuLauncherWindow launcherWindow;
	
	public RokuLauncherComponentListener(RokuLauncherWindow rlWindow){
		this.launcherWindow = rlWindow;
	}
	@Override
	public void componentResized(ComponentEvent e) {
		launcherWindow.reSizeDetect();
	}
}
