package com.myscrabble.managers;

import java.util.Stack;

import com.myscrabble.states.GameState;
import com.myscrabble.states.Menu;
import com.myscrabble.states.Play;
import com.myscrabble.user.UserProfile;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * The GameStateManager handles all the transitions between game states.
 * A stack is used to push and pop states as well as update and render them.
 */

public class GameStateManager 
{
	public static final int MENU = 0;
	public static final int PLAY = 1;
	public static final int N_STATES = 2;
	
	private ResourceManager res;
	private SoundManager soundManager;
	
	private Stack<GameState> states;
	private int currentState;
	
	public GameStateManager()
	{
		res = new ResourceManager();
		soundManager = new SoundManager(res);
		soundManager.disable();
		
		states = new Stack<GameState>();
		currentState = MENU;
		
		pushState(currentState);
	}
	
	public void handleInput()
	{
		MouseManager.listenToInput();
		KeyboardManager.listenToInput();
		states.peek().handleInput();
		MouseManager.update();
		KeyboardManager.update();
	}
	
	public void update()
	{
		states.peek().update();
		
		if(states.peek().getFinished())
		{
			currentState = (currentState + 1) % N_STATES;
			pushState(currentState);
		}
	}
	
	public void render()
	{
		states.peek().render();
	}
	
	public void pushState(int state)
	{
	    UserProfile currentUser = null;
	    
	    if(states.size() > 0)
	    {
	        currentUser = states.peek().getCurrentUser();
	    }
	    
		states.clear();
		
		if(state == MENU)
		{	
			states.push(new Menu(this, currentUser));
		}
		
		else if(state == PLAY)
		{
			states.push(new Play(this, currentUser));
		}
	}
	
	public ResourceManager getRes()
	{
		return res;
	}
	
	public SoundManager getSoundManager()
	{
		return soundManager;
	}
	
	public GameState getCurrentState()
	{
	    return states.peek();
	}
	
	public boolean isCurrenttStatePaused()
	{
	    return states.peek().isPaused();
	}
}
