package RokuLauncher;

public enum RokuProperties {
	APPLICATION_TITLE("application_title"),
	BROWSER_PATH("browser_path"),
	WINDOW_HEIGHT("window_height"),
	WINDOW_WIDTH_MIN("window_width_min"),
	WINDOW_LOCATION_X("window_location_x"),
	WINDOW_LOCATION_Y("window_location_y"),
	WINDOW_WIDTH_CALC_PAD("window_width_calc_pad"),
	ICON("icon"),
	SYSTEM_TRAY("system_tray"),
	CLOSE_VIDEO_TEXT("close_button_text");
	
	private String 
		propertiesValue = "",
		propertiesKey = "";
	
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