package RokuLauncher;

public enum Paths {
	
	GUI(new String [] {".\\src\\RokuLauncher\\data\\Gui.properties", 
			".\\RokuLauncher\\data\\Gui.properties"}),
	LAUNCHER(new String [] {".\\src\\RokuLauncher\\data\\Launcher.properties", 
			".\\RokuLauncher\\data\\Launcher.properties"});
	
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
