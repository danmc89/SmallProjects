package RokuLauncher;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PropertiesFileLoader {
	
	private static HashMap<String,String> PROPERTIES = null;
	static {
		readLauncherProperties();
	}
	
	private static void readLauncherProperties() 
	{
		PROPERTIES = readProperties(".\\RokuLauncher\\data\\Launcher.properties", "=");
		if(PROPERTIES == null)
		{
			PROPERTIES = readProperties(".\\src\\RokuLauncher\\data\\Launcher.properties", "=");
		}
	}
	public static HashMap<String,String> readProperties(String location, String delimter)
	{
		HashMap<String,String> props = new HashMap<String,String>();
		File file = new File(location);//use location from .bat script
		if(!file.exists())//use eclipse workspace location
		{
			return null;
		}
		
		Scanner sc;
		try {
			sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String s = sc.nextLine();
				String [] ss = s.split(delimter);
				if(ss.length == 2)
					props.put(ss[0], ss[1]);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return props;
	}
	
	public static HashMap<String,String> getLauncherProperties()
	{
		return PROPERTIES;
	}
	
	public static ArrayList<String> getOSFileList(String dir, String filter) 
	{
		ArrayList<String> files = new ArrayList<String>();
		File [] fs = new File(dir).listFiles();
		
		for (File f : fs)
		{
			if(f.getName().contains(filter))
			{
				files.add(f.getName());
			}
		}
		return files;
	}
}
