package com.myscrabble.uicomponents;

import com.myscrabble.entities.Player;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.MouseManager;

public class BWordSelection extends Button
{

	/* Standard button name */
	private static final String BUTTON_NAME = "wordSelection";
	
	private static final int X_OFFSET = 612;
	private static final int Y_OFFSET = 340;
	
	/* Texture Flags */
	private static final int INVALID           = -1;
	private static final int VALID_NORMAL      = 0;
	private static final int VALID_HIGHLIGHT   = 1;
	private static final int VALID_PRESSED     = 2;
	
	private String currentWordSelection;
	private int currentWordPoints;
	
	public BWordSelection(GameStateManager gsm)
	{
		super(BUTTON_NAME, gsm);
		
		status = INVALID;
		
		x = X_OFFSET;
		y = Y_OFFSET;
	}
	
	public void handleInput()
	{
		if(status == INVALID) return;
		
		super.handleInput();
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
		if(status == VALID)
		{
			selectTexture();
			super.render();
		}
	}
	
	/**
	 * Selects adequate texture
	 * based on the button's boolean
	 * (highlighted, pressed) values.
	 */
	private void selectTexture()
	{
		if(highlighted)
		{
			currentTexture = VALID_HIGHLIGHT;
		}
		if(pressed)
		{
			currentTexture = VALID_PRESSED;
		}
		
		if(!highlighted && !pressed)
		{
			currentTexture = VALID_NORMAL;
		}
	}
}
