package RokuLauncher;

import java.util.ArrayList;

public class Videos {
	private static int counter = 0;
	
	private int position = 0;
	private ArrayList<String> videos = new ArrayList<String>();
	private String path;
	private String title;
	private String videoStripFilter;
	
	public Videos(ArrayList<String> videos, String path, String title, String videoStripFilter)
	{
		this.videos = videos;
		this.path = path;
		this.title = title;
		this.videoStripFilter = videoStripFilter;
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
}
