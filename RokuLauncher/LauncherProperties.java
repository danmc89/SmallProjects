package RokuLauncher;

public enum LauncherProperties implements Properties{
	BROWSER_PATH("browser_path"),
	WINDOW_HEIGHT("window_height"),
	WINDOW_WIDTH_MIN("window_width_min"),
	WINDOW_LOCATION_X("window_location_x"),
	WINDOW_LOCATION_Y("window_location_y"),
	WINDOW_WIDTH_CALC_PAD("window_width_calc_pad"),
	ICON("icon"),
	SYSTEM_TRAY("system_tray");
	
	private String propertiesKey;
	private Paths path = Paths.LAUNCHER;
	
	private	LauncherProperties(String propertiesKey)
	{
		this.propertiesKey = propertiesKey;
	}
	
	@Override
	public Paths getPath() {
		return path;
	}
	@Override
	public String getProperty() {
		return propertiesKey;
	}

}