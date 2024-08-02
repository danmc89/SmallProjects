package RokuLauncher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Launcher window
 * Since a GUI using static values often as it's single instance
 */
public class RokuLauncherWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static final Color HIGHLIGHT_COLOR = new Color(238, 238, 238);
	private static final ArrayList<Videos> VIDEO_PATHS_AND_TITLE = new ArrayList<Videos>();
	private static final int //TODO cleanup calculation here
		NAV_BUTTON_WIDTH = 50, 
		NAV_BUTTON_HEIGHT = 100,
		SCROLL_BAR_WIDTH = 25;
	private static final String 
		NAV_BUTTON_WEST = "<",
		NAV_BUTTON_EAST = ">",
		SYSTEM_TRAY_OPEN_OPTION = "Open",
		SYSTEM_TRAY_CLOSE_OPTION = "Close",
		SYSTEM_TRAY_LABEL = "Video Launcher",
		MENU_OPTION_FILE = "File",
		MENU_OPTION_RELOAD ="Reload",
		MENU_OPTION_MIN_TRAY = "Minimize to System Tray";
	
	private static JButton selectedButton;
	private static String selectedName;
	private static int videoPos = 0;
	
	private FontMetrics fontMetricsButton;
	private JPanel 
		innerPanel = new JPanel(),
		innerPanel2 = new JPanel();//scrollbar crap
	private JScrollPane scrPane = null;
	private TrayIcon launcherTrayIcon = null;
	
	public RokuLauncherWindow()
	{
		addMenuButtons();
		setupVideoLists();
		ArrayList<String> listOfOptions = VIDEO_PATHS_AND_TITLE.get(0).getVideos();
		
		setTitle(RokuProperties.APPLICATION_TITLE.getPropertiesValue());
		setLocation(RokuProperties.WINDOW_LOCATION_X.getPropertiesValueAsInt(), 
				RokuProperties.WINDOW_LOCATION_Y.getPropertiesValueAsInt());
		
		BorderLayout bl = new BorderLayout();
		scrPane = new JScrollPane(innerPanel);
		
		BorderLayout bl2 = new BorderLayout();
		innerPanel2.setLayout(bl2);
		innerPanel2.add(scrPane, BorderLayout.NORTH);
		this.setLayout(bl);
		this.add(innerPanel2, BorderLayout.CENTER);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				RokuLauncher.closeRokuVideo();
			}
		});
		
		addChannelButtons(listOfOptions);
		//add close selection at bottom
		createNavigationButtons();
		
		int winWide = windowWidth() + (NAV_BUTTON_WIDTH * 2) + SCROLL_BAR_WIDTH; //+100 for nav buttons + 25 scrollbar
		//setSize is dependent on button add for "fontMetricsButton" variable
		this.setSize(winWide, (RokuProperties.BUTTON_HEIGHT.getPropertiesValueAsInt() * listOfOptions.size()));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private JButton findButton(String text)
	{
		if (text == null)
			return null;
		JButton b = null;
		for (Component c : innerPanel.getComponents())
		{
			if(c.getName() != null && c.getName().equals(text)){
				b = (JButton) c;
				break;
			}
		}
		return b;
	}
	
	private void addMenuButtons()
	{
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem 
			jmReload,
			jmSystemTray;
		
		//Create the menu bar.
		menuBar = new JMenuBar();
		menu = new JMenu(MENU_OPTION_FILE);
		jmReload = new JMenuItem(MENU_OPTION_RELOAD);
		jmReload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reloadPropertiesFile();
				LoggingMessages.printOut(MENU_OPTION_RELOAD);
			}
		});
		menu.add(jmReload);//end "reload" option add
		
		if(RokuProperties.SYSTEM_TRAY.getPropertiesValue().toLowerCase().equals("true"))
		{
			setupTaskbar();
			jmSystemTray = new JMenuItem(MENU_OPTION_MIN_TRAY);
			jmSystemTray.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setupTrayIcon();
					setVisible(false);
					setExtendedState(NORMAL);
					LoggingMessages.printOut(MENU_OPTION_MIN_TRAY);
				}
			});
			menu.add(jmSystemTray);
		}//end "minimize to system tray" add
		
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
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
		JButton sel = findButton(selectedName);
		if(sel != null)
		{
			toggleHighlightButton(innerPanel, selectedButton, sel);
		}
		updateInnerPanelSize(listOfFiles);
	}
	
	private void clearChannelButtons()
	{
		innerPanel.removeAll();
		innerPanel2.removeAll();
		scrPane.removeAll();
		this.remove(innerPanel2);
	}
	
	private JButton createChannelButton(String title)
	{
		JButton b = createButton(title);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String exe = VIDEO_PATHS_AND_TITLE.get(videoPos).getExeName();
				if(exe != null && !exe.equals(""))
				{
					RokuLauncher.executeProcess(exe, VIDEO_PATHS_AND_TITLE.get(videoPos).returnVideo(b.getName()));
					toggleHighlightButton(innerPanel, selectedButton, b);
				}
				else
				{
					RokuLauncher.executeProcess(VIDEO_PATHS_AND_TITLE.get(videoPos).returnVideo(b.getName()));
					toggleHighlightButton(innerPanel, selectedButton, b);
				}
			}
		});
		return b;
	}
	
	private JButton createCloseButton(String title)
	{
		JButton b = createButton(title);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selectedButton != null)
					selectedButton.setBackground(b.getBackground());
				RokuLauncher.closeRokuVideo();
				selectedButton = null;
				selectedName = null;
				repaint();
			}
		});
		return b;
	}
	
	private JButton createButton(String title)
	{
		String filter = VIDEO_PATHS_AND_TITLE.get(videoPos).getVideoStripFilter();
		JButton b = new JButton();
		
		b.setName(title);
		b.setText(titleCreator(title, filter));
		
		return b;
	}
	
	private void createNavigationButtons()
	{
		JPanel 
			jpW = new JPanel(),
			jpE = new JPanel();
		BorderLayout
			blW = new BorderLayout(),
			blE = new BorderLayout();
		JButton 
			navW = createButton(NAV_BUTTON_WEST),
			navE = createButton(NAV_BUTTON_EAST);
		
		jpW.setLayout(blW);
		jpE.setLayout(blE);
		navW.setMaximumSize(new Dimension(NAV_BUTTON_WIDTH, NAV_BUTTON_HEIGHT));
		navW.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearChannelButtons();
				setNextVideoIndex(videoPos, Direction.BACKWARD);
				addChannelButtons(VIDEO_PATHS_AND_TITLE.get(videoPos).getVideos());
				paintComponents(getGraphics());
			}
		});
		navE.setMaximumSize(new Dimension(NAV_BUTTON_WIDTH, NAV_BUTTON_HEIGHT));
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
		String replstr = "";
		if(stripStr == null || stripStr.equals(""))
			return buttonTitle;
		Pattern pat = Pattern.compile(stripStr, Pattern.CASE_INSENSITIVE);
		Matcher mat = pat.matcher(buttonTitle);
		return mat.replaceAll(replstr);
	}
	
	private int windowWidth()
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
		LoggingMessages.printOut("Longest title character length: " + lenStr.length() + "; calculated width: " + width);
		
		return width > RokuProperties.WINDOW_WIDTH_MIN.getPropertiesValueAsInt()
				? width 
				: RokuProperties.WINDOW_WIDTH_MIN.getPropertiesValueAsInt();
	}
	
	private void updateInnerPanelSize(ArrayList<String> listOfOptions)
	{
		LayoutManager gl = new GridLayout(listOfOptions.size()+2, 1);//plus 2 for "close roku video option" and title
		innerPanel.setLayout(gl);
		
		scrPane = new JScrollPane(innerPanel);
		if(listOfOptions.size() < RokuProperties.MAX_NUMBER_OPTIONS.getPropertiesValueAsInt()) {
			innerPanel2.add(scrPane, BorderLayout.NORTH);
		}
		else {
			//center for scrolling...
			innerPanel2.add(scrPane, BorderLayout.CENTER);
		}
		this.add(innerPanel2, BorderLayout.CENTER);
	}
	
	private void reloadPropertiesFile()
	{
		VIDEO_PATHS_AND_TITLE.clear();
		clearChannelButtons();
		setupVideoLists();
		PropertiesFileLoader.reloadLauncherProperties();
		addChannelButtons(VIDEO_PATHS_AND_TITLE.get(videoPos).getVideos());
		paintComponents(getGraphics());
	}
	private void setupTrayIcon()
	{
		try {
			PopupMenu trayPopupMenu = new PopupMenu();
			File file = new File(RokuProperties.ICON.getPropertiesValue());//use location from .bat script
			BufferedImage img = ImageIO.read(file);
			MenuItem 	
				open = new MenuItem(SYSTEM_TRAY_OPEN_OPTION),
				close = new MenuItem(SYSTEM_TRAY_CLOSE_OPTION);
			
			open.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(true);
					destroySystemTray();
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
			launcherTrayIcon = new TrayIcon(img, SYSTEM_TRAY_LABEL, trayPopupMenu);
			
			launcherTrayIcon.setImageAutoSize(true);
			systemTray.add(launcherTrayIcon);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void destroySystemTray()
	{
		SystemTray systemTray = SystemTray.getSystemTray();
		systemTray.remove(launcherTrayIcon);
	}
	
	private void setupTaskbar()
	{
		File file = new File(RokuProperties.ICON.getPropertiesValue());
		BufferedImage img;
		try {
			img = ImageIO.read(file);
			this.setIconImage(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void setupVideoLists()
	{
		ArrayList<String> extList = ExtendableProperties.getExtendedList();
		for(String k : extList)
		{
			LoggingMessages.printOut(k);
			VIDEO_PATHS_AND_TITLE.add(
				new Videos(PropertiesFileLoader.getOSFileList(
						ExtendableProperties._PATH.getPropertiesValue(k),
						ExtendableProperties._CHANNEL_FILETYPE.getPropertiesValue(k)),
					ExtendableProperties._PATH.getPropertiesValue(k),
					ExtendableProperties._TITLE.getPropertiesValue(k),
					ExtendableProperties._CHANNEL_SUFFIX.getPropertiesValue(k),
					ExtendableProperties._CHANNEL_FILETYPE.getPropertiesValue(k),
					ExtendableProperties._EXE_PATH.getPropertiesValue(k)
				)
			);
		}
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
	
	private static void toggleHighlightButton(Component c, JButton selButton, JButton curButton) {
		Color color = curButton.getBackground();
		if(selButton != null)
			selButton.setBackground(color);
		curButton.setBackground(HIGHLIGHT_COLOR);
		selButton = curButton;
		selectedName = curButton.getName();
		selectedButton = curButton;
		c.repaint();
	}
}
