package com.myscrabble.entities;


import java.util.ArrayList;
import com.myscrabble.ai.AIController;
import com.myscrabble.ai.AIController.AIState;
import com.myscrabble.entities.LetterTile.Direction;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.MouseManager;
import com.myscrabble.managers.SoundManager.SoundType;
import com.myscrabble.states.Play;
import com.myscrabble.util.ScrabbleDictionary;
import com.myscrabble.util.Timer;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A class that holds the information of 
 * a player in the game
 */

public class Player
{
	/* A cool down for the player's selection (to avoid spamming left click on tile rack) */
	private static final int SELECTION_COOLDOWN = 60; /* measured in frames */
	private static final String AI_NAME = "AI Player";
	
	/* Sound effect constant */
	private static final String SFX_RELEASE_NAME = "tileRelease";
	
	/* Game State Manager reference */
	private GameStateManager gsm;
	
	/* Name of the player */
	private String name;
	
	/* Reference to the current play state */
	private Play playStateRef;
	
	/* An instance of AIController in case
	 * this player is controlled by the computer */
	private AIController aiController;
	
	/* The currently selected letter tile */
	private LetterTile selLetterTile;
	
	/* A reference to the game board */
	private Board board;
	
	/* The player's tile rack */
	private TileRack tileRack;
	
	/* The timer/cool down of player selection */ 
	private Timer selectionTimer;
	
	/* Reference to the dictionary */
	private ScrabbleDictionary scrabbleDict;
	
	/* Reference to the letterBag used by all players */
	private LetterBag letterBag;
	
	/* Is it this player's turn */
	private boolean isActive;
	
	/* Is this a human controlled player */
	private boolean isHuman;
	
	/* Drawing allowance of the player */
	private int drawingAllowance;
	
	public Player(GameStateManager gsm, Play playStateRef, Board board,
				  ScrabbleDictionary scrabbleDict, LetterBag letterBag, boolean isHuman)
	{
		this(gsm, playStateRef, board, scrabbleDict, letterBag, isHuman, AI_NAME);
	}
	public Player(GameStateManager gsm, Play playStateRef, Board board, 
				  ScrabbleDictionary scrabbleDict, LetterBag letterBag, boolean isHuman, String name)
	{
		this.gsm = gsm;
		this.playStateRef = playStateRef;
		this.board = board;
		this.scrabbleDict = scrabbleDict;
		this.letterBag = letterBag;
		this.isHuman = isHuman;
		this.name = name;
		
		aiController = new AIController(Play.AI_LEVEL, this, board, scrabbleDict);
		tileRack = new TileRack(gsm, this, letterBag);
		
		isActive = false;
		
		selLetterTile = null;
		drawingAllowance = 0;
		
		selectionTimer = new Timer(SELECTION_COOLDOWN);
		
		gsm.getSoundManager().loadClip(SFX_RELEASE_NAME, SoundType.SOUND_EFFECT);
	}
	
	public void handleInput()
	{	 
		/* Response of mouse hovering over the tile rack or game board holding a letter tile */
		if(hasSelectedLetterTile() && MouseManager.isButtonDown(MouseManager.LEFT_BUTTON))
		{
			checkAreaHovering();
		}
		/* Release of letter tile to either the rack or the game board*/
		else if(hasSelectedLetterTile() && MouseManager.isButtonReleased(MouseManager.LEFT_BUTTON))
		{
			releaseLetterTile();
		}
		/* Release of letter tile from game board to the tile rack */
		else if(!hasSelectedLetterTile() && MouseManager.isButtonDown(MouseManager.RIGHT_BUTTON))
		{
			checkForBoardWithdrawal();
		}
		/* If no letter tile is held letter tiles in the rack, the game board and the letter bag  
		 * should respond to the mouse movement */
		else if(!hasSelectedLetterTile())
		{
			highlightLetters();
			checkForSelection();
			checkForBoardInteraction();
			checkForBagHovering();
		}
		
		/* Check for letter drawing request*/
		if(!hasSelectedLetterTile() && MouseManager.isButtonPressed(MouseManager.LEFT_BUTTON))
		{
			checkForLetterDrawAttempt();
		}
	}
	
	public void updateAI()
	{
		aiController.update();
		
		if(aiController.getState() == AIState.FINISHING)
		{
			playStateRef.finaliseMove(false);
			aiController.setState(AIState.WORD_SELECTION);
		}
		else if(aiController.getState() == AIState.PASS)
		{
			pass();
			aiController.setState(AIState.WORD_SELECTION);
		}
	}
	
