import java.util.concurrent.locks.*;


public class Restaurant {

	private static int numTables = 6;
	private static int numChairsPerTable = 5;
	private static Table[] tables = new Table[numTables];
	private static Philosopher[] philosophers = new Philosopher[(numTables-1)*numChairsPerTable];
	private static Chopstick[][] chopsticks = new Chopstick[numTables][numChairsPerTable];
	private static ReentrantLock[][] chopsticklocks = new ReentrantLock[numTables][numChairsPerTable];
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{	
		char curId = 'A';
		int curPhilosopher = 0;
		
		int lsticknum, rsticknum;
		
		
		// Generate tables and chopsticks
		for (int i = 0; i < numTables; i++)
		{
			chopsticks[i] = new Chopstick[numChairsPerTable];
			chopsticklocks[i] = new ReentrantLock[numChairsPerTable];
			for (int j = 0; j < numChairsPerTable; j++)
			{
				chopsticks[i][j] = new Chopstick();
				chopsticklocks[i][j] = new ReentrantLock();
			}
			tables[i] = new Table();
		}
					
		// Generate philosophers and add them to tables
		for (int i = 0; i < numTables-1; i++)
		{
			for (int j = 0; j < tables[i].getNumChairs(); j++)
			{				
				lsticknum = j-1;
				if (lsticknum < 0)
				{
					lsticknum = lsticknum + tables[i].getNumChairs();
				}
				rsticknum = j;
				philosophers[curPhilosopher] = new Philosopher(curId, i, j, chopsticks[i][lsticknum],
						chopsticklocks[i][lsticknum], chopsticks[i][rsticknum], chopsticklocks[i][rsticknum]);
				tables[i].addPhilosopher(philosophers[curPhilosopher]);
				curId++;
				curPhilosopher++;
			}
		}
		
		// Start off all the philosopher threads.
		// Could randomize this or set up a barrier so that lower-indexed philosophers are not favoured to
		// begin earlier.
		for (int i = 0; i < (numTables-1)*tables[0].getNumChairs(); i++) 
		{
			philosophers[i].start();
		}
		
		boolean[] deadlocked =  new boolean[numTables];
		for (int i= 0; i < numTables; i++)
		{
			deadlocked[i] = false;
		}
		int newPosition;
		boolean deadlockedFinalTable = false;
		while (!deadlockedFinalTable) // Check for deadlock continuously
		{
			try
			{
				Thread.sleep(5);
				for (int i = 0; i < numTables; i++)
				{
					if (!deadlocked[i]) // Only check non-deadlocked tables for deadlock
					{						
						deadlocked[i] = tables[i].isDeadlocked();
						if (deadlocked[i])
						{
							if (i != numTables-1) // Move random philosopher to sixth table if deadlock detected
							{
								System.out.println("Deadlock on Table " + i + "!");								
								newPosition = tables[i].movePhilosopher(tables[numTables-1]);
								lsticknum = newPosition-1;
								if (lsticknum < 0) lsticknum = lsticknum + tables[numTables-1].getNumChairs();
								tables[numTables-1].getChair(newPosition).getPhilosopher().setLeftChopstick(chopsticks[numTables-1][lsticknum], 
										chopsticklocks[numTables-1][lsticknum]);
								tables[numTables-1].getChair(newPosition).getPhilosopher().setRightChopstick(chopsticks[numTables-1][newPosition], 
										chopsticklocks[numTables-1][newPosition]);
							}
							else
							{
								System.out.println("Deadlock on final table - last Philosopher ID added was " + tables[i].getLastPhilosopherID() + "\n");
								deadlockedFinalTable = true;
								break;
							}
						}
					}
				}
			}
			catch (InterruptedException ie)
			{
				
			}
		}
		for (int i = 0; i < (numTables-1)*numChairsPerTable; i++)
		{
			philosophers[i].interrupt();
		}
	}
}
