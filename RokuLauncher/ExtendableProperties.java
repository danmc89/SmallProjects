package RokuLauncher;

import java.util.ArrayList;

public enum ExtendableProperties {
	_POSITION("position"),
	_TITLE("_title"),
	_PATH("_path"),
	_CHANNEL_SUFFIX("_channel_suffix"),
	_CHANNEL_FILETYPE("_channel_filetype"),
	EXTENDED_LIST("extended_list");
	
	private String propertiesKey = "";
	
	//inital key is grouping prefix, followed by key and value;
	private static final ArrayList<String> EXTENDED_LIST_KEYS = new ArrayList<String>();
	static {
		gatherExtended();
	}
	
	private ExtendableProperties(String propertiesKey)
	{
		this.propertiesKey = propertiesKey;
	}
	
	public String getPropertiesKey()
	{
		return this.propertiesKey;
	}
	public String getPropertiesValue(String prefix)
	{
		String propVal = PropertiesFileLoader.getLauncherProperties().get(prefix + propertiesKey);
		return propVal;
	}
	public int getPropertiesValueAsInt(String prefix)
	{
		String propVal = PropertiesFileLoader.getLauncherProperties().get(prefix + propertiesKey);
		return Integer.parseInt(propVal);
	}
	
	public static ArrayList<String> getExtendedList()
	{
		return EXTENDED_LIST_KEYS;
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
}
