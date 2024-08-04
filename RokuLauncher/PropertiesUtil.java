package RokuLauncher;

import java.awt.Color;

public interface PropertiesUtil {
	
	public abstract Paths getPath();
	public abstract String getProperty();
	
	public default String getPropertiesValue()
	{
		return PropertiesFileLoader.getLauncherProperties(getPath()).get(getProperty());
	}
	
	public default int getPropertiesValueAsInt()
	{
		return Integer.parseInt(getPropertiesValue());
	}

	public default int [] getPropertyValueAsIntArray()
	{
		String str;
		int[] retI = null;
		
		str = PropertiesFileLoader.getLauncherProperties(getPath()).get(getProperty());
		String [] strS = str.split(",");
		retI = new int[strS.length];
		for (int i =0; i <strS.length; i++)
		{
			retI [i] = Integer.parseInt(strS[i]);
		}
		return retI;
	}
	
	public default Color getPropertyValueAsColor()
	{
		int [] c1 = getPropertyValueAsIntArray();
		return new Color(c1[0], c1[1], c1[2]);
	}
}
