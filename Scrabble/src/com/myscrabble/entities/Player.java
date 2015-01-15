package com.myscrabble.entities;

import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.InputManager;
import com.myscrabble.util.ScrabbleUtils;
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
	
	private boolean isHuman;
	private boolean isActive;
	
	public Player(GameStateManager gsm, Board board)
	{
		this.board = board;
		
		selectionTimer = new Timer(SELECTION_COOLDOWN);
		
		tileRack = new TileRack(gsm);
		isActive = true;
		isHuman = true;
		
		selLetterTile = null;
	}
		
	public void handleInput()
	{
		if(hasSelectedLetterTile() && InputManager.isButtonDown(InputManager.LEFT_BUTTON))
		{
			checkAreaHovering();
		}
		else if(hasSelectedLetterTile() && InputManager.isButtonReleased(InputManager.LEFT_BUTTON))
		{
			releaseLetterTile();
		}
		else if(!hasSelectedLetterTile())
		{
			highlightLetters();
			checkForSelection();
		}
		
	}
	
	private void releaseLetterTile()
	{
		if(tileRack.nTiles() < TileRack.MAX_NO_TILES)
		{				
			tileRack.addTile(selLetterTile, tileRack.getLetterTileFormationHole().getIndex());
			selLetterTile = null;
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
		
		if(board.getRect().contains(InputManager.getX(), InputManager.getY()))
		{
			board.hoveredOver();
		}
		else
		{
			board.disableIndicator();
		}
	}
	
	private void highlightLetters()
	{
				
		for(LetterTile lt : tileRack.getLetterTiles())
		{
			lt.highlightResponse(InputManager.getX(), InputManager.getY());
		}		
	}
	
	/**
	 * Explanation:
	 * On left click where a letter tile is highlighted ->
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
		
		for(LetterTile lt : tileRack.getLetterTiles())
		{
			if(InputManager.isButtonDown(InputManager.LEFT_BUTTON))
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
	
	public void render()
	{
		tileRack.renderBack();
		tileRack.renderFront();
		
		if(hasSelectedLetterTile())
		{
			selLetterTile.render();
		}
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
}
