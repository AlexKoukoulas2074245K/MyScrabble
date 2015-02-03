package com.myscrabble.managers;

import java.util.Stack;

import com.myscrabble.states.GameState;
import com.myscrabble.states.Menu;
import com.myscrabble.states.Play;

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
	private Stack<GameState> states;
	private int currentState;
	
	public GameStateManager()
	{
		res = new ResourceManager();
		
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
		states.clear();
		
		if(state == MENU)
		{	
			states.push(new Menu(this));
		}
		
		else if(state == PLAY)
		{
			states.push(new Play(this));	
		}
	}
	
	public ResourceManager getRes()
	{
		return res;
	}
	
	public boolean isCurrenttStatePaused()
	{
	    return states.peek().isPaused();
	}
}
