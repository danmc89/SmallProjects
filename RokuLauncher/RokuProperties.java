package RokuLauncher;

public enum RokuProperties {
	APPLICATION_TITLE("application_title"),
	BROWSER_PATH("browser_path"),
	BUTTON_HEIGHT("button_height"),
	WINDOW_WIDTH_MIN("button_width_min"),
	WINDOW_LOCATION_X("window_location_x"),
	WINDOW_LOCATION_Y("window_location_y"),
	ICON("icon"),
	SYSTEM_TRAY("system_tray"),
	CLOSE_VIDEO_TEXT("close_button_text");
	
	private String propertiesValue = "";
	private String propertiesKey = "";
	
	private	RokuProperties(String propertiesKey)
	{
		this.propertiesKey = propertiesKey;
		this.propertiesValue = PropertiesFileLoader.getLauncherProperties().get(propertiesKey);
	}
	
	public String getPropertiesKey()
	{
		return this.propertiesKey;
	}
	
	public String getPropertiesValue()
	{
		return this.propertiesValue;
	}
	
	public int getPropertiesValueAsInt()
	{
		return Integer.parseInt(propertiesValue);
	}
	
	
}