package com.myscrabble.main;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A class representing a window configuration that is used
 * to set the default Display Mode of lwjgl. A custom configuration
 * from a cfg file may be supplied
 */

public class WindowConfiguration 
{
	public static final String TITLE = "My Scrabble";
	public static final boolean RESIZABLE = false;
	
	public static final int DEFAULT_WIDTH = 760;
	public static final int DEFAULT_HEIGHT = 640;	
	public static final int DEFAULT_FRAME_CAP = 60;
	public static final boolean DEFAULT_VSYNC = true;
	
	private int width;
	private int height;
	private int fps;
	private String title;
	private boolean resizable;
	private boolean vsync;
	
	public WindowConfiguration()
	{
		this.title = TITLE;
		this.width = DEFAULT_WIDTH;
		this.height = DEFAULT_HEIGHT;
		this.fps = DEFAULT_FRAME_CAP;
		this.resizable = RESIZABLE;
		this.vsync = DEFAULT_VSYNC;
	}
	
	public boolean getResizable()
	{
		return resizable;
	}
	
	public boolean getVsync()
	{
		return vsync;
	}
	
	public float getAspect()
	{
		return (float) width / (float) height;
	}
	
	public int getWidth() 
	{
		return width;
	}

	public int getHeight() 
	{
		return height;
	}
	
	public int getFps()
	{
		return fps;
	}
	
	public String getTitle()
	{
		return title;
	}
}
