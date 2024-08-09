package RokuLauncher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NavigationButtonActionListener implements ActionListener{

	private RokuLauncherWindow rlWindow;
	private Direction direction;
	
	public NavigationButtonActionListener(RokuLauncherWindow rlWindow, Direction direction){
		this.rlWindow = rlWindow;
		this.direction = direction;
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		rlWindow.setNextVideoIndex(rlWindow.getVideosListPos(), direction);
		rlWindow.addChannelButtons();
		rlWindow.paintComponents(rlWindow.getGraphics());
	}

}
