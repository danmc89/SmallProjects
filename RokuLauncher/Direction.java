package RokuLauncher;

public enum Direction {
	FORWARD(1),
	BACKWARD(-1);
	
	private int dir = 0;
	private Direction (int dir)
	{
		this.dir = dir;
	}
	public int getValue()
	{
		return dir;
	}
}
