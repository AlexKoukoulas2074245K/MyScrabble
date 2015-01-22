package com.myscrabble.entities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.myscrabble.util.ScrabbleUtils;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A tile formation represents a series
 * of tiles that the player has chosen
 * and enables dynamic insertion and removal
 * of tiles from the formation
 */
public class TileFormation
{
	/* The direction flags of the word formation (i.e. horizontal/vertical) */
	public static final int NONE = -1;
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	
	private ArrayList<LetterTile> letterTiles;
	private int direction;
	
	public TileFormation()
	{
		letterTiles = new ArrayList<>();
		direction = NONE;
	}
	
	/**
	 * 
	 * @param tile To be added to the
	 * current tile formation
	 */
	public void addTile(LetterTile tile)
	{
		letterTiles.add(tile);
		updateDirection();
		fixIndices();
	}
	
	/**
	 * 
	 * @param tile To be removed from the
	 * current tile formation
	 */
	public void removeTile(LetterTile tile)
	{
		letterTiles.remove(tile);
		updateDirection();
		fixIndices();
	}
	
	public void releaseTiles()
	{
	    for(LetterTile lt : letterTiles)
	    {
	        lt.clearPlayerRef();
	    }
	}
	
	/**
	 * 
	 * @return All the letter tiles in the
	 * current tile formation
	 */
	public ArrayList<LetterTile> getTiles()
	{
		return letterTiles;
	}
	
	/**
	 * 
	 * @return The current word formulated from
	 * the current tile formation by extracting
	 * the letter points from each letter tile.
	 */
	public String getWord()
	{
		StringBuilder result = new StringBuilder();
		
		for(LetterTile letterTile : letterTiles)
		{
			result.append(letterTile.getLetter());
		}
		
		return result.toString();
	}
	
	/**
	 * 
	 * @return the direction of the tile formation
	 * <br> (i.e. horizontal or vertical)
	 */
	public int getDirection()
	{
		return direction;
	}
	
	/**
	 * 
	 * @return Whether the current tile formation has 
	 * no gaps in between tiles and all the tiles
	 * are aligned in a horizontal or vertical line
	 * in the game board
	 */
	public boolean isValidFormation(Board board)
	{
		return !formationHasGaps(board) && formationIsAligned();
	}
	
	/**
	 * 
	 * @return Whether the letter tile formation
	 * currently has gaps between one or more
	 * tiles. Gaps are essentially any distance
	 * between the tiles greater than the standard
	 * tile size.
	 */
	private boolean formationHasGaps(Board board)
	{
	    
		if(letterTiles.size() == 1)
		{
			return false;
		}
		
		for(int i = 1; i < letterTiles.size(); i++)
		{
			if(direction == HORIZONTAL && 
			   ScrabbleUtils.xDistanceBetween(letterTiles.get(i), letterTiles.get(i - 1)) > Tile.TILE_SIZE)
			{
				return true;
			}
			else if(direction == VERTICAL && 
					ScrabbleUtils.yDistanceBetween(letterTiles.get(i), letterTiles.get(i - 1)) > Tile.TILE_SIZE)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void checkForNeutrals(ArrayList<LetterTile> neutralTiles)
	{
	    LinkedHashMap<LetterTile, Integer> tilesToAdd = new LinkedHashMap<>();
	    
	    for(LetterTile lt : letterTiles)
	    {
	        for(LetterTile neutral : neutralTiles)
	        {
	            if(ScrabbleUtils.xDistanceBetween(lt, neutral) == Tile.TILE_SIZE  && direction == HORIZONTAL ||
	               ScrabbleUtils.yDistanceBetween(lt, neutral) == Tile.TILE_SIZE && direction == VERTICAL)
	            {
	                if((lt.getX() < neutral.getX() && direction == HORIZONTAL)|| 
	                   (lt.getY() < neutral.getY() && direction == VERTICAL))
	                {
	                    tilesToAdd.put(neutral, letterTiles.indexOf(lt) + 1);
	                }
	                else if((lt.getX() > neutral.getX() && direction == HORIZONTAL)|| 
	                        (lt.getY() > neutral.getY() && direction == VERTICAL))
	                {
	                    tilesToAdd.put(neutral, letterTiles.indexOf(lt) - 1);
	                }
	            }
	        }
	    }
	    
	    for(Entry<LetterTile, Integer> entry : tilesToAdd.entrySet())
	    {
	        letterTiles.add(entry.getValue(), entry.getKey());
	    }
	}
	
//	private boolean replacementExists(LetterTile lt, Board board)
//	{
//	    float desiredX;
//	    float desiredY;
//	    
//	    if(direction == HORIZONTAL)
//	    {
//	        desiredX = lt.getX() + Tile.TILE_SIZE;
//	        desiredY = lt.getY();
//	    }
//	    else
//	    {
//	        desiredX = lt.getX();
//	        desiredY = lt.getY() + Tile.TILE_SIZE;
//	    }
//	    
//	    if(board.getLetterTile(desiredX, desiredY) != null)
//	    {
//	        System.out.println(board.getLetterTile(desiredX, desiredY).getLetter());
//	    }
//	    else
//	    System.out.println("BAD");
//	    return false;
//	}
	
	/**
	 * 
	 * @return Whether the letterTile formation
	 * is currently aligned. In essence this will
	 * return true if all letter tiles are in the
	 * same horizontal line or vertical line in the 
	 * game board.
	 */
	private boolean formationIsAligned()
	{
		if(letterTiles.size() == 1)
		{
			return true;
		}
		
		for(int i = 1; i < letterTiles.size(); i++)
		{
			/* Check for tiles unaligned if direction is horizontal */
			if(direction == HORIZONTAL && letterTiles.get(i).getY() != letterTiles.get(i - 1).getY())
			{
				return false;
			}
			/* Check for tiles unaligned if direction is vertical */
			else if(direction == VERTICAL && letterTiles.get(i).getX() != letterTiles.get(i - 1).getX())
			{
				return false;
			}	
		}
		
		return true;
	}
	
	/**
	 * Used to regroup the letterTiles in the letterTiles ArrayList
	 * to account for their position. This is handy
	 * if a tile has been withdrawn from the board
	 * or a new one has been inserted in front 
	 * of the formation.
	 */
	private void fixIndices()
	{
		if(direction == NONE || !formationIsAligned())
		{
			return;
		}

		ArrayList<Float> tilePositions = ScrabbleUtils.getAscendingPositions(letterTiles, direction);
		ArrayList<LetterTile> newTiles = new ArrayList<>();
		
		for(Float coord : tilePositions)
		{
			for(LetterTile tile : letterTiles)
			{
				if(direction == HORIZONTAL && tile.getX() == coord)
				{
					newTiles.add(tile);
					break;
				}
				else if(direction == VERTICAL && tile.getY() == coord)
				{
					newTiles.add(tile);
					break;
				}
			}
		}
		
		letterTiles = newTiles;
	}
	
	private void updateDirection()
	{
		if(letterTiles.size() == 1)
		{
			resetDirection();
		}
		else if(letterTiles.size() == 2)
		{
			decideDirection();
		}
	}
	
	/**
	 * Decides on the direction of the current
	 * tile formation
	 */
	private void decideDirection()
	{
		if(letterTiles.get(0).getY() == letterTiles.get(1).getY())
		{
			direction = HORIZONTAL;
		}
		else
		{
			direction = VERTICAL;
		}
	}
	
	private void resetDirection()
	{
		direction = NONE;
	}
}
