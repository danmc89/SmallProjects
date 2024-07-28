package RokuLauncher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

public class RokuLauncher {
	public static final String 
		ROKU_PATH="RokuLauncher\\Roku Channels",
		FIREFOX_PATH="firefox.exe",
		ROKU_CHANNEL_SUFFIX="Online for Free _ The Roku Channel _ Roku.url",
		ROKU_CHANNEL_FILETYPE=".url";
	
	public static ArrayList<String> getOSFileList(String dir, String filter) 
	{
		ArrayList<String> files = new ArrayList<String>();
		File [] fs = new File(dir).listFiles();
		
		for (File f : fs)
		{
			if(f.getName().contains(filter))
			{
				files.add(f.getName());
				System.out.println(f.getName());
			}
		}
		return files;
	}
	
	public static void executeProcess(String ...args)
	{
		try {
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void launchChannel(String filename)
	{
		executeProcess(FIREFOX_PATH, filename);
	}
	
	public static void main(String [] args)
	{
		ArrayList<String> listOfChannels = getOSFileList(ROKU_PATH, ROKU_CHANNEL_FILETYPE);
		
		 SwingUtilities.invokeLater(() -> {
			 RokuLauncherWindow window = new RokuLauncherWindow(listOfChannels.size(), listOfChannels);
		        window.setVisible(true);
		    });
	}
}
