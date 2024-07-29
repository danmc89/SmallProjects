package RokuLauncher;

import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

public class RokuLauncherWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private FontMetrics fontMetricsButton;
	
	public RokuLauncherWindow(int count, ArrayList<String> listOfFiles)
	{
		setTitle("Roku Launcher");
		setLocation(RokuProperties.WINDOW_LOCATION_X.getPropertiesValueAsInt(), 
				RokuProperties.WINDOW_LOCATION_Y.getPropertiesValueAsInt());
		
		addChannelButtons(listOfFiles);
		this.add(createCloseButton(this, "Close Roku Video"));//add close selection at bottom
		int winWide = windowWidth(listOfFiles);
		//setSize is dependent on button add for "fontMetricsButton" variable
		setSize(winWide, (RokuProperties.BUTTON_HEIGHT.getPropertiesValueAsInt() * count));
		LayoutManager gl = new GridLayout(count+1, 1);//plus 1 for "close roku video option"
		setLayout(gl);
		this.addWindowListener(new WindowAdapter()
		{
		    @Override
		    public void windowClosing(WindowEvent e)
		    {
		      RokuLauncher.closeRokuVideo();
		    }
		});
		setupTrayIcon();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void addChannelButtons(ArrayList<String> listOfFiles)
	{
		JButton b = null;
		for (String s : listOfFiles)
		{
			b = createChannelButton(this, s);
			this.add(b);
		}
		this.fontMetricsButton = b.getFontMetrics(b.getFont()); 		
	}
	
	public JButton createChannelButton(JFrame jf, String title)
	{
		JButton b = createButton(jf, title);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RokuLauncher.launchChannel(
						RokuProperties.ROKU_PATH.getPropertiesValue() + File.separator + b.getName());
			}
		});
		return b;
	}
	
	public JButton createCloseButton(JFrame jf, String title)
	{
		JButton b = createButton(jf, title);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RokuLauncher.closeRokuVideo();
			}
		});
		return b;
	}
	
	public JButton createButton(JFrame jf, String title)
	{
		JButton b = new JButton();
		
		b.setName(title);
		b.setText(titleCreator(title));
		
		return b;
	}
	
	private String titleCreator(String buttonTitle)
	{
		String 
			replstr = "",
			stripStr = RokuProperties.ROKU_CHANNEL_SUFFIX.getPropertiesValue();
		
		Pattern pat = Pattern.compile(stripStr, Pattern.CASE_INSENSITIVE);
		Matcher mat = pat.matcher(buttonTitle);
		return mat.replaceAll(replstr);
	}
	
	private int windowWidth(ArrayList<String> listOfFiles)
	{
		int pad = 100;
		Collections.sort(listOfFiles, new Comparator<String>(){
		    public int compare(String s1, String s2) {
		        return s2.length() - s1.length();
		    }
		});
		String lenStr = titleCreator(listOfFiles.get(0));
		int width = this.fontMetricsButton.stringWidth(lenStr) + pad;
		System.out.println("Longest title character length: " + lenStr.length() + "; calculated width: " + width);
		
		return width > RokuProperties.WINDOW_WIDTH_MIN.getPropertiesValueAsInt()
				? width 
				: RokuProperties.WINDOW_WIDTH_MIN.getPropertiesValueAsInt();
	}
	
	private void setupTrayIcon()
	{
		try {
			PopupMenu trayPopupMenu = new PopupMenu();
			File file = new File(RokuProperties.ICON.getPropertiesValue());//use location from .bat script
			BufferedImage img = ImageIO.read(file);
			
			MenuItem 	
				open = new MenuItem("Open"),
				close = new MenuItem("Close");
			
			open.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(true);
				}
			});
			close.addActionListener(new ActionListener() {
			    @Override
			    public void actionPerformed(ActionEvent e) {
			    	RokuLauncher.closeRokuVideo();
			        System.exit(0);           
			    }
			});
			
			trayPopupMenu.add(open);
			trayPopupMenu.add(close);
			
			TrayIcon trayIcon = new TrayIcon(img, "Roku Launcher", trayPopupMenu);
			trayIcon.setImageAutoSize(true);
			//setup system tray
			SystemTray systemTray = SystemTray.getSystemTray();
			systemTray.add(trayIcon);
			
			this.addWindowStateListener(new WindowStateListener() {
				public void windowStateChanged(WindowEvent e) {
					if(e.getNewState()==ICONIFIED) {
						setVisible(false);
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
