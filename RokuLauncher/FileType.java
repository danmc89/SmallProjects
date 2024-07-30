package RokuLauncher;

public enum FileType {
	URL(".url");
	
	private String url;
	private FileType(String url)
	{
		this.url = url;
	}
	
	public String getUrlStr()
	{
		return this.url;
	}
	
	public static FileType getFileType(String fileType)
	{
		for (FileType ft : FileType.values())
		{
			if(ft.getUrlStr().equals(fileType))
				return ft;
		}
		return null;
	}
}