	public void update()
	{
		tileRack.update();
		
		if(tileRack.getTilesAnimating())
		{
			return;
		}
		
		if(hasSelectedLetterTile())
		{
			selLetterTile.update();
		}
		
		if(selectionTimer != null)
		{
			selectionTimer.update();
		}
	}
	
    public void render()
    {
        Play.applyShading();
        tileRack.renderBack();
        tileRack.renderFront();
        
        if(hasSelectedLetterTile())
        {
            selLetterTile.render();
        }
        Play.clearShading();
    }
    
    public void makeMove()
    {
        board.makeMove(this);
    }
    
    public void pass()
    {
    	playStateRef.finaliseMove(true);
    }
    
	private void releaseLetterTile()
	{
		gsm.getSoundManager().playClip(SFX_RELEASE_NAME);
		
		if(selLetterTile.getRect().intersects(board.getRect()))
		{
			
			if(board.getIndicator().getStatus() == TileIndicator.FAILURE)
			{
				addTileToRack();
			}
			else if(board.getIndicator().getStatus() == TileIndicator.SUCCESS)
			{
				addTileToBoard();
			}
		}
		else
		{
			addTileToRack();
		}
	}
	
	public void drawAllAI()
	{
		for(int i = 0; i < drawingAllowance; i++)
		{
			tileRack.drawLetterTile();
		}
	}
	
	private void checkAreaHovering()
	{
		if(selLetterTile.getRect().intersects(tileRack.getRect()))
		{
			tileRack.reformTiles(selLetterTile);
		}
		else if(!selLetterTile.getRect().intersects(tileRack.getRect()) && 
		         tileRack.getLetterTileFormationHole() != null)
		{
		    
			if(tileRack.tilesAreIdle() && tileRack.getLetterTileFormationHole().getIndex() != tileRack.size())
			{
				tileRack.pushTiles(Direction.LEFT, 0);
			}
		}
		
		if(board.getRect().contains(MouseManager.getX(), MouseManager.getY()))
		{
			board.hoveredOverWithTile();
		}
		else
		{
			board.disableIndicator();
		}
	}
	
	/**
	 * Highlights the letter bag if
	 * the cursor is hovering over it
	 */
	private void checkForBagHovering()
	{
		if(letterBag.getRect().contains(MouseManager.getX(), MouseManager.getY()))
		{
			letterBag.highlight(true);
		}
		else
		{
			letterBag.highlight(false);
		}
	}
	
	/**
	 * Highlights the letter tiles on the rack
	 * in response to mouse hovering over them
	 */
	private void highlightLetters()
	{
				
		for(LetterTile lt : tileRack.getLetterTiles())
		{
			lt.highlightResponse(MouseManager.getX(), MouseManager.getY());
		}		
	}
	
	/**
	 * Explanation:
	 * On left click where a letter tile in the rack is highlighted ->
	 * grab that tile, de-highlight it, remove it from the rack
	 * and last but not least disable the left movement of all the 
	 * previous tiles (according to the index of the selected tile)
	 * and enable the left movement of all the tiles after the selected
	 * one. Finally push all the tiles to the left. (Some of them
	 * obviously will not be able to move because of the 
	 * restrictions we applied before)
	 */
	private void checkForSelection()
	{
		if(!selectionTimer.isFinished())
		{
			return;
		}
		
		checkForSelectionInRack();
		
		if(board.getRect().contains(MouseManager.getX(), MouseManager.getY()))
		{
			checkForSelectionInBoard();
		}
	}
	
