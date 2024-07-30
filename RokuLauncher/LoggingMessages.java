package RokuLauncher;

import java.nio.file.Paths;

public class LoggingMessages {
	
	public static void printOut(String out)
	{
		System.out.println(out);
	}
	
	public static void printFileNotFound(String filename)
	{
		printOut("Launcher Properties File: " + filename + " not Found!");
		printCurrentPath();
	}
	public static void printCurrentPath()
	{
		String s = Paths.get("").toAbsolutePath().toString();
		printOut("Current absolute path is: " + s);
	}
}
