package RokuLauncher;

public enum RokuProperties {
	
	ROKU_PATH(PropertiesFileLoader.getLauncherProperties().get("roku_path")),
	BROWSER_PATH(PropertiesFileLoader.getLauncherProperties().get("browser_path")),
	ROKU_CHANNEL_SUFFIX(PropertiesFileLoader.getLauncherProperties().get("roku_channel_suffix")),
	ROKU_CHANNEL_FILETYPE(".url"),
	BUTTON_HEIGHT(PropertiesFileLoader.getLauncherProperties().get("button_height")),
	WINDOW_WIDTH_MIN(PropertiesFileLoader.getLauncherProperties().get("button_width_min")),
	WINDOW_LOCATION_X(PropertiesFileLoader.getLauncherProperties().get("window_location_x")),
	WINDOW_LOCATION_Y(PropertiesFileLoader.getLauncherProperties().get("window_location_y")),
	ICON(PropertiesFileLoader.getLauncherProperties().get("icon"));
	
	private String propertiesValue = "";
	
	private	RokuProperties(String s)
	{
		this.propertiesValue = s;
	}
	
	public String getPropertiesValue()
	{
		return propertiesValue;
	}
	
	public int getPropertiesValueAsInt()
	{
		return Integer.parseInt(propertiesValue);
	}
	
}