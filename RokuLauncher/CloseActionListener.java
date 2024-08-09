package RokuLauncher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CloseActionListener implements ActionListener{
	
	private RokuLauncherWindow rlWindow;
	
	public CloseActionListener(RokuLauncherWindow rlWindow)
	{
		this.rlWindow = rlWindow;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(rlWindow.getSelectedButton() != null)
			rlWindow.getSelectedButton().setBackground(ChannelActionListener.getChannelButtonDefaultColor());
		RokuLauncher.closeRokuVideo();
		rlWindow.setSelectedButtonAndText(null, null);
		rlWindow.getChannelPanel().repaint();
	}
	
}
