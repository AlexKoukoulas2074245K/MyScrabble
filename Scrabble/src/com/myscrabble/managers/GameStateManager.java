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
		InputManager.update();
		states.peek().handleInput();
		InputManager.listenToInput();
	}
	
	public void update()
	{
		states.peek().update();
		
		if(states.peek().getFinished())
		{
			currentState ++;
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
}
