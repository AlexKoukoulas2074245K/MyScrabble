package com.myscrabble.states;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

import com.myscrabble.entities.Board;
import com.myscrabble.entities.GameObject;
import com.myscrabble.entities.LetterBag;
import com.myscrabble.entities.Player;
import com.myscrabble.entities.TileIndicator;
import com.myscrabble.main.Main;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.util.RenderUtils;
import com.myscrabble.util.ScrabbleDictionary;

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
	private static final String BG_DIR = "/board/boardBackgrounds/wood.png";
	
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
	
	private Texture backgroundTexture;
	
	/* Dictionary Reference */
	private ScrabbleDictionary scrabbleDict;
	 
	public Play(GameStateManager gsm)
	{
		super(gsm);
		
		scrabbleDict = new ScrabbleDictionary(gsm.getRes());
		
		board = new Board(gsm);
		players = new ArrayList<Player>();
		players.add(new Player(gsm, board, scrabbleDict));
		
		gameObjects = new ArrayList<GameObject>();
		gameObjects.add(board);
		gameObjects.add(new LetterBag(gsm));
		
		//TODO: remove
		backgroundTexture = gsm.getRes().loadTexture(BG_DIR);
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
		RenderUtils.renderTexture(backgroundTexture, 0, 0, 
								  Main.getNormalDimensions()[0], 
								  Main.getNormalDimensions()[1]);
		
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
