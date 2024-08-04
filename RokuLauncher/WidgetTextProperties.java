package RokuLauncher;

public enum WidgetTextProperties implements Properties{
	APPLICATION_TITLE("application_title"),
	HIGHLIGHT_COLOR("highlight_color"),
	TITLE_COLOR_FOREGROUND("title_color_foreground"),
	TITLE_COLOR_BACKGROUND("title_color_background"),
	NAV_BUTTON_WEST("nav_button_west"),
	NAV_BUTTON_EAST("nav_button_east"),
	SYSTEM_TRAY_OPEN_OPTION("system_tray_open_option"),
	SYSTEM_TRAY_CLOSE_OPTION("system_tray_close_option"),
	SYSTEM_TRAY_LABEL("system_tray_label"),
	MENU_OPTION_FILE("menu_option_file"),
	MENU_OPTION_RELOAD("menu_option_reload"),
	MENU_OPTION_MIN_TRAY("menu_option_min_tray"),
	CLOSE_VIDEO_TEXT("close_button_text");
	
	private String property;
	private Paths path = Paths.WIDGET_TEXT;
	
	private WidgetTextProperties(String property)
	{
		this.property = property;
	}
	
	@Override
	public Paths getPath() {
		return path;
	}
	@Override
	public String getProperty(){
		return this.property;
	}
}
