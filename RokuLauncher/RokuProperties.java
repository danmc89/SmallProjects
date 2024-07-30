package RokuLauncher;

public enum RokuProperties {
	APPLICATION_TITLE(PropertiesFileLoader.getLauncherProperties().get("application_title")),
	ROKU_TITLE(PropertiesFileLoader.getLauncherProperties().get("roku_title")),
	ROKU_PATH(PropertiesFileLoader.getLauncherProperties().get("roku_path")),
	ROKU_CHANNEL_SUFFIX(PropertiesFileLoader.getLauncherProperties().get("roku_channel_suffix")),
	ROKU_CHANNEL_FILETYPE(".url"),
	YOUTUBE_TITLE(PropertiesFileLoader.getLauncherProperties().get("youtube_title")),
	YOUTUBE_PATH(PropertiesFileLoader.getLauncherProperties().get("youtube_path")),
	YOUTUBE_CHANNEL_SUFFIX(PropertiesFileLoader.getLauncherProperties().get("youtube_channel_suffix")),
	YOUTUBE_CHANNEL_FILETYPE(".url"),
	BROWSER_PATH(PropertiesFileLoader.getLauncherProperties().get("browser_path")),
	BUTTON_HEIGHT(PropertiesFileLoader.getLauncherProperties().get("button_height")),
	WINDOW_WIDTH_MIN(PropertiesFileLoader.getLauncherProperties().get("button_width_min")),
	WINDOW_LOCATION_X(PropertiesFileLoader.getLauncherProperties().get("window_location_x")),
	WINDOW_LOCATION_Y(PropertiesFileLoader.getLauncherProperties().get("window_location_y")),
	ICON(PropertiesFileLoader.getLauncherProperties().get("icon")),
	SYSTEM_TRAY(PropertiesFileLoader.getLauncherProperties().get("system_tray")),
	CLOSE_VIDEO_TEXT(PropertiesFileLoader.getLauncherProperties().get("close_button_text"));
	
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