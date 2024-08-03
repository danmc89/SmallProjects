package RokuLauncher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.awt.event.WindowListener;
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
	
	private JPanel 
		innerPanel = new JPanel(),
		innerPanel2 = new JPanel();//using for scrollbar
	private JScrollPane scrPane = null;
	private JButton 
		navW = null,
		navE = null;
	private TrayIcon launcherTrayIcon = null;
	
	private int maxScrollBarSize = 20; //TODO have calc but sequence of events
	
	public RokuLauncherWindow()
	{
		int winHeight = RokuProperties.WINDOW_HEIGHT.getPropertiesValueAsInt();
		
		addMenuButtons();
		setupVideoLists();
		
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
		
		createNavigationButtons();
		addChannelButtons();
		
		this.setSize(getWindowWidth(), winHeight);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private JButton findButton(String text, String filter)
	{
		if (text == null)
			return null;
		if(filter == null)
			filter = "";
		JButton b = null;
		
		for (Component c : innerPanel.getComponents())
		{
			if (c == null)
				continue;
			String nme = c.getName();
			LoggingMessages.printOut(nme + ":to match:" + text + filter);
			if(nme != null && nme.equals(text + filter)){
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
	
	private void addChannelButtons()
	{
		clearInnerPanels();
		
		JButton b = null;
		String filter = VIDEO_PATHS_AND_TITLE.get(videoPos).getVideoStripFilter();
		ArrayList<String> listOfFiles = VIDEO_PATHS_AND_TITLE.get(videoPos).getVideos();
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
		innerPanel.add(createCloseButton(RokuProperties.CLOSE_VIDEO_TEXT.getPropertiesValue()));
		JButton sel = findButton(selectedName, filter);
		if(sel != null)
		{
			toggleHighlightButton(innerPanel, selectedButton, sel);
		}
		buildInnerPanels(listOfFiles);
	}
	
	private void clearInnerPanels()
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
		
		navW = createButton(NAV_BUTTON_WEST);
		navE = createButton(NAV_BUTTON_EAST);
		
		jpW.setLayout(blW);
		jpE.setLayout(blE);
		navW.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setNextVideoIndex(videoPos, Direction.BACKWARD);
				addChannelButtons();
				paintComponents(getGraphics());
			}
		});
		navE.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setNextVideoIndex(videoPos, Direction.FORWARD);
				addChannelButtons();
				paintComponents(getGraphics());
			}
		});
		this.add(jpW, BorderLayout.WEST);
		this.add(jpE, BorderLayout.EAST);
		jpW.add(navW, BorderLayout.NORTH);
		jpE.add(navE, BorderLayout.NORTH);
	}
	
//	public void initialSizeCalc()
//	{
//		int 
//			panelHeight = innerPanel2.getSize().height,
//			buttonHeight = navE.getSize().height,
//			limitHeightCount = buttonHeight > 0 ? (panelHeight/buttonHeight) - 2:0;//2 for title and close button always
//	
//		LoggingMessages.printOut("button height: " + buttonHeight);
//		LoggingMessages.printOut("panel height: " + panelHeight);
//		LoggingMessages.printOut("limit amount: " + limitHeightCount);
//		maxScrollBarSize = limitHeightCount;
//		
//		clearInnerPanels();
//		buildInnerPanels(VIDEO_PATHS_AND_TITLE.get(videoPos).getVideos());
//		
//		reloadPropertiesFile();
//		LoggingMessages.printOut(MENU_OPTION_RELOAD);
//	}
	
	private String titleCreator(String buttonTitle, String stripStr)
	{
		String replstr = "";
		if(stripStr == null || stripStr.equals(""))
			return buttonTitle;
		Pattern pat = Pattern.compile(stripStr, Pattern.CASE_INSENSITIVE);
		Matcher mat = pat.matcher(buttonTitle);
		return mat.replaceAll(replstr);
	}
	
	private int getWindowWidth()
	{
		return RokuProperties.WINDOW_WIDTH_MIN.getPropertiesValueAsInt();
	}
	
//	private int getWindowWidth()
//	{
//		ArrayList<String> listOfFiles = VIDEO_PATHS_AND_TITLE.get(videoPos).getVideos();
//		@SuppressWarnings("unchecked")
//		ArrayList<String> cloneList = (ArrayList<String>) listOfFiles.clone();
//		String filter = VIDEO_PATHS_AND_TITLE.get(videoPos).getVideoStripFilter();
//		
//		Collections.sort(cloneList, new Comparator<String>(){
//		    public int compare(String s1, String s2) {
//		        return s2.length() - s1.length();
//		    }
//		});
//		String lenStr = titleCreator(cloneList.get(0), filter);
//		JButton b = findButton(lenStr, filter);
//		LoggingMessages.printOut(lenStr + b);
//		
//		LoggingMessages.printOut("Longest title character length: " + lenStr.length() );
//		
//		if(b == null)
//		{
//			return RokuProperties.WINDOW_WIDTH_MIN.getPropertiesValueAsInt();
//		}
//		FontMetrics fm = b.getFontMetrics(b.getFont());
//		
//		int width = fm.stringWidth(lenStr) + (navE.getSize().width * 2) + 
//				RokuProperties.WINDOW_WIDTH_CALC_PAD.getPropertiesValueAsInt();
//		LoggingMessages.printOut("Longest title character length: " + lenStr.length() + "; calculated width: " + width);
//		
//		return width > RokuProperties.WINDOW_WIDTH_MIN.getPropertiesValueAsInt()
//				? width 
//				: RokuProperties.WINDOW_WIDTH_MIN.getPropertiesValueAsInt();
//	}
	
	private void buildInnerPanels(ArrayList<String> listOfOptions)
	{
		LayoutManager gl = new GridLayout(listOfOptions.size()+2, 1);//plus 2 for "close roku video option" and title
		innerPanel.setLayout(gl);
		
		scrPane = new JScrollPane(innerPanel);
		if(listOfOptions.size() <= maxScrollBarSize) {
			innerPanel2.add(scrPane, BorderLayout.NORTH);
		}
		else {
			//center for scrolling...
			innerPanel2.add(scrPane, BorderLayout.CENTER);
		}
		this.add(innerPanel2, BorderLayout.CENTER);
		
		int width = getWindowWidth();
		if(this.getSize().width < width)
		{
			this.setSize(getWindowWidth(), this.getSize().height);
		}
	}
	
	private void reloadPropertiesFile()
	{
		VIDEO_PATHS_AND_TITLE.clear();
		setupVideoLists();
		PropertiesFileLoader.reloadLauncherProperties();
		addChannelButtons();
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
