import java.util.concurrent.locks.*;

public class Philosopher extends Thread {
	private char id;
	private Chopstick leftchopstick;
	private Chopstick rightchopstick;
	private ReentrantLock leftsticklock;
	private ReentrantLock rightsticklock;
	private boolean usingleftstick;
	private boolean usingrightstick;
	private boolean interrupted = false;
	private ReentrantLock waitlock = new ReentrantLock();
	private boolean waitingonLock;
	
	private ReentrantLock chopstickmutex = new ReentrantLock();
	
	public Philosopher(char myID, int myTable, int myChair, Chopstick lstick, ReentrantLock lsticklock,
			Chopstick rstick, ReentrantLock rsticklock)
	{
		id = myID;
		usingleftstick = false;
		usingrightstick = false;
		leftchopstick = lstick;
		leftsticklock = lsticklock;
		rightchopstick = rstick;
		rightsticklock = rsticklock;
		waitingonLock = false;		
	}
	
	public char getID()
	{
		return id;
	}
	
	public void setLeftChopstick(Chopstick lstick, ReentrantLock lsticklock)
	{
		chopstickmutex.lock();
		if (leftsticklock.isHeldByCurrentThread())
		{
			leftsticklock.unlock();
		}
		leftchopstick = lstick;
		leftsticklock = lsticklock;
		chopstickmutex.unlock();
	}
	
	public void setRightChopstick(Chopstick rstick, ReentrantLock rsticklock)
	{
		chopstickmutex.lock();
		if (rightsticklock.isHeldByCurrentThread())
		{
			rightsticklock.unlock();
		}
		rightchopstick = rstick;
		rightsticklock = rsticklock;
		chopstickmutex.unlock();
	}
	
	public Chopstick getLeftChopstick()
	{
		return leftchopstick;
	}
	
	public Chopstick getRightChopstick()
	{
		return rightchopstick;
	}
	
	public ReentrantLock getLeftLock()
	{
		return leftsticklock;
	}
	
	public ReentrantLock getRightLock()
	{
		return rightsticklock;
	}
	
	public boolean usingLeftChopstick()
	{
		return usingleftstick;
	}
	
	public boolean usingRightChopstick()
	{
		return usingrightstick;
	}
	
	// Return true if this philosopher is waiting for a chopstick and false otherwise
	// Used to detect deadlock
	public boolean isWaiting()
	{
		boolean waitingonLockValue = false;
		waitlock.lock();
		waitingonLockValue = waitingonLock;
		waitlock.unlock();
		return waitingonLockValue;
	}
	
    public void run() 
    {
    	long thinkTime;
    	long forkWaitTime;
    	long eatTime;
    	
    	// Scale all wait times to make then 10 times faster than specified in assignment
    	while (true)
    	{
    		thinkTime = (long) (1000*Math.random())+1;
    		try
    		{
    			Thread.sleep(thinkTime);
    			waitlock.lock();
    			waitingonLock = true;
    			waitlock.unlock();
    			leftsticklock.lockInterruptibly(); // want to ensure we can interrupt thread
    			waitlock.lock();
    			waitingonLock = false;
    			waitlock.unlock();
    			usingleftstick = true;
    			leftchopstick.useChopstick();    				    			
    		}
    		catch (InterruptedException ie)
    		{
    			break;
    		}
    		forkWaitTime = (long) 400;
    		try
    		{
    			Thread.sleep(forkWaitTime);
    			waitlock.lock();
    			waitingonLock = true;
    			waitlock.unlock();
    			rightsticklock.lockInterruptibly();
    			waitlock.lock();
    			waitingonLock = false;
    			waitlock.unlock();
    			usingrightstick = true;
    			rightchopstick.useChopstick();
    		}
    		catch (InterruptedException ie)
    		{
    			break;
    		}
    		eatTime = (long) (500*Math.random()+1);
    		try {    			
    			Thread.sleep(eatTime);
    		}
    		catch (InterruptedException ie)
    		{
    			break;
    		}
    		
    		if (!interrupted)
    		{
	    		leftchopstick.dropChopstick();
	    		usingleftstick = false;	    			
	    		if (leftsticklock.isHeldByCurrentThread())
	    		{
	    			leftsticklock.unlock();
	    		}		
	    		rightchopstick.dropChopstick();
	    		usingrightstick = false;
	    		if (rightsticklock.isHeldByCurrentThread())
	    		{
	    			rightsticklock.unlock();
	    		}
    		}		    		
    	}     	
    }
}
