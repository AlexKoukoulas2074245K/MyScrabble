package com.myscrabble.uicomponents;

import com.myscrabble.entities.Player;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.states.Play;

public class BWordSelection extends Button
{

	/* Standard button name */
	private static final String BUTTON_NAME = "wordSelection";
	
	private static final int X_OFFSET = 600;
	private static final int Y_OFFSET = 300;
	
	/* Texture Flags */
	private static final int PASS_NORMAL      = 0;
	private static final int PASS_PRESSED     = 1;
	private static final int VALID_NORMAL     = 2;
	private static final int VALID_PRESSED    = 3;
	
	private Play playStateRef;
	
	public BWordSelection(GameStateManager gsm, Play playStateRef)
	{
		super(BUTTON_NAME, gsm);
		
		this.playStateRef = playStateRef;
		
		status = INVALID;
		
		x = X_OFFSET;
		y = Y_OFFSET;
	}
	
	public void handleInput(Player playerRef)
	{
		super.handleInput(playerRef);
	}

	@Override
	public void executeFunction(Player playerRef)
	{
	    if(playerRef.isActive() && playerRef.isHuman())
	    {
	    	if(status == INVALID)
	    	{
	    		playerRef.withdrawAll();
	    	}
	        playStateRef.finaliseMove(status == INVALID);
	    }
	}
	
	@Override
	public void update(Player playerRef)
	{
		if(playerRef.hasValidWord())
		{
			status = VALID;
		}
		else
		{
			status = INVALID;
		}
	}
	
	@Override
	public void render()
	{
		selectTexture();
		super.render();
	}
	
	/**
	 * Selects adequate texture
	 * based on the button's boolean
	 * (highlighted, pressed) values.
	 */
	private void selectTexture()
	{
		if(pressed)
		{
			currentTexture = status == INVALID ? PASS_PRESSED : VALID_PRESSED;
		}
		else
		{
		    currentTexture = status == INVALID ? PASS_NORMAL : VALID_NORMAL;
		}
	}
}
