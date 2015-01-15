package com.myscrabble.util;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A simple class representing a timer/cool down/delay
 * for any purpose
 */
public class Timer
{
	private int timer;
	private boolean isFinished;
	
	public Timer(int delay)
	{
		this.timer = delay;
		isFinished = false;
	}
	
	public void update()
	{
		timer--;
		if(timer == 0)
		{
			isFinished = true;
		}
	}
	
	public boolean isFinished()
	{
		return isFinished;
	}
	
	public int getTimeLeft()
	{
		return timer;
	}
}
