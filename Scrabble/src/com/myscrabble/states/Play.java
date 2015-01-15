package com.myscrabble.states;

import java.util.ArrayList;

import com.myscrabble.entities.Board;
import com.myscrabble.entities.GameObject;
import com.myscrabble.entities.LetterBag;
import com.myscrabble.entities.Player;
import com.myscrabble.entities.TileIndicator;
import com.myscrabble.managers.GameStateManager;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 */

public class Play extends GameState
{
	/* TEMP */
	public static final int NO_PLAYERS = 1;
	public static final int TILE_STYLE = 1;
	/* All the GameObjects that need to be drawn and 
	 * updated on screen
	 */
	private ArrayList<GameObject> gameObjects;
	
	/* All active players */
	private ArrayList<Player> players;
	
	/* Instance of game Board */
	private Board board;
	
	/* Letter bag */
	private LetterBag letterBag;
	
	public Play(GameStateManager gsm)
	{
		super(gsm);
		
		board = new Board(gsm);
		players = new ArrayList<Player>();
		players.add(new Player(gsm, board));
		
		gameObjects = new ArrayList<GameObject>();
		gameObjects.add(board);
		gameObjects.add(new LetterBag(gsm));		
	}

	@Override
	public void handleInput() 
	{	
		for(Player player: players)
		{
			if(player.isActive())
			{
				player.handleInput();
			}
		}
	}

	@Override
	public void update() 
	{	
		for(Player player: players)
		{
			player.update();
		}
		
		for(GameObject go: gameObjects)
		{
			go.update();
		}
	}

	@Override
	public void render() 
	{	
		for(GameObject go: gameObjects)
		{
			go.render();
		}
		
		for(Player player: players)
		{
			player.render();
		}
	}
}
