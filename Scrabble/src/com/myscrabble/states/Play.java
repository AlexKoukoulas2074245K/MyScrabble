package com.myscrabble.states;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.newdawn.slick.TrueTypeFont;

import com.myscrabble.ai.AIController.AILevel;
import com.myscrabble.entities.Board;
import com.myscrabble.entities.GameObject;
import com.myscrabble.entities.LetterBag;
import com.myscrabble.entities.Player;
import com.myscrabble.entities.TileRack;
import com.myscrabble.main.Main;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.KeyboardManager;
import com.myscrabble.managers.MouseManager;
import com.myscrabble.rendering.Shader;
import com.myscrabble.rendering.Shader.ShaderType;
import com.myscrabble.uicomponents.BWordSelection;
import com.myscrabble.uicomponents.Button;
import com.myscrabble.uicomponents.PauseMenu;
import com.myscrabble.uicomponents.ScoreDisplay;
import com.myscrabble.user.UserProfile;
import com.myscrabble.util.ScrabbleDictionary;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 */

public class Play extends GameState
{
	/**
	 * Uses the shading program for
	 * all entities that are rendered on screen
	 * and sets appropriately the darkness factor
	 * uniform.
	 */
	public static void applyShading()
	{
		shader.useProgram();
		shader.setUniform3f(SHADING_FACTOR_NAME, new float[]{darknessFactor, darknessFactor, darknessFactor});
	}
	
	/**
	 * Stops the usage of the shading program
	 */
	public static void clearShading()
	{
		shader.stopProgram();
	}
	
    /* public use shader and uniform */
    public static Shader shader = new Shader(ShaderType.SHADING);
    public static float darknessFactor = 1.0f;
    
    /* Shading variables and uniform intervals */
    public static final String SHADING_FACTOR_NAME = "darknessFactor";
    private static final float MAX_DARKNESS_FACTOR = 1.0f;
    private static final float MIN_DARKNESS_FACTOR = MAX_DARKNESS_FACTOR / 3f;
    private static final float DARKNESS_INTERVALS  = 0.045f;
    
	/* TEMP */
	public static final int NO_PLAYERS = 1;
	public static final int TILE_STYLE = 1;
	public static final AILevel AI_LEVEL = AILevel.HARD;

	private static final String FONT_NAME   = "font_bold";
	private static final int FONT_SIZE      = 32;
	private static final boolean FONT_ALIAS = true;
	private static final float PASS_RED_INTERVAL = 0.02f;
	private static final float[] AI_PASS_POS  = new float[]{470, 15};
	private static final float[] HUM_PASS_POS = new float[]{470, Main.getNormalDimensions()[1] - 50};
	
	/* Reference to the profile of the user currently playing */
	private UserProfile currentUserProfile;
	
	/* All the GameObjects that need to be drawn and 
	 * updated on screen
	 */
	private ArrayList<GameObject> gameObjects;
	
	/* The pause menu */
	private PauseMenu pauseMenu;
	 
	/* All active players */
	private ArrayList<Player> players;
	
	/* Pass text time for each player */
	private HashMap<Player, Float> playerPassTime;
	
	/* All active buttons */
	private ArrayList<Button> buttons;
	
	/* Player Points */
	private HashMap<Player, Integer> playerPoints;
	
	/* Instance of game Board */
	private Board board;
	
	/* Score Display instance */
	private ScoreDisplay scoreDisplay;
	
	/* Letter bag */
	private LetterBag letterBag;
	
	/* Dictionary Reference */
	private ScrabbleDictionary scrabbleDict;
	
	/* Current player index */
	private int activePlayer;
	
	/* Time accumulator for the player */
	private int timeAccum;
	private int frameCounter;
	
	/* Font to alert about passes */
	private TrueTypeFont passFont;
	
	public Play(GameStateManager gsm, UserProfile userProfile)
	{
		super(gsm);
		
		this.currentUserProfile = userProfile;
		
		activePlayer = 0;
        
		initCoreEntities();
		
		darknessFactor = 0.0f;
		timeAccum = 0;
		frameCounter = 0;
	}

