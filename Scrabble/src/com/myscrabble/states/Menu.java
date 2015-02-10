package com.myscrabble.states;

import com.myscrabble.entities.Board;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.KeyboardManager;
import com.myscrabble.user.UserProfile;

/**
 * @author Alex Koukoulas
 * Class Description:
 */

public class Menu extends GameState
{
    private UserProfile currentUserProfile;
    
	public Menu(GameStateManager gsm)
	{
		super(gsm);
		
		currentUserProfile = new UserProfile("Alex");
	}

	@Override
	public void handleInput() 
	{
		if(KeyboardManager.isKeyDown(KeyboardManager.K_SPACE))
		{
		    finished = true;
		}
	}

	@Override
	public void update() 
	{
		
	}

	@Override
	public void render() 
	{
		
	}

    @Override
    public boolean isPaused()
    {
        return false;
    }
    
    @Override
    public UserProfile getCurrentUser()
    {
        return currentUserProfile;
    }
}
