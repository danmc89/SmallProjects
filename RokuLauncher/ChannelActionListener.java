package RokuLauncher;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class ChannelActionListener implements ActionListener{

	private RokuLauncherWindow rlWindow;
	private JButton channelButton;
	private static Color 
		defaultBackgroundColor = new JButton().getBackground(),
		highlightColor = WidgetTextProperties.HIGHLIGHT_COLOR.getPropertyValueAsColor();
	
	public ChannelActionListener(RokuLauncherWindow rlWindow)
	{
		this.rlWindow = rlWindow;
	}
	
	public void setChannelButton(JButton channelButton) {
		this.channelButton = channelButton;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String exe = rlWindow.getVideos().get(rlWindow.getVideosListPos()).getExeName();
		if(exe != null && !exe.equals(""))
		{
			RokuLauncher.executeProcess(exe, rlWindow.getVideos().get(rlWindow.getVideosListPos()).returnVideo(channelButton.getName()));
		}
		else
		{
			RokuLauncher.executeProcess(rlWindow.getVideos().get(rlWindow.getVideosListPos()).returnVideo(channelButton.getName()));
		}
		
		toggleHighLightButton();
	}
	
	public void toggleHighLightButton() {
		WidgetCreator.toggleHighlightButton(rlWindow.getChannelPanel(), 
				rlWindow.getSelectedButton(), 
				channelButton,
				defaultBackgroundColor,
				highlightColor);
		rlWindow.setSelectedButtonAndText(channelButton, channelButton.getText());
	}
	
	public static Color getChannelButtonDefaultColor() {
		return defaultBackgroundColor;
	}

}
