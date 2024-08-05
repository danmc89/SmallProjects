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
import javax.swing.JLabel;
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
	private static final ArrayList<Videos> VIDEO_PATHS_AND_TITLE = new ArrayList<Videos>();
	
	private static JButton selectedButton;
	private static String selectedName;
	private static int videosListPos = 0;
	private static int maxScrollBarSize = 0;
	private static boolean scrollUse = false;
	private static Color defaultBackgroundColorChannel = null;
	
	private JPanel 
		innerPanel = new JPanel(),
		innerPanel2 = new JPanel();//using for scrollbar
	private JScrollPane scrPane = null;
	private JButton 
		navW = null,
		navE = null;
	private TrayIcon launcherTrayIcon = null;
	
	
	public RokuLauncherWindow()
	{
		int winHeight = LauncherProperties.WINDOW_HEIGHT.getPropertiesValueAsInt();
		
		addMenuButtons();
		setupVideoLists();
		
		setTitle(WidgetTextProperties.APPLICATION_TITLE.getPropertiesValue());
		setLocation(LauncherProperties.WINDOW_LOCATION_X.getPropertiesValueAsInt(), 
				LauncherProperties.WINDOW_LOCATION_Y.getPropertiesValueAsInt());
		
		BorderLayout bl = new BorderLayout();
		scrPane = new JScrollPane(innerPanel);
		
		BorderLayout bl2 = new BorderLayout();
		innerPanel2.setLayout(bl2);
		innerPanel2.add(scrPane, BorderLayout.NORTH);
		this.setLayout(bl);
		this.add(innerPanel2, BorderLayout.CENTER);
		
		createNavigationButtons();
		addChannelButtons();
		
		this.setSize(getWindowWidth(), winHeight);
		this.addWindowListener(new RokuLauncherWindowListener(this));
		this.addComponentListener(new RokuLauncherComponentListener(this));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private JButton findButton(String text)
	{
		if (text == null)
			return null;
		JButton retB = null;
		
		for (Component c : innerPanel.getComponents())
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
	
	private void addMenuButtons()
	{
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem 
			jmReload,
			jmSystemTray,
			jmExit;
		
		//Create the menu bar.
		menuBar = new JMenuBar();
		menu = new JMenu(WidgetTextProperties.MENU_OPTION_FILE.getPropertiesValue());
		jmReload = new JMenuItem(WidgetTextProperties.MENU_OPTION_RELOAD.getPropertiesValue());
		jmReload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reloadPropertiesFile();
				LoggingMessages.printOut(WidgetTextProperties.MENU_OPTION_RELOAD.getPropertiesValue());
			}
		});
		menu.add(jmReload);//end "reload" option add
		
		if(LauncherProperties.SYSTEM_TRAY.getPropertiesValue().toLowerCase().equals("true"))
		{
			setupTaskbar();
			jmSystemTray = new JMenuItem(WidgetTextProperties.MENU_OPTION_MIN_TRAY.getPropertiesValue());
			jmSystemTray.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setupTrayIcon();
					setVisible(false);
					setExtendedState(NORMAL);
				}
			});
			menu.add(jmSystemTray);
		}//end "minimize to system tray" add
		
		jmExit = new JMenuItem(WidgetTextProperties.MENU_OPTION_EXIT.getPropertiesValue());
		jmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RokuLauncher.closeRokuVideo();
				System.exit(0);
				LoggingMessages.printOut(WidgetTextProperties.MENU_OPTION_EXIT.getPropertiesValue());
			}
		});
		menu.add(jmExit);//end "exit" option add
		
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}
	
	private void addChannelButtons()
	{
		clearInnerPanels();
		
		JButton b = null;
		ArrayList<String> listOfFiles = VIDEO_PATHS_AND_TITLE.get(videosListPos).getVideos();
		JLabel tf = new JLabel(VIDEO_PATHS_AND_TITLE.get(videosListPos).getTitle());
		{
			tf.setOpaque(true);
			tf.setHorizontalAlignment(JTextField.CENTER);
			tf.setBackground(WidgetTextProperties.TITLE_COLOR_BACKGROUND.getPropertyValueAsColor());
			tf.setForeground(WidgetTextProperties.TITLE_COLOR_FOREGROUND.getPropertyValueAsColor());
			innerPanel.add(tf, BorderLayout.CENTER);
		}
		for (String s : listOfFiles)
		{
			b = createChannelButton(s);
			innerPanel.add(b);
		}
		defaultBackgroundColorChannel = b.getBackground();
		innerPanel.add(createCloseButton(WidgetTextProperties.CLOSE_VIDEO_TEXT.getPropertiesValue()));
		JButton sel = findButton(selectedName);
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
				String exe = VIDEO_PATHS_AND_TITLE.get(videosListPos).getExeName();
				if(exe != null && !exe.equals(""))
				{
					RokuLauncher.executeProcess(exe, VIDEO_PATHS_AND_TITLE.get(videosListPos).returnVideo(b.getName()));
					toggleHighlightButton(innerPanel, selectedButton, b);
				}
				else
				{
					RokuLauncher.executeProcess(VIDEO_PATHS_AND_TITLE.get(videosListPos).returnVideo(b.getName()));
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
		String filter = VIDEO_PATHS_AND_TITLE.get(videosListPos).getVideoStripFilter();
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
		
		navW = createButton(WidgetTextProperties.NAV_BUTTON_WEST.getPropertiesValue());
		navE = createButton(WidgetTextProperties.NAV_BUTTON_EAST.getPropertiesValue());
		
		jpW.setLayout(blW);
		jpE.setLayout(blE);
		navW.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setNextVideoIndex(videosListPos, Direction.BACKWARD);
				addChannelButtons();
				paintComponents(getGraphics());
			}
		});
		navE.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setNextVideoIndex(videosListPos, Direction.FORWARD);
				addChannelButtons();
				paintComponents(getGraphics());
			}
		});
		this.add(jpW, BorderLayout.WEST);
		this.add(jpE, BorderLayout.EAST);
		jpW.add(navW, BorderLayout.NORTH);
		jpE.add(navE, BorderLayout.NORTH);
	}
	
	public void initialSizeCalc()
	{
		int 
			panelHeight = innerPanel2.getSize().height,
			buttonHeight = navE.getSize().height,
			limitHeightCount = buttonHeight > 0 ? (panelHeight/buttonHeight) - 2:0;//2 for title and close button always
	
		maxScrollBarSize = limitHeightCount;
		
		addChannelButtons();
		paintComponents(getGraphics());
	}
	
	public void reSizeCalc()
	{
		int 
			panelHeight = innerPanel2.getSize().height,
			buttonHeight = navE.getSize().height,
			limitHeightCount = buttonHeight > 0 ? (panelHeight/buttonHeight) - 2:0;//2 for title and close button always
	
		maxScrollBarSize = limitHeightCount;
		ArrayList<String> listOfOptions = VIDEO_PATHS_AND_TITLE.get(videosListPos).getVideos(); 
		
		if ((listOfOptions.size() > maxScrollBarSize) != scrollUse)
		{
			addChannelButtons();
			paintComponents(getGraphics());
		}
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
	
	private int getWindowWidth()
	{
		ArrayList<String> listOfFiles = VIDEO_PATHS_AND_TITLE.get(videosListPos).getVideos();
		@SuppressWarnings("unchecked")
		ArrayList<String> cloneList = (ArrayList<String>) listOfFiles.clone();
		String filter = VIDEO_PATHS_AND_TITLE.get(videosListPos).getVideoStripFilter();
		
		Collections.sort(cloneList, new Comparator<String>(){
		    public int compare(String s1, String s2) {
		        return s2.length() - s1.length();
		    }
		});
		String lenStr = titleCreator(cloneList.get(0), filter);
		JButton b = findButton(lenStr);
		
		
		if(b == null)
		{
			return LauncherProperties.WINDOW_WIDTH_MIN.getPropertiesValueAsInt();
		}
		FontMetrics fm = b.getFontMetrics(b.getFont());
		
		int width = fm.stringWidth(lenStr) + (navE.getSize().width * 2) + 
				LauncherProperties.WINDOW_WIDTH_CALC_PAD.getPropertiesValueAsInt();
		
		LoggingMessages.printOut("Longest title character length: " + lenStr.length() + "; calculated width: " + width);
		
		return width > LauncherProperties.WINDOW_WIDTH_MIN.getPropertiesValueAsInt()
				? width 
				: LauncherProperties.WINDOW_WIDTH_MIN.getPropertiesValueAsInt();
	}
	
	private void buildInnerPanels(ArrayList<String> listOfOptions)
	{
		LayoutManager gl = new GridLayout(listOfOptions.size()+2, 1);//plus 2 for "close roku video option" and title
		innerPanel.setLayout(gl);
		
		scrPane = new JScrollPane(innerPanel);
		if(listOfOptions.size() <= maxScrollBarSize) {
			innerPanel2.add(scrPane, BorderLayout.NORTH);
			scrollUse = false;
		}
		else {
			//center for scrolling...
			innerPanel2.add(scrPane, BorderLayout.CENTER);
			scrollUse = true;
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
			File file = new File(LauncherProperties.ICON.getPropertiesValue());//use location from .bat script
			BufferedImage img = ImageIO.read(file);
			MenuItem 	
				open = new MenuItem(WidgetTextProperties.SYSTEM_TRAY_OPEN_OPTION.getPropertiesValue()),
				close = new MenuItem(WidgetTextProperties.SYSTEM_TRAY_CLOSE_OPTION.getPropertiesValue());
			
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
			String trayText = selectedName == null || selectedName.equals("")
					? WidgetTextProperties.SYSTEM_TRAY_LABEL.getPropertiesValue()
					: selectedName;
			launcherTrayIcon = new TrayIcon(img, trayText, trayPopupMenu);
			
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
		File file = new File(LauncherProperties.ICON.getPropertiesValue());
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
		videosListPos = direction.getIndexDirectionNext(
				curPosition, 
				VIDEO_PATHS_AND_TITLE.size()-1);
	}
	
	private static void toggleHighlightButton(Component c, JButton selButton, JButton curButton) {
		if(selButton != null)
			selButton.setBackground(defaultBackgroundColorChannel);
		curButton.setBackground(WidgetTextProperties.HIGHLIGHT_COLOR.getPropertyValueAsColor());
		selButton = curButton;
		selectedName = curButton.getText();
		selectedButton = curButton;
		c.repaint();
	}
}
