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
		HashMap<String, String> tmp;
		String [] propLocs = new String [] {
				".\\RokuLauncher\\data\\Launcher.properties",
				".\\src\\RokuLauncher\\data\\Launcher.properties"};
		
		tmp = readProperties(propLocs[0], "=");
		if(tmp != null)
			PROPERTIES.putAll(tmp);
		else
		{
			tmp = readProperties(propLocs[1], "=");
			if(tmp != null)
				PROPERTIES.putAll(tmp);
			else
			{
				FileNotFoundException fe = new FileNotFoundException(propLocs[0] + " or " + propLocs[1]);
				throw fe;
			}
		}
	}
}
