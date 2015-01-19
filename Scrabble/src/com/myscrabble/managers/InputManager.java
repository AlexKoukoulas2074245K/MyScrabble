package com.myscrabble.managers;

import org.lwjgl.input.Mouse;

import com.myscrabble.main.Main;

public class InputManager
{
	public static final int MAX_BUTTONS   = 3;
	
	public static final int LEFT_BUTTON   = 0;
	public static final int RIGHT_BUTTON  = 1;
	public static final int MIDDLE_BUTTON = 2;
	
	private static boolean[] buttonsDown = new boolean[MAX_BUTTONS];
	private static boolean[] buttonsPrev = new boolean[MAX_BUTTONS];

	public static void update()
	{
		for(int i = 0; i < MAX_BUTTONS; i++)
		{
			buttonsPrev[i] = buttonsDown[i];
		}
	}
	
	public static boolean thereAreButtonsPressed()
	{
		for(int i = 0; i < MAX_BUTTONS; i++)
		{
			if(isButtonDown(i))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean isButtonDown(int button)
	{
		return buttonsDown[button];
	}
	
	public static boolean isButtonPressed(int button)
	{
		return buttonsDown[button] && !buttonsPrev[button];
	}
	
	public static boolean isButtonReleased(int button)
	{
		return !buttonsDown[button];
	}
	
	public static int getX()
	{
		return Mouse.getX();
	}
	
	public static int getY()
	{
		return (int)Main.getNormalDimensions()[1] - Mouse.getY();
	}
	
	public static int getDX()
	{
		return Mouse.getDX();
	}
	
	public static int getDY()
	{
		return Mouse.getDY();
	}
	
	/**
	 * Listens to input from the Lwjgl Mouse input adapter
	 */
	public static void listenToInput()
	{
		if(Mouse.isButtonDown(LEFT_BUTTON))
		{
			buttonsDown[LEFT_BUTTON] = true;
		}
		else
		{
			buttonsDown[LEFT_BUTTON] = false;
		}
		
		if(Mouse.isButtonDown(RIGHT_BUTTON))
		{
			buttonsDown[RIGHT_BUTTON] = true;
		}
		else
		{
			buttonsDown[RIGHT_BUTTON] = false;
		}
		
		if(Mouse.isButtonDown(MIDDLE_BUTTON))
		{
			buttonsDown[MIDDLE_BUTTON] = true;
		}
		else
		{
			buttonsDown[MIDDLE_BUTTON] = false;
		}
	}
}
