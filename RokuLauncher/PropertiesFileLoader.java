package RokuLauncher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PropertiesFileLoader {
	
	private static final HashMap<String,String> PROPERTIES = new HashMap<String, String>();
	static {
		try {
			readLauncherProperties();
		} catch (FileNotFoundException e) {
			LoggingMessages.printFileNotFound(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static HashMap<String,String> getLauncherProperties()
	{
		return PROPERTIES;
	}
	
	public static void reloadLauncherProperties()
	{
		PROPERTIES.clear();
		try {
			readLauncherProperties();
		} catch (FileNotFoundException e) {
			LoggingMessages.printFileNotFound(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static HashMap<String,String> readProperties(String location, String delimter)
	{
		HashMap<String,String> props = new HashMap<String,String>();
		File file = new File(location);//use location from .bat script
		Scanner sc;
		
		if(!file.exists())//use eclipse workspace location
		{
			return null;
		}
		try {
			sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String s = sc.nextLine();
				String [] ss = s.split(delimter);
				if(ss.length == 2)
					props.put(ss[0], ss[1]);
			}
		} catch (FileNotFoundException e) {
			LoggingMessages.printFileNotFound(e.getMessage());
			e.printStackTrace();
		}
		return props;
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
	
	private static void readLauncherProperties() throws FileNotFoundException 
	{
		HashMap<String, String> tmp = null;
		String [] propLocs = new String [] {
				".\\RokuLauncher\\data\\Launcher.properties",
				".\\src\\RokuLauncher\\data\\Launcher.properties"};
		for(String loc : propLocs)
		{
			tmp = readProperties(loc, "=");
			if(tmp != null)
			{
				PROPERTIES.putAll(tmp);
				break;
			}
		}
		if (tmp == null) {
			FileNotFoundException fe = new FileNotFoundException("Launcher.properties not found!");
			throw fe;
		}
	}
}
