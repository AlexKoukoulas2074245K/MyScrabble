package com.myscrabble.uicomponents;

import com.myscrabble.entities.Player;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.MouseManager;

public class BWordSelection extends Button
{

	/* Standard button name */
	private static final String BUTTON_NAME = "wordSelection";
	
	private static final int X_OFFSET = 604;
	private static final int Y_OFFSET = 400;
	
	/* Texture Flags */
	private static final int INVALID_NORMAL    = 0;
	private static final int INVALID_HIGHLIGHT = 1;
	private static final int INVALID_PRESSED   = 2;
	private static final int VALID_NORMAL      = 3;
	private static final int VALID_HIGHLIGHT   = 4;
	private static final int VALID_PRESSED     = 5;
	
	private String currentWordSelection;
	private int currentWordPoints;
	
	public BWordSelection(GameStateManager gsm)
	{
		super(BUTTON_NAME, gsm);
		currentTexture = INVALID_NORMAL;
		
		x = X_OFFSET;
		y = Y_OFFSET;
	}
	
	public void handleInput()
	{
		if(status == INVALID) return;
		
		int mouseX = MouseManager.getX();
		int mouseY = MouseManager.getY();
		
		if(getRect().contains(mouseX, mouseY))
		{
			highlighted = true;
			
			if(MouseManager.isButtonPressed(MouseManager.LEFT_BUTTON))
			{
				pressed = true;
			}
			else if(MouseManager.isButtonReleased(MouseManager.LEFT_BUTTON) && pressed)
			{
				executeFunction();
				pressed = false;
				highlighted = false;
			}
		}
		else
		{
			highlighted = false;
			
			if(MouseManager.isButtonReleased(MouseManager.LEFT_BUTTON))
			{
				pressed = false;
			}
		}
	}

	@Override
	public void executeFunction()
	{
	    System.out.println(currentWordSelection + ": " + currentWordPoints);
	}
	
	@Override
	public void update(Player playerRef)
	{
		if(playerRef.hasValidWord())
		{
		    currentWordSelection = playerRef.getCurrentWord();
		    currentWordPoints = playerRef.getCurrentPoints();
		    
			status = VALID;
		}
		else
		{
		    currentWordSelection = null;
		    currentWordPoints = 0;
			status = INVALID;
		}
	}
	
	@Override
	public void render()
	{
		selectTexture();
		super.render();
	}
	
	private void selectTexture()
	{
		if(highlighted)
		{
			currentTexture = status == VALID ? VALID_HIGHLIGHT : INVALID_HIGHLIGHT;
		}
		if(pressed)
		{
			currentTexture = status == VALID ? VALID_PRESSED : INVALID_PRESSED;
		}
		
		if(!highlighted && !pressed)
		{
			currentTexture = status == VALID ? VALID_NORMAL : INVALID_NORMAL;
		}
	}
}