	private void initCoreEntities()
	{
	    scrabbleDict = new ScrabbleDictionary();
        board = new Board(gsm, currentUserProfile.getLastBackgroundUsed());
        letterBag = new LetterBag(gsm);
        
        players = new ArrayList<Player>();
        players.add(new Player(gsm, this, board, scrabbleDict, letterBag, true, currentUserProfile.getName()));
        players.get(activePlayer).setActive(true);
        players.add(new Player(gsm, this, board, scrabbleDict, letterBag, false));

        gameObjects = new ArrayList<GameObject>();
        gameObjects.add(board);
        gameObjects.add(letterBag);
        
        buttons = new ArrayList<Button>();
        buttons.add(new BWordSelection(gsm, this));
        
        scoreDisplay = new ScoreDisplay(gsm.getRes(), players.get(0), players.get(1));
        
        playerPassTime = new HashMap<Player, Float>();
        playerPoints = new HashMap<Player, Integer>();
        
        for(Player player : players)
        {
            playerPoints.put(player, 0);
            playerPassTime.put(player, 0.0f);
        }
        
        pauseMenu = new PauseMenu(gsm);
        
        passFont = gsm.getRes().loadFont(FONT_NAME, FONT_SIZE, FONT_ALIAS);
	}
	
	@Override
	public void handleInput() 
	{
	    if(pauseMenu.isActive())
	    {
	        pauseMenu.handleInput();
	        return;
	    }
	    
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
		
		if(KeyboardManager.isKeyPressed(KeyboardManager.K_ESCAPE))
		{
		    pauseMenu.setActive(true);
		}
		
		if(MouseManager.isButtonPressed(MouseManager.MIDDLE_BUTTON))
		{
		    System.out.println(MouseManager.getX() + ", " + MouseManager.getY());
		}
	}

	@Override
	public void update() 
	{	
	    frameCounter++;
	    if(frameCounter == Main.getFPS())
	    {
	        timeAccum++;
	        frameCounter = 0;
	    }
	    
	    if(pauseMenu.isActive())
	    {
	        if(darknessFactor > MIN_DARKNESS_FACTOR)
	        {
	            darknessFactor -= DARKNESS_INTERVALS;
	        }
	        
	        pauseMenu.update();
	        
	        if(pauseMenu.getMainMenuRequest())
	        {
	        	finished = true;
	        	currentUserProfile.addTimePlayed(timeAccum);
	        	timeAccum = 0;
	        }
	        
	        return;
	    }
	    else
	    {
	        if(darknessFactor < MAX_DARKNESS_FACTOR)
	        {
	            darknessFactor += DARKNESS_INTERVALS;
	        }
	    }
	    
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
		
		for(Entry<Player, Float> entry : playerPassTime.entrySet())
		{
			if(entry.getValue() > 0.0f)
			{
				playerPassTime.replace(entry.getKey(), entry.getValue() - PASS_RED_INTERVAL);
			}
		}
		
		scoreDisplay.update();
	}
	
	@Override
	public void render() 
	{
		applyShading();
		board.renderBackground();
		clearShading();
		
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
		
		for(Entry<Player, Float> entry : playerPassTime.entrySet())
		{
			if(entry.getValue() > 0.0f && entry.getKey().isHuman())
			{
				passFont.drawString(HUM_PASS_POS[0], HUM_PASS_POS[1], "I pass..");
			}
			else if(entry.getValue() > 0.0f && !entry.getKey().isHuman())
			{
				passFont.drawString(AI_PASS_POS[0], AI_PASS_POS[1], "I pass..");
			}
		}
		
		scoreDisplay.render();
		
		if(pauseMenu.isActive())
		{
		    pauseMenu.render();
		}
	}
	
	@Override
	public UserProfile getCurrentUser()
	{
	    return currentUserProfile;
	}
	
	@Override
	public boolean isPaused()
	{
	    return pauseMenu.isActive();
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
		    if(board.isFirstRound())
		    {
		        System.out.println("Can't pass on the first turn");
		        return;
		    }
		    
			playerPassTime.replace(getActivePlayer(), 1.0f);
		}
		
	    scoreDisplay.addPoints(getActivePlayer().getCurrentPoints(), getActivePlayer());
	    getActivePlayer().makeMove();
	    endOfPlayersTurn();
	    checkForGameOver();
	}
	
	public boolean isFirstRound()
	{
	    return board.isFirstRound();
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
	
	private void checkForGameOver()
	{
	    if(letterBag.hasRunOut())
	    {
	    	for(Entry<Player, Integer> entry : playerPoints.entrySet())
	    	{
	    		if(entry.getKey().isHuman())
	    		{
	    			currentUserProfile.addTokens(entry.getValue());
	    		}
	    	}
	        finished = true;
	        currentUserProfile.addTimePlayed(timeAccum);
	        timeAccum = 0;
	    }
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
	
	public int getTimeElapsed()
	{
	    return timeAccum;
	}
}
