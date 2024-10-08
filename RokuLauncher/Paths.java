package RokuLauncher;

public enum Paths {
	
	WIDGET_TEXT(new String [] {".\\src\\RokuLauncher\\data\\WidgetText.properties", 
		".\\RokuLauncher\\data\\WidgetText.properties"}),
	LAUNCHER(new String [] {".\\src\\RokuLauncher\\data\\Launcher.properties", 
		".\\RokuLauncher\\data\\Launcher.properties"}),
	EXTENDABLE(new String [] {".\\src\\RokuLauncher\\data\\Extendable.properties", 
		".\\RokuLauncher\\data\\Extendable.properties"});
	
	private String [] paths;
	
	private Paths(String ... path)
	{
		this.paths = path;
	}
	
	public String [] getPaths()
	{
		return this.paths;
	}
}
