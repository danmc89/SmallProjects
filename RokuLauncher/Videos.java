package RokuLauncher;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Videos {
	private static int counter = 0;
	
	private int position = 0;
	private ArrayList<String> videos = new ArrayList<String>();
	private String 
		path,
		title,
		videoStripFilter,
		filetype;
	
	public Videos(ArrayList<String> videos, String path, String title, String videoStripFilter, String filetype)
	{
		this.videos = videos;
		this.path = path;
		this.title = title;
		this.videoStripFilter = videoStripFilter;
		this.filetype = filetype;
		position = counter++;
	}
	
	public ArrayList<String> getVideos()
	{
		return this.videos;
	}
	
	public String getPath()
	{
		return this.path;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public int getPosition()
	{
		return this.position;
	}
	
	public String getVideoStripFilter()
	{
		return this.videoStripFilter;
	}
	
	public String getFileType()
	{
		return filetype;
	}
	
	public String returnVideo(String video)
	{
		String 
			vid = "",
			propFile = path + File.separator + video;
		//if url type use the contents
		if(FileType.getFileType(getFileType()) == FileType.URL)
		{
			HashMap<String,String> propCol = PropertiesFileLoader.readProperties(propFile, "=");
			vid = propCol.get("URL");
		}
		//otherwise use the file itself
		else
			vid = propFile;
		return vid;
	}
}
