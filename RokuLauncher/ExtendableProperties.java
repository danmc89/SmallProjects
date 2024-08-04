package RokuLauncher;

import java.util.ArrayList;

public enum ExtendableProperties implements Properties{
	_POSITION("position"),
	_TITLE("_title"),
	_PATH("_path"),
	_CHANNEL_SUFFIX("_channel_suffix"),
	_CHANNEL_FILETYPE("_channel_filetype"),
	_EXE_PATH("_exe_path"),
	EXTENDED_LIST("extended_list");
	
	private String propertiesKey = "";
	private Paths path = Paths.EXTENDABLE;
	
	private static final ArrayList<String> EXTENDED_LIST_KEYS = new ArrayList<String>();
	static {
		gatherExtended();
	}
	
	private static void gatherExtended()
	{
		String [] extList = ExtendableProperties.EXTENDED_LIST.getPropertiesValue("").split(",");
		for(String ext : extList)
		{
			LoggingMessages.printOut(ext);
			EXTENDED_LIST_KEYS.add(ext);
		}
	}
	public static ArrayList<String> getExtendedList()
	{
		return EXTENDED_LIST_KEYS;
	}
	
	private ExtendableProperties(String propertiesKey)
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
	
	public String getPropertiesValue(String prefix)
	{
		String propVal = PropertiesFileLoader.getLauncherProperties(path).get(prefix + propertiesKey);
		return propVal;
	}
	public int getPropertiesValueAsInt(String prefix)
	{
		String propVal = PropertiesFileLoader.getLauncherProperties(path).get(prefix + propertiesKey);
		return Integer.parseInt(propVal);
	}
}
