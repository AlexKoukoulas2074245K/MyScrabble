package com.myscrabble.entities;

import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.MouseManager;
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
	/* A cool down for the players selection (to avoid spamming left click on tile rack) */
	private static final int SELECTION_COOLDOWN = 60; /* measured in frames */
	
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
	
	private boolean isActive;
	
	public Player(GameStateManager gsm, Board board, ScrabbleDictionary scrabbleDict, LetterBag letterBag)
	{
		this.board = board;
		this.scrabbleDict = scrabbleDict;
		this.letterBag = letterBag;
		
		selectionTimer = new Timer(SELECTION_COOLDOWN);
		
		tileRack = new TileRack(gsm, this, letterBag);
		isActive = true;
		
		selLetterTile = null;
	}
	
	public void handleInput()
	{
		if(hasSelectedLetterTile() && MouseManager.isButtonDown(MouseManager.LEFT_BUTTON))
		{
			checkAreaHovering();
		}
		else if(hasSelectedLetterTile() && MouseManager.isButtonReleased(MouseManager.LEFT_BUTTON))
		{
			releaseLetterTile();
		}
		else if(!hasSelectedLetterTile() && MouseManager.isButtonDown(MouseManager.RIGHT_BUTTON))
		{
			checkForBoardWithdrawal();
		}
		else if(!hasSelectedLetterTile())
		{
			highlightLetters();
			checkForSelection();
			checkForBoardInteraction();
			checkForBagHovering();
		}
		
		if(!hasSelectedLetterTile() && MouseManager.isButtonPressed(MouseManager.LEFT_BUTTON))
		{
			checkForLetterDrawAttempt();
		}
	}
	
	public void update()
	{
		tileRack.update();
		
		if(hasSelectedLetterTile())
		{
			selLetterTile.update();
		}
		
		if(selectionTimer != null)
		{
			selectionTimer.update();
		}
	}
	
	private void releaseLetterTile()
	{
		if(selLetterTile.getRect().intersects(board.getRect()))
		{
			
			if(board.getIndicator().getStatus() == TileIndicator.FAILURE)
			{
				addTileToRack();
			}
			else if(board.getIndicator().getStatus() == TileIndicator.SUCCESS)
			{
				addTileToBoard();
				System.out.println("Word : " + getCurrentWord() + " " + wordExists());
			}
		}
		else
		{
			addTileToRack();
		}
	}
	
	private void checkAreaHovering()
	{
		
		if(selLetterTile.getRect().intersects(tileRack.getRect()))
		{
			tileRack.reformTiles(selLetterTile);
		}
		else if(!selLetterTile.getRect().intersects(tileRack.getRect()))
		{
			if(tileRack.tilesAreIdle())
			{
				tileRack.pushTiles(LetterTile.LEFT, 0);
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
		
		if(tileRack.getRect().contains(MouseManager.getX(), MouseManager.getY()))
		{
			checkForSelectionInRack();
		}
		else if(board.getRect().contains(MouseManager.getX(), MouseManager.getY()))
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
					selLetterTile.setHighlightStatus(LetterTile.HIGHLIGHT_IDLE);
					
					int indexOfTile = tileRack.getTileIndex(lt);
					tileRack.removeTile(selLetterTile);
					
					for(int i = 0; i < indexOfTile; i++)
					{
						tileRack.getLetterTiles().get(i).setPushDir(LetterTile.LEFT, true);
					}
					for(int i = indexOfTile; i < tileRack.getLetterTiles().size(); i++)
					{
						tileRack.getLetterTiles().get(i).setPushDir(LetterTile.LEFT, false);
					}
					
					tileRack.pushTiles(LetterTile.LEFT, indexOfTile + 1);
					
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
		//selectionTimer = new Timer(SELECTION_COOLDOWN);
	}
	
	private void checkForBoardWithdrawal()
	{
		if(board.getRect().contains(MouseManager.getX(), MouseManager.getY()))
		{
			if(board.checkForTileWithdrawal())
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
		if(letterBag.getHighlighted() && tileRack.nTiles() < TileRack.MAX_NO_TILES)
		{
			tileRack.drawLetterTile();
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
		if(tileRack.nTiles() < TileRack.MAX_NO_TILES)
		{
			tileRack.addTile(letterTile, tileRack.getLetterTileFormationHole().getIndex());
			selLetterTile = null;
		}
	}
	
	public void render()
	{
		tileRack.renderBack();
		tileRack.renderFront();
		
		if(hasSelectedLetterTile())
		{
			selLetterTile.render();
		}
	}
	
	public boolean hasValidWord()
	{
		return wordExists() && wordIsValid();
	}
	
	public boolean isActive()
	{
		return isActive;
	}
	
	public boolean hasSelectedLetterTile()
	{
		return selLetterTile != null;
	}
	
	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
	}
	
	public String getCurrentWord()
	{
	    return board.getCurrentWord(this);
	}
	
	public int getCurrentPoints()
	{
	    return board.calculatePoints(this);
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
}
