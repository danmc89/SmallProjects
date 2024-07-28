package RokuLauncher;

import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;

public class RokuLauncherWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static final int 
		BUTTON_HEIGHT = 50,
		WINDOW_WIDTH_MIN = 300,
		WINDOW_LOCATION_X = 150,
		WINDOW_LOCATION_Y = 150;
	
	public RokuLauncherWindow(int count, ArrayList<String> listOfFiles)
	{
		setTitle("Roku Launcher");
		setSize(windowWidth(listOfFiles), BUTTON_HEIGHT * count);
		LayoutManager gl = new GridLayout(count, 1);
		setLayout(gl);
		setLocation(WINDOW_LOCATION_X, WINDOW_LOCATION_Y);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		addButtons(listOfFiles);
	}
	
	private void addButtons(ArrayList<String> listOfFiles)
	{
		for (String s : listOfFiles)
		{
			this.add(createButton(this, s));
		}
	}
	
	public JButton createButton(JFrame jf, String title)
	{
		JButton b = new JButton(title);
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
		String replstr = "";
		String stripStr = RokuLauncher.ROKU_CHANNEL_SUFFIX;
		
		Pattern pat = Pattern.compile(stripStr, Pattern.CASE_INSENSITIVE);
		Matcher mat = pat.matcher(buttonTitle);
		return mat.replaceAll(replstr);
	}
	
	private int windowWidth(ArrayList<String> listOfFiles)
	{
		int hi = 0, pad=100;
		for (String s : listOfFiles)
		{
			int len = s.length();
			if(hi < len)
				hi = s.length();
		}
		hi=(hi * 3) + pad;
		return hi > WINDOW_WIDTH_MIN ? hi : WINDOW_WIDTH_MIN;
	}
}
