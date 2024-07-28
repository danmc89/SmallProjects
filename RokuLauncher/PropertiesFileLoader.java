package RokuLauncher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class PropertiesFileLoader {
	
	private static final HashMap<String,String> PROPERTIES = new HashMap<String,String>();
	static {
		readLauncherProperties();
	}
	
	private static void readLauncherProperties() 
	{
		
		File file = new File(".\\RokuLauncher\\data\\Launcher.properties");//use location from .bat script
		if(!file.exists())//use eclipse workspace location
		{
			System.out.println(new File(".").getAbsolutePath());
			file = new File(".\\src\\RokuLauncher\\data\\Launcher.properties");
		}
		Scanner sc;
		try {
			sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String s = sc.nextLine();
				String [] ss = s.split("=");
				PROPERTIES.put(ss[0], ss[1]);
				System.out.println(ss[0] + "," + ss[1]);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static HashMap<String,String> getLauncherProperties()
	{
		return PROPERTIES;
	}
	
}
