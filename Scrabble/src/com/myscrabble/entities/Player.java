package com.myscrabble.entities;

import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.InputManager;
import com.myscrabble.util.ScrabbleUtils;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A class that holds the information of 
 * a player in the game
 */

public class Player
{
	private LetterTile selLetterTile;
	private Board board;
	private TileRack tileRack;
	private TileSelector tileSelector;
	
	private boolean isHuman;
	private boolean isActive;
	
	public Player(GameStateManager gsm, Board board, TileSelector tileSelector)
	{
		this.tileSelector = tileSelector;
		this.board = board;
		
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
		if(!ScrabbleUtils.intersects(selLetterTile.getRect(), tileRack.getRect()))
		{
			tileRack.addTile(selLetterTile);
			selLetterTile = null;
		}
	}
	
	private void checkAreaHovering()
	{
		
		if(ScrabbleUtils.intersects(selLetterTile.getRect(), tileRack.getRect()))
		{
			tileRack.reformTiles(selLetterTile);
		}
		else if(!ScrabbleUtils.intersects(selLetterTile.getRect(), tileRack.getRect()))
		{
			if(tileRack.tilesIdle())
			{
				tileRack.pushTiles(LetterTile.LEFT, 0);
			}
		}
		else if(ScrabbleUtils.intersects(selLetterTile.getRect(), board.getRect()))
		{
			
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
		for(LetterTile lt : tileRack.getLetterTiles())
		{
			if(InputManager.isButtonDown(InputManager.LEFT_BUTTON))
			{
				if(lt.getHighlightStatus() == LetterTile.HIGHLIGHT_SELECTED)
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
