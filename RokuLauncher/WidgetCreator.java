package RokuLauncher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public interface WidgetCreator {

	public static JPanel createNavigationPanel(JButton navPanel)
	{
		JPanel buttonPanel = new JPanel();
		BorderLayout blW = new BorderLayout();
		buttonPanel.setLayout(blW);
		buttonPanel.add(navPanel, BorderLayout.NORTH);
		
		return buttonPanel;
	}
	
	public static JButton createButton(String title, String filter)
	{
		JButton b = new JButton();
		
		b.setName(title);
		b.setText(titleCreator(title, filter));
		
		return b;
	}
	
	public static JButton createButton(String title)
	{
		return createButton(title, "");
	}
	
	public static JButton createChannelButton(String title, ChannelActionListener cListener, RokuLauncherWindow rlWindow)
	{
		String filter = rlWindow.getVideos().get(rlWindow.getVideosListPos()).getVideoStripFilter();
		JButton b = WidgetCreator.createButton(title, filter);
		b.addActionListener(cListener);
		return b;
	}
	
	public static JButton createCloseButton(RokuLauncherWindow rlWindow, String title)
	{
		JButton b = WidgetCreator.createButton(title);
		b.addActionListener(new CloseActionListener(rlWindow));
		return b;
	}
	
	public static String titleCreator(String buttonTitle, String stripStr)
	{
		String replstr = "";
		if(stripStr == null || stripStr.equals(""))
			return buttonTitle;
		Pattern pat = Pattern.compile(stripStr, Pattern.CASE_INSENSITIVE);
		Matcher mat = pat.matcher(buttonTitle);
		return mat.replaceAll(replstr);
	}
	
	public static void toggleHighlightButton(Component c, 
			JButton selButton, 
			JButton curButton, 
			Color defaultBackgroundColor, 
			Color highlightColor) {
		if(selButton != null)
			selButton.setBackground(defaultBackgroundColor);
		curButton.setBackground(highlightColor);
		selButton = curButton;
		
		c.repaint();
	}
	
	public static void setupTaskbar(JFrame frame)
	{
		File file = new File(LauncherProperties.ICON.getPropertiesValue());
		BufferedImage img;
		try {
			img = ImageIO.read(file);
			frame.setIconImage(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static TrayIcon setupTrayIcon(JFrame frame, String selectedTitle, PopupMenu trayPopupMenu)
	{
		TrayIcon retIcon = null;
		try {
			File file = new File(LauncherProperties.ICON.getPropertiesValue());//use location from .bat script
			BufferedImage img = ImageIO.read(file);
			
			final TrayIcon trayIcon = new TrayIcon(img, selectedTitle, trayPopupMenu);
			trayIcon.setImageAutoSize(true);
			
			SystemTray systemTray = SystemTray.getSystemTray();
			systemTray.add(trayIcon);
			retIcon = trayIcon;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return retIcon;
	}
	
	public static PopupMenu buildPopupMenu(MenuItem ... menuItems)
	{
		PopupMenu trayPopupMenu = new PopupMenu();
		
		for (MenuItem mi : menuItems)
			trayPopupMenu.add(mi);
		
		return trayPopupMenu;
	}
	
	public static MenuItem buildMenuItem(String labelText, ActionListener aListener)
	{
		MenuItem menuItem = new MenuItem(labelText);
		menuItem.addActionListener(aListener);
		return menuItem;
	}
	
	public static void destroySystemTray(TrayIcon trayIcon)
	{
		SystemTray systemTray = SystemTray.getSystemTray();
		systemTray.remove(trayIcon);
	}
	
	public static JButton findButton(Component [] buttons, String text)
	{
		if (text == null)
			return null;
		JButton retB = null;
		
		for (Component c : buttons)
		{
			if (c == null)
				continue;
			
			if (c instanceof JButton)
			{
				JButton b = (JButton) c;
				String bText = b.getText();
				if(bText.equals(text)){
					retB = b;
					break;
				}
			}
		}
		return retB;
	}
	
}
