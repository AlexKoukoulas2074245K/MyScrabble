package com.myscrabble.states;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import ai.AIController.AILevel;

import com.myscrabble.entities.Board;
import com.myscrabble.entities.GameObject;
import com.myscrabble.entities.LetterBag;
import com.myscrabble.entities.Player;
import com.myscrabble.entities.TileRack;
import com.myscrabble.fx.Effect;
import com.myscrabble.fx.PassAnimation;
import com.myscrabble.main.Main;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.MouseManager;
import com.myscrabble.rendering.Shader;
import com.myscrabble.rendering.Shader.ShaderType;
import com.myscrabble.uicomponents.BWordSelection;
import com.myscrabble.uicomponents.Button;
import com.myscrabble.uicomponents.ScoreDisplay;
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
	public static final AILevel AI_LEVEL = AILevel.HARD;
	private static final String SHADING_FACTOR_NAME = "darknessParam";
	public static boolean LETTER_TILE_SLOWDOWN = false;
	
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
	
	/* Game Effects */
	private HashSet<Effect> effects;
	private HashSet<Effect> effectsToRemove;
	
	/* Instance of game Board */
	private Board board;
	
	/* Score Display instance */
	private ScoreDisplay scoreDisplay;
	
	/* Letter bag */
	private LetterBag letterBag;
	
	private Texture backgroundTexture;
	
	/* Dictionary Reference */
	private ScrabbleDictionary scrabbleDict;
	
	/* Current player index */
	private int activePlayer;
	
	/* First round flags */
	private boolean isFirstRound;
	
	//TODO: remove
	private Shader shader;
	private float darknessFactor;
	
	public Play(GameStateManager gsm)
	{
		super(gsm);
		
		activePlayer = 0;
        isFirstRound = true;
        
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
        players.add(new Player(gsm, this, board, scrabbleDict, letterBag, true));
        players.get(activePlayer).setActive(true);
        players.add(new Player(gsm, this, board, scrabbleDict, letterBag, false));

        gameObjects = new ArrayList<GameObject>();
        gameObjects.add(board);
        gameObjects.add(letterBag);
        
        buttons = new ArrayList<Button>();
        buttons.add(new BWordSelection(gsm, this));
        
        scoreDisplay = new ScoreDisplay(gsm.getRes(), players.get(0), players.get(1));
        
        playerPoints = new HashMap<Player, Integer>();
        
        effects = new HashSet<Effect>();
        effectsToRemove = new HashSet<Effect>();
        
        for(Player player : players)
        {
            playerPoints.put(player, 0);
        }
	}
	
	@Override
	public void handleInput() 
	{	
		for(Player player : players)
		{
			if(player.isHuman())
			{
				player.handleInput();
			}
		}
		
		for(Button b : buttons)
		{
			b.handleInput(getActivePlayer());
		}
		
		if(MouseManager.isButtonPressed(MouseManager.MIDDLE_BUTTON))
		{
		    System.out.println(MouseManager.getX() + ", " + MouseManager.getY());
		}
	}

	@Override
	public void update() 
	{	
		if(getActivePlayer().isHuman())
		{
			getActivePlayer().update();
		}
		else
		{
			getActivePlayer().updateAI();
		}
		
		for(Button button : buttons)
		{
			button.update(getActivePlayer());
		}
		
		for(GameObject go: gameObjects)
		{
			go.update();
		}
		
		for(Effect effect : effects)
		{
			effect.update();
			
			if(effect.isFinished())
			{
				effectsToRemove.add(effect);
			}
		}
		
		if(effectsToRemove.size() > 0)
		{
			for(Effect effect : effectsToRemove)
			{
				effects.remove(effect);
			}
			
			effectsToRemove.clear();
		}
		
		scoreDisplay.update();
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
		
		for(Effect effect : effects)
		{
			effect.render();
		}
		
		scoreDisplay.render();
		
		clearShading();
	}
	
	/**
	 * Request to move to the next
	 * turn. Addition of points to the
	 * respective player is also done
	 * in this stage.
	 */
	public void finaliseMove(boolean passed)
	{
		if(passed)
		{
			effects.add(new PassAnimation(gsm.getRes(), getActivePlayer().getName()));
		}
		
	    scoreDisplay.addPoints(getActivePlayer().getCurrentPoints(), getActivePlayer());
	    getActivePlayer().makeMove();
	    endOfPlayersTurn();
	}
	
	private void endGame()
	{
	    finished = true;
	}
	
	/**
	 * Ends the active player's turn
	 * and moves on to the next one.
	 * Also resets the drawing allowance
	 * of the next active player
	 */
	private void endOfPlayersTurn()
	{
	    getActivePlayer().setActive(false);
	    activePlayer++;
	    
	    if(activePlayer > players.size() - 1)
	    {
	        activePlayer = 0;
	    }
	    
	    getActivePlayer().setActive(true);
	    int noTiles = getActivePlayer().getNoTiles();
	    
	    getActivePlayer().setDrawingAllowance(TileRack.MAX_NO_TILES - noTiles);
	    
	    if(!getActivePlayer().isHuman())
	    {
	    	getActivePlayer().drawAllAI();
	    }
	    
	    if(board.isFirstRound())
	    {
	        board.setFirstRound(false);
	    }
	}
	
	/**
	 * Uses the shading program for
	 * all entities that are rendered on screen
	 * and sets appropriately the darkness factor
	 * uniform.
	 */
	private void applyShading()
	{
		shader.useProgram();
		shader.setUniform3f(SHADING_FACTOR_NAME, new float[]{darknessFactor, darknessFactor, darknessFactor});
	}
	
	/**
	 * Stops the usage of the shading program
	 */
	private void clearShading()
	{
		shader.stopProgram();
	}
	
	/**
	 * 
	 * @return the currently
	 * active player of the game
	 */
	private Player getActivePlayer()
	{
	    return players.get(activePlayer);
	}    
}