	/**
	 * Explanation:
	 * On left click where a letter tile in the rack is highlighted ->
	 * grab that tile, de-highlight it, remove it from the rack
	 * and last but not least disable the left movement of all the 
	 * previous tiles (according to the index of the selected tile)
	 * and enable the left movement of all the tiles after the selected
	 * one. Finally push all the tiles to the left. (Some of them
	 * obviously will not be able to move because of the 
	 * restrictions we applied before)
	 */
	private void checkForSelectionInRack()
	{
		for(LetterTile lt : tileRack.getLetterTiles())
		{
			if(MouseManager.isButtonPressed(MouseManager.LEFT_BUTTON))
			{
				if(lt.getHighlightStatus() == LetterTile.HIGHLIGHT_SELECTED ||
				   lt.isRecentlyAdded())
				{
					
					selLetterTile = lt;
					selLetterTile.setGrabbed(true);
					
					int indexOfTile = tileRack.getTileIndex(lt);
					tileRack.removeTile(selLetterTile);
					
					for(int i = 0; i < indexOfTile; i++)
					{
						tileRack.getLetterTiles().get(i).setPushDir(Direction.LEFT, true);
					}
					
					for(int i = indexOfTile; i < tileRack.getLetterTiles().size(); i++)
					{
						tileRack.getLetterTiles().get(i).setPushDir(Direction.LEFT, false);
					}
					
					tileRack.pushTiles(Direction.LEFT, indexOfTile + 1);
					
					selectionTimer = new Timer(SELECTION_COOLDOWN);
				}
			}
		}
	}
	
	private void checkForSelectionInBoard()
	{
		if(MouseManager.isButtonDown(MouseManager.LEFT_BUTTON))
		{
			if(board.checkForTileWithdrawal())
			{
				selLetterTile = board.withdrawTile(this);
				selLetterTile.setGrabbed(true);
				selLetterTile.setHighlightStatus(LetterTile.HIGHLIGHT_IDLE);
			}
		}
	}
	
	private void checkForBoardWithdrawal()
	{
		if(board.getRect().contains(MouseManager.getX(), MouseManager.getY()))
		{
			if(board.checkForTileWithdrawal() && tileRack.size() < TileRack.MAX_NO_TILES)
			{
			    LetterTile target = board.withdrawTile(this);
				addTileToRack(target);
			}
		}
	}
	
	private void checkForBoardInteraction()
	{
		if(board.getRect().contains(MouseManager.getX(), MouseManager.getY()))
		{
			board.hoveredOverWithoutTile(this);
		}
		else
		{
			board.disableIndicator();
		}
	}
	
	private void checkForLetterDrawAttempt()
	{
		if(letterBag.getHighlighted() && drawingAllowance > 0)
		{
			tileRack.drawLetterTile();
			drawingAllowance--;
		}
	}
	
	private void addTileToBoard()
	{
		board.addLetterTile(selLetterTile, this);
		selLetterTile = null;
	}
		
	private void addTileToRack()
	{
		addTileToRack(selLetterTile);
	}
	
	private void addTileToRack(LetterTile letterTile)
	{		
		if(tileRack.size() < TileRack.MAX_NO_TILES)
		{
			tileRack.addTile(letterTile, tileRack.getLetterTileFormationHole().getIndex());
			selLetterTile = null;
		}
	}
	
	public void withdrawAll()
	{
		for(LetterTile lt : board.withdrawAll(this))
		{
			addTileToRack(lt);
			tileRack.updateState();
		}		
	}
	
	public int getNoTiles()
	{
	    return tileRack.size();
	}
	
	public boolean hasValidWord()
	{
		return wordExists() && wordIsValid();
	}
	
    public boolean wordExists()
    {
        if(board.getPlayerRegistered(this))
        {
            return scrabbleDict.wordExists(board.getCurrentWord(this));
        }
        else
        {
            return false;
        }
    }
    
    public boolean wordIsValid()
    {
        return board.isCurrentFormationValid(this);
    }
    
	public boolean isActive()
	{
		return isActive;
	}
	
	public String getName()
	{
	    return name;
	}
	
	public boolean isHuman()
	{
	    return isHuman;
	}
	
	public TileRack getTileRack()
	{
		return tileRack;
	}
	
	public boolean hasSelectedLetterTile()
	{
		return selLetterTile != null;
	}
	
	public AIState getAIState()
	{
		return aiController.getState();
	}
	
	public String getCurrentWord()
	{
	    return board.getCurrentWord(this);
	}
	
	public ArrayList<LetterTile> getLastSelectionAI()
	{
	    return aiController.getLastSelection();
	}
	
	public int getCurrentPoints()
	{
		if(isHuman)
		{
			return board.calculatePoints(this);
		}
		else
		{
			return aiController.calculatePoints();
		}
	}
	
    public void setActive(boolean isActive)
    {
        this.isActive = isActive;
    }
    
    public void setDrawingAllowance(int drawingAllowance)
    {
        this.drawingAllowance = drawingAllowance;
    }
    
    public void setHuman(boolean isHuman)
    {
        this.isHuman = isHuman;
    }
}
