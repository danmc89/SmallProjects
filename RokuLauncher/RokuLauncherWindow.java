package RokuLauncher;

import java.awt.BorderLayout;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
	private static final ArrayList<Videos> COLLECTION_VIDEOS = new ArrayList<Videos>();
	static {
		setupVideoLists();
	}
	
	private static int videosListPos = 0;
	private static int maxScrollBarSize = 0;
	private static boolean scrollUse = false;
	
	private JPanel 
		innerPanel = new JPanel(),
		innerPanel2 = new JPanel();//using for scrollbar
	private JScrollPane scrPane = null;
	private JButton 
		navW = null,
		navE = null;
	private TrayIcon launcherTrayIcon = null;
	private JButton selectedButton;
	private String selectedName;
	
	
	public RokuLauncherWindow()
	{
		int winHeight = LauncherProperties.WINDOW_HEIGHT.getPropertiesValueAsInt();
		
		addMenuButtons();
		
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
	
	public int getVideosListPos()
	{
		return videosListPos;
	}
	
	public ArrayList<Videos> getVideos()
	{
		return COLLECTION_VIDEOS;
	}
	
	public JButton getSelectedButton()
	{
		return this.selectedButton;
	}
	
	public void setSelectedButtonAndText(JButton selectedButton, String text)
	{
		this.selectedButton = selectedButton;
		this.selectedName = text;
	}
	
	public JPanel getChannelPanel() {
		return innerPanel;
	}
	
	public TrayIcon getTrayIcon()
	{
		return launcherTrayIcon;
	}
	
	private void addMenuButtons()
	{
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem
			jmReload,
			jmSystemTray,
			jmExit;
		
		WidgetCreator.setupTaskbar(this);
		
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
			jmSystemTray = new JMenuItem(WidgetTextProperties.MENU_OPTION_MIN_TRAY.getPropertiesValue());
			jmSystemTray.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MenuItem miO = WidgetCreator.buildMenuItem(
							WidgetTextProperties.SYSTEM_TRAY_OPEN_OPTION.getPropertiesValue(),
							new OpenPopupMenu(RokuLauncherWindow.this));
					MenuItem miC = WidgetCreator.buildMenuItem(
							WidgetTextProperties.SYSTEM_TRAY_CLOSE_OPTION.getPropertiesValue(),
							new ClosePopupMenu());
					String trayText = selectedName == null || selectedName.equals("")
							? WidgetTextProperties.SYSTEM_TRAY_LABEL.getPropertiesValue()
							: selectedName;
					PopupMenu popMenu = WidgetCreator.buildPopupMenu(miO, miC);
					launcherTrayIcon = WidgetCreator.setupTrayIcon(RokuLauncherWindow.this, trayText, popMenu);
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
	
	protected void addChannelButtons()
	{
		clearInnerPanels();
		
		JButton b = null;
		ArrayList<String> listOfFiles = COLLECTION_VIDEOS.get(videosListPos).getVideos();
		JLabel tf = new JLabel(COLLECTION_VIDEOS.get(videosListPos).getTitle());
		{
			tf.setOpaque(true);
			tf.setHorizontalAlignment(JTextField.CENTER);
			tf.setBackground(WidgetTextProperties.TITLE_COLOR_BACKGROUND.getPropertyValueAsColor());
			tf.setForeground(WidgetTextProperties.TITLE_COLOR_FOREGROUND.getPropertyValueAsColor());
			innerPanel.add(tf, BorderLayout.CENTER);
		}
		for (String s : listOfFiles)
		{
			ChannelActionListener cListener = new ChannelActionListener(this); 
			b = WidgetCreator.createChannelButton(s, cListener, this);
			cListener.setChannelButton(b);
			innerPanel.add(b);
		}
		innerPanel.add(WidgetCreator.createCloseButton(this,WidgetTextProperties.CLOSE_VIDEO_TEXT.getPropertiesValue()));
		
		//select the channel button if already in selected name
		JButton sel = WidgetCreator.findButton(getChannelPanel().getComponents(), selectedName);
		if(sel != null)
		{
			for(ActionListener al : sel.getActionListeners())
			{
				LoggingMessages.printOut("listeners: " + al);
				if (al != null && al instanceof ChannelActionListener)
				{
					ChannelActionListener cListener = (ChannelActionListener) al;
					cListener.toggleHighLightButton();
				}
			}
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
	
	private void createNavigationButtons()
	{
		navW = WidgetCreator.createButton(WidgetTextProperties.NAV_BUTTON_WEST.getPropertiesValue());
		navW.addActionListener(new NavigationButtonActionListener(this, Direction.BACKWARD));
		navE = WidgetCreator.createButton(WidgetTextProperties.NAV_BUTTON_EAST.getPropertiesValue());
		navE.addActionListener(new NavigationButtonActionListener(this, Direction.FORWARD));
		
		JPanel 
			jpW = WidgetCreator.createNavigationPanel(navW),
			jpE = WidgetCreator.createNavigationPanel(navE);
		
		this.add(jpW, BorderLayout.WEST);
		this.add(jpE, BorderLayout.EAST);
	}
	
	public void initialSizeDetect()
	{
		maxScrollBarSize = getCalcScrollThreshold();
		
		addChannelButtons();
		paintComponents(getGraphics());
	}
	public void reSizeDetect()
	{
		maxScrollBarSize = getCalcScrollThreshold();
		ArrayList<String> listOfOptions = COLLECTION_VIDEOS.get(videosListPos).getVideos(); 
		
		if ((listOfOptions.size() > maxScrollBarSize) != scrollUse)
		{
			addChannelButtons();
			paintComponents(getGraphics());
		}
	}
	/**
	 * 
	 * @return number of channel options able to provide before a scrollbar is required.
	 */
	private int getCalcScrollThreshold()
	{
		int 
			panelHeight = innerPanel2.getSize().height,
			buttonHeight = navE.getSize().height,
			limitHeightCount = buttonHeight > 0 ? (panelHeight/buttonHeight) - 2:0;//2 for title and close button always

		return limitHeightCount;
	}
	
	private int getWindowWidth()
	{
		ArrayList<String> listOfFiles = COLLECTION_VIDEOS.get(videosListPos).getVideos();
		@SuppressWarnings("unchecked")
		ArrayList<String> cloneList = (ArrayList<String>) listOfFiles.clone();
		String filter = COLLECTION_VIDEOS.get(videosListPos).getVideoStripFilter();
		
		Collections.sort(cloneList, new Comparator<String>(){
		    public int compare(String s1, String s2) {
		        return s2.length() - s1.length();
		    }
		});
		String lenStr = WidgetCreator.titleCreator(cloneList.get(0), filter);
		JButton b = WidgetCreator.findButton(getChannelPanel().getComponents(), lenStr);
		
		
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
		COLLECTION_VIDEOS.clear();
		setupVideoLists();
		PropertiesFileLoader.reloadLauncherProperties();
		addChannelButtons();
		paintComponents(getGraphics());
	}
	
	private static void setupVideoLists()
	{
		ArrayList<String> extList = ExtendableProperties.getExtendedList();
		for(String k : extList)
		{
			COLLECTION_VIDEOS.add(
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
	
	protected void setNextVideoIndex(int curPosition, Direction direction)
	{
		videosListPos = direction.getIndexDirectionNext(
				curPosition, 
				COLLECTION_VIDEOS.size()-1);
	}
}
