package com.myscrabble.util;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A class that handles animation of gameObjects. It holds
 * all the frames-to-render in an ArrayList and process them
 * depending on the animation delay provided
 */

public class Animation 
{
	private ArrayList<Texture> frames;
	private float aniDelay;
	private float maxAniDelay;
	private int currentFrame;
	
	private boolean playedOnce;	
	
	public Animation(final ArrayList<Texture> frames, final float aniDelay)
	{
		this(frames, aniDelay, true);
	}
	
	public Animation(final ArrayList<Texture> frames, final float aniDelay, final boolean loop)
	{
		this.frames = frames;
		this.aniDelay = aniDelay;
		this.maxAniDelay = aniDelay;
		
		playedOnce = false;
		currentFrame = 0;
	}
	
	public void update()
	{
		aniDelay--;
		if(aniDelay == 0)
		{
			aniDelay = maxAniDelay;
			currentFrame = (currentFrame + 1) % frames.size();
			
			if(currentFrame == 0)
			{
			    playedOnce = true;
			}
		}
	}
	
	public Texture getCurrentFrame()
	{
		return frames.get(currentFrame);
	}
	
	public boolean getPlayedOnce()
	{
		return playedOnce;
	}
}	
