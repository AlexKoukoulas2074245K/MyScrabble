package com.myscrabble.states;


import java.util.ArrayList;
import java.util.HashMap;

import com.myscrabble.uicomponents.Button;
import com.myscrabble.uicomponents.BWordSelection;

import org.newdawn.slick.opengl.Texture;

import com.myscrabble.entities.Board;
import com.myscrabble.entities.GameObject;
import com.myscrabble.entities.LetterBag;
import com.myscrabble.entities.Player;
import com.myscrabble.entities.TileRack;
import com.myscrabble.main.Main;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.rendering.Shader;
import com.myscrabble.rendering.Shader.ShaderType;
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
	
	private static final String SHADING_FACTOR_NAME = "darknessParam";
	
	/* All the GameObjects that need to be drawn and 
	 * updated on screen
	 */
	private ArrayList<GameObject> gameObjects;
	
	/* All active players */
	private ArrayList<Player> players;
	
	/* All active buttons */
	private ArrayList<Button> buttons;
	
	/* Player Points */
	private HashMap<Player, Integer> playerPoints;
	
	/* Instance of game Board */
	private Board board;
	
	/* Letter bag */
	private LetterBag letterBag;
	
	private Texture backgroundTexture;
	
	/* Dictionary Reference */
	private ScrabbleDictionary scrabbleDict;
	
	/* Current player index */
	private int activePlayer;
	
	//TODO: remove
	private Shader shader;
	private float darknessFactor;
	
	public Play(GameStateManager gsm)
	{
		super(gsm);
		activePlayer = 0;
        
		initCoreEntities();
		
		//TODO: remove
		backgroundTexture = gsm.getRes().loadTexture(BG_DIR);
		
		shader = new Shader(ShaderType.SHADING);
		
		darknessFactor = 1.0f;
	}

	private void initCoreEntities()
	{
	    scrabbleDict = new ScrabbleDictionary();
        board = new Board(gsm);
        letterBag = new LetterBag(gsm);
        
        
        players = new ArrayList<Player>();
        players.add(new Player(gsm, board, scrabbleDict, letterBag, true));
        players.get(activePlayer).setActive(true);
        players.add(new Player(gsm, board, scrabbleDict, letterBag, false));
        
        gameObjects = new ArrayList<GameObject>();
        gameObjects.add(board);
        gameObjects.add(letterBag);
        
        buttons = new ArrayList<Button>();
        buttons.add(new BWordSelection(gsm, this));
        
        playerPoints = new HashMap<Player, Integer>();
        
        for(Player player : players)
        {
            playerPoints.put(player, 0);
        }
	}
	
	@Override
	public void handleInput() 
	{	
		
		getActivePlayer().handleInput();
		
		for(Button b : buttons)
		{
			b.handleInput();
		}
	}

	@Override
	public void update() 
	{	
		getActivePlayer().update();
		
		for(Button button : buttons)
		{
			button.update(getActivePlayer());
		}
		
		for(GameObject go: gameObjects)
		{
			go.update();
		}
	}

	@Override
	public void render() 
	{
		
		applyShading();
		
		RenderUtils.renderTexture(backgroundTexture, 0, 0, 
								  Main.getNormalDimensions()[0], 
								  Main.getNormalDimensions()[1]);
		
		
		for(Button button : buttons)
		{
			button.render();
		}
		
		for(GameObject go: gameObjects)
        {
            go.render();
        }
		
		for(Player player: players)
		{
			player.render();
		}
		
		clearShading();	
	}
	
	public void finaliseMove()
	{
	    int claimedPoints = getActivePlayer().getCurrentPoints();
	    addPoints(getActivePlayer(), claimedPoints);
	    getActivePlayer().makeMove();
	    endOfPlayersTurn();
	}
	
	private void addPoints(Player player, int points)
	{
	    int currentPoints = playerPoints.get(player);
	    playerPoints.replace(player, currentPoints + points);
	}
	
	private void endOfPlayersTurn()
	{
	    getActivePlayer().setActive(false);
	    activePlayer = 0;//activePlayer++;
	    if(activePlayer > players.size() - 1)
	    {
	        activePlayer = 0;
	    }
	    getActivePlayer().setActive(true);
	    int noTiles = getActivePlayer().getNoTiles();
	    
	    getActivePlayer().setDrawingAllowance(TileRack.MAX_NO_TILES - noTiles);
	}
	
	private void applyShading()
	{
		shader.useProgram();
		shader.setUniform3f(SHADING_FACTOR_NAME, new float[]{darknessFactor, darknessFactor, darknessFactor});
	}
	
	private void clearShading()
	{
		shader.stopProgram();
	}
	
	private Player getActivePlayer()
	{
	    return players.get(activePlayer);
	}    
}
