
public class Table {
	private static int numSeats = 5;
	private Chair[] chairs;
	private char lastPhilosopherID;
	
	public Table() 
	{
		chairs =  new Chair[numSeats];		
		for (int i = 0; i < numSeats; i++)
		{
			chairs[i] = new Chair();			
		}
		lastPhilosopherID = 'Z';
	}
	
	public boolean isDeadlocked()
	{
		boolean deadlocked = true;
		for (int i = 0; i < numSeats; i++)
		{
			if (!(chairs[i].getOccupied()) || !((chairs[i].isWaiting())))
			{
				deadlocked = false;
				break;
			}
		}
		return deadlocked;
	}
	
	public int getNumChairs()
	{
		return numSeats;
	}
	
	public Chair getChair(int chairnum)
	{
		return chairs[chairnum];
	}
	
	public int addPhilosopher(Philosopher p)
	{
		for (int i = 0; i < numSeats; i++)
		{
			if (!chairs[i].getOccupied()) // Add philosopher to an empty chair
			{
				chairs[i].addPhilosopher(p);				
				lastPhilosopherID = p.getID();
				return i;
			}
		}
		return -1;
	}
	
	public char getLastPhilosopherID()
	{
		return lastPhilosopherID;
	}
	
	// This method should only be called when deadlock has been detected
	public int movePhilosopher(Table otherTable)
	{
		int philtomove = ((int) (Math.random()*numSeats));
		if (chairs[philtomove].getPhilosopher().usingLeftChopstick())
		{			
			chairs[philtomove].getPhilosopher().getLeftChopstick().dropChopstick();			
		}
		if (chairs[philtomove].getPhilosopher().usingRightChopstick())
		{			
			chairs[philtomove].getPhilosopher().getRightChopstick().dropChopstick();			
		}
		int otherPosition = otherTable.addPhilosopher(chairs[philtomove].getPhilosopher());
		chairs[philtomove].removePhilosopher();		
		return otherPosition;
	}
	
}
