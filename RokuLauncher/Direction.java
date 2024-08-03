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
	
	public int getIndexDirectionNext(int curPosition, int lastIndex)
	{
		int 
			indexEnd = lastIndex,
			indexReturn = 0;
		
		switch (this) {
			case FORWARD:
				if(indexEnd < curPosition + 1)
					indexReturn = 0;
				else
					indexReturn = curPosition + 1;
				break;
				
			case BACKWARD:
				if(0 > curPosition - 1)
					indexReturn = indexEnd;
				else
					indexReturn = curPosition - 1;
				break;
		}
		return indexReturn;
	}
}
