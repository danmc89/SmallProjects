package RokuLauncher;

import java.lang.Integer;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;

public class RokuLauncherWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static final int 
		BUTTON_HEIGHT = Integer.parseInt(PropertiesFileLoader.getLauncherProperties().get("button_height")),
		WINDOW_WIDTH_MIN = Integer.parseInt(PropertiesFileLoader.getLauncherProperties().get("button_width_min")),
		WINDOW_LOCATION_X = Integer.parseInt(PropertiesFileLoader.getLauncherProperties().get("window_location_x")),
		WINDOW_LOCATION_Y = Integer.parseInt(PropertiesFileLoader.getLauncherProperties().get("window_location_y"));
	private FontMetrics fontMetricsButton;
	
	public RokuLauncherWindow(int count, ArrayList<String> listOfFiles)
	{
		setTitle("Roku Launcher");
		LayoutManager gl = new GridLayout(count, 1);
		setLayout(gl);
		setLocation(WINDOW_LOCATION_X, WINDOW_LOCATION_Y);
		
		addButtons(listOfFiles);
		int winWide = windowWidth(listOfFiles);
		setSize(winWide, BUTTON_HEIGHT * count);//setSize is dependent on button add for "fontMetricsButton" variable
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void addButtons(ArrayList<String> listOfFiles)
	{
		JButton b = null;
		for (String s : listOfFiles)
		{
			b = createButton(this, s);
			this.add(b);
		}
		this.fontMetricsButton = b.getFontMetrics(b.getFont()); 		
	}
	
	public JButton createButton(JFrame jf, String title)
	{
		JButton b = new JButton();
		
		b.setName(title);
		b.setText(titleCreator(title));
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RokuLauncher.launchChannel(RokuLauncher.ROKU_PATH + File.separator + b.getName());
			}
		});
		return b;
	}
	
	private String titleCreator(String buttonTitle)
	{
		String 
			replstr = "",
			stripStr = RokuLauncher.ROKU_CHANNEL_SUFFIX;
		
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
		
		return width > WINDOW_WIDTH_MIN ? width : WINDOW_WIDTH_MIN;
	}
}
