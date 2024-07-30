package RokuLauncher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Launcher window
 * Since a GUI using static values often as it's single instance
 */

public class RokuLauncherWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private FontMetrics fontMetricsButton;
	private JButton selectedButton;
	private JPanel innerPanel = new JPanel();
	private static final Color HIGHLIGHT_COLOR = new Color(238, 238, 238);
	private static final ArrayList<Videos> VIDEO_PATHS_AND_TITLE = new ArrayList<Videos>();
	private static int videoPos = 0;
	
	public RokuLauncherWindow()
	{
		setupVideoLists();
		ArrayList<String> listOfOptions = VIDEO_PATHS_AND_TITLE.get(0).getVideos();
		
		setTitle(RokuProperties.APPLICATION_TITLE.getPropertiesValue());
		setLocation(RokuProperties.WINDOW_LOCATION_X.getPropertiesValueAsInt(), 
				RokuProperties.WINDOW_LOCATION_Y.getPropertiesValueAsInt());
		
		LayoutManager gl = new GridLayout(listOfOptions.size()+2, 1);//plus 1 for "close roku video option"
		BorderLayout bl = new BorderLayout();
		innerPanel.setLayout(gl);
		this.setLayout(bl);
		this.add(innerPanel, BorderLayout.CENTER);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				RokuLauncher.closeRokuVideo();
			}
		});
		if(RokuProperties.SYSTEM_TRAY.getPropertiesValue().toLowerCase().equals("true")) {
			setupTrayIcon();
		}
		
		addChannelButtons(listOfOptions);
		//add close selection at bottom
		createNavigationButtons();
		
		int winWide = windowWidth(videoPos) + 100; //+100 for nav buttons
		//setSize is dependent on button add for "fontMetricsButton" variable
		setSize(winWide, (RokuProperties.BUTTON_HEIGHT.getPropertiesValueAsInt() * listOfOptions.size()));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private static void setupVideoLists()
	{
		VIDEO_PATHS_AND_TITLE.add(
				new Videos(PropertiesFileLoader.getOSFileList(
						RokuProperties.ROKU_PATH.getPropertiesValue(), 
							RokuProperties.ROKU_CHANNEL_FILETYPE.getPropertiesValue()),
						RokuProperties.ROKU_PATH.getPropertiesValue(),
						RokuProperties.ROKU_TITLE.getPropertiesValue(),
						RokuProperties.ROKU_CHANNEL_SUFFIX.getPropertiesValue()));
		VIDEO_PATHS_AND_TITLE.add(
				new Videos(PropertiesFileLoader.getOSFileList(
						RokuProperties.YOUTUBE_PATH.getPropertiesValue(), 
							RokuProperties.YOUTUBE_CHANNEL_FILETYPE.getPropertiesValue()),
						RokuProperties.YOUTUBE_PATH.getPropertiesValue(),
						RokuProperties.YOUTUBE_TITLE.getPropertiesValue(),
						RokuProperties.YOUTUBE_CHANNEL_SUFFIX.getPropertiesValue()));
	}
	
	private static void setNextVideoIndex(int curPosition, Direction direction)
	{
		int 
			indexEnd = VIDEO_PATHS_AND_TITLE.size()-1,
			indexReturn = 0;
		
		switch (direction) {
			case FORWARD:
				if(indexEnd < curPosition + 1)
					indexReturn = 0;
				else
					indexReturn = curPosition + 1;
				break;
				
			case BACKWARD:
				if(0 > curPosition - 1)
					indexReturn = indexEnd;
				else
					indexReturn = curPosition - 1;
				break;
		}
		videoPos = indexReturn;
	}
	
	private void addChannelButtons(ArrayList<String> listOfFiles)
	{
		JButton b = null;
		JTextField tf = new JTextField(VIDEO_PATHS_AND_TITLE.get(videoPos).getTitle());{
			tf.setEditable(false);
			tf.setHorizontalAlignment(JTextField.CENTER);
			tf.setBackground(Color.GRAY);
			tf.setForeground(HIGHLIGHT_COLOR);
			innerPanel.add(tf, BorderLayout.CENTER);
		}
		for (String s : listOfFiles)
		{
			b = createChannelButton(s);
			innerPanel.add(b);
		}
		this.fontMetricsButton = b.getFontMetrics(b.getFont());
		innerPanel.add(createCloseButton(RokuProperties.CLOSE_VIDEO_TEXT.getPropertiesValue()));
	}
	
	private void clearChannelButtons()
	{
		innerPanel.removeAll();
	}
	
	public JButton createChannelButton(String title)
	{
		JButton b = createButton(title);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RokuLauncher.launchChannel(
						VIDEO_PATHS_AND_TITLE.get(videoPos).getPath() + File.separator + b.getName());
				toggleHighlightButton();
				repaint();
			}
			public void toggleHighlightButton() {
				Color color = b.getBackground();
				if(selectedButton != null)
					selectedButton.setBackground(color);
				b.setBackground(HIGHLIGHT_COLOR);
				selectedButton = b;
			}
		});
		return b;
	}
	
	public JButton createCloseButton(String title)
	{
		JButton b = createButton(title);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selectedButton != null)
					selectedButton.setBackground(b.getBackground());
				RokuLauncher.closeRokuVideo();
				repaint();
			}
		});
		return b;
	}
	
	public JButton createButton(String title)
	{
		String filter = VIDEO_PATHS_AND_TITLE.get(videoPos).getVideoStripFilter();
		JButton b = new JButton();
		
		b.setName(title);
		b.setText(titleCreator(title, filter));
		
		return b;
	}
	
	public void createNavigationButtons()
	{
		JPanel 
			jpW = new JPanel(),
			jpE = new JPanel();
		BorderLayout
			blW = new BorderLayout(),
			blE = new BorderLayout();
		JButton 
			navW = createButton("<"),
			navE = createButton(">");
		
		jpW.setLayout(blW);
		jpE.setLayout(blE);
		navW.setMaximumSize(new Dimension(100, 100));
		navW.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearChannelButtons();
				setNextVideoIndex(videoPos, Direction.BACKWARD);
				addChannelButtons(VIDEO_PATHS_AND_TITLE.get(videoPos).getVideos());
				paintComponents(getGraphics());
			}
		});
		navE.setMaximumSize(new Dimension(50, 100));
		navE.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearChannelButtons();
				setNextVideoIndex(videoPos, Direction.FORWARD);
				addChannelButtons(VIDEO_PATHS_AND_TITLE.get(videoPos).getVideos());
				paintComponents(getGraphics());
			}
		});
		this.add(jpW, BorderLayout.WEST);
		this.add(jpE, BorderLayout.EAST);
		jpW.add(navW, BorderLayout.NORTH);
		jpE.add(navE, BorderLayout.NORTH);
	}
	
	private String titleCreator(String buttonTitle, String stripStr)
	{
		String 
			replstr = "";
		
		Pattern pat = Pattern.compile(stripStr, Pattern.CASE_INSENSITIVE);
		Matcher mat = pat.matcher(buttonTitle);
		return mat.replaceAll(replstr);
	}
	
	private int windowWidth(int position)
	{
		ArrayList<String> listOfFiles = VIDEO_PATHS_AND_TITLE.get(0).getVideos();
		@SuppressWarnings("unchecked")
		ArrayList<String> cloneList = (ArrayList<String>) listOfFiles.clone();
		String filter = VIDEO_PATHS_AND_TITLE.get(0).getVideoStripFilter();
		int pad = 100;
		
		Collections.sort(cloneList, new Comparator<String>(){
		    public int compare(String s1, String s2) {
		        return s2.length() - s1.length();
		    }
		});
		String lenStr = titleCreator(listOfFiles.get(0), filter);
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
			trayPopupMenu.add(open);
			close.addActionListener(new ActionListener() {
			    @Override
			    public void actionPerformed(ActionEvent e) {
			    	RokuLauncher.closeRokuVideo();
			        System.exit(0);           
			    }
			});
			trayPopupMenu.add(close);
			
			
			SystemTray systemTray = SystemTray.getSystemTray();
			TrayIcon trayIcon = new TrayIcon(img, "Roku Launcher", trayPopupMenu);
			
			trayIcon.setImageAutoSize(true);
			systemTray.add(trayIcon);
			
			this.addWindowStateListener(new WindowStateListener() {
				public void windowStateChanged(WindowEvent e) {
					if(e.getNewState()==ICONIFIED) {
						setVisible(false);
						setExtendedState(NORMAL);
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
