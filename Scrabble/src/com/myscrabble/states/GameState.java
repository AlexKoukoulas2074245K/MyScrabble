package com.myscrabble.states;

import com.myscrabble.managers.GameStateManager;
import com.myscrabble.user.UserProfile;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * An abstract representation of a game state
 * Contains common methods that need to be implemented
 * by all game states.
 */

public abstract class GameState 
{
	protected GameStateManager gsm;
	protected boolean finished;
	
	protected GameState(GameStateManager gsm)
	{
		this.gsm = gsm;
		finished = false;
	}
	
	public abstract void handleInput();
	public abstract void update();
	public abstract void render();
	public abstract UserProfile getCurrentUser();
	
	public boolean getFinished()
	{
		return finished;
	}
	
	public abstract boolean isPaused();
}
