package com.myscrabble.entities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

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
	    if(board.isFirstRound())
	    {
	        return !formationHasGaps(board) && formationIsAligned() && middleTilePresent(board);
	    }
	    else
	    {
	        return !formationHasGaps(board) && formationIsAligned() && nNeutralTiles() > 0;
	    }
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
	
	/** Remove neutral tiles unaligned with
	 * the current formation
	 */
	public void removeUnaligned()
	{
		ArrayList<LetterTile> tilesToRemove = new ArrayList<>();
		
		for(int i = 1; i < letterTiles.size(); i++)
		{
			if(!letterTiles.get(i).isNeutral())
			{
				continue;
			}
			
			float xDistance = ScrabbleUtils.xDistanceBetween(letterTiles.get(i), letterTiles.get(i - 1));
        	float yDistance = ScrabbleUtils.yDistanceBetween(letterTiles.get(i), letterTiles.get(i - 1));
        	
			if(direction == HORIZONTAL && 
			  ((xDistance > Tile.TILE_SIZE) || (yDistance != 0)))
			{
				tilesToRemove.add(letterTiles.get(i));
			}
			else if(direction == VERTICAL && 
				   ((yDistance > Tile.TILE_SIZE) || (xDistance != 0)))
			{
				tilesToRemove.add(letterTiles.get(i));
			}
			else if(direction == NONE && 
					!((xDistance == Tile.TILE_SIZE && yDistance == 0) || 
					  (xDistance == 0 && yDistance == Tile.TILE_SIZE)))
			{
				tilesToRemove.add(letterTiles.get(i));
			}
	               
		}
		
		for(LetterTile lt : tilesToRemove)
		{
			letterTiles.remove(lt);
		}
		
		tilesToRemove.clear();
	}
	
	/**
	 * Removes all the neutral tiles from the current
	 * formation
	 */
	public void removeAllNeutrals()
	{
		ArrayList<LetterTile> tilesToRemove = new ArrayList<>();
		
		for(LetterTile lt : letterTiles)
		{
			if(lt.isNeutral())
			{
				tilesToRemove.add(lt);
			}
		}
		
		for(LetterTile lt : tilesToRemove)
		{
			letterTiles.remove(lt);
		}
		
		tilesToRemove.clear();
	}
	
	/**
	 * 
	 * @param neutralTiles All the neutral letter tiles on board.
	 * Procedure: to avoid concurrent modification exceptions a linked
	 * hash map is created to store temporarily the neutral tiles that
	 * need to be added to the main letterTiles. To find the correct
	 * neutrals for every letter tile currently in formation, all the neutrals
	 * are checked against it on their x-distance and y-distance. If
	 * the current direction of the formation is horizontal, any neutral
	 * next to the letterTiles and of the same height with them will be 
	 * added to the hash map. On the other hand if the current direction
	 * is vertical, any tile on the same x and below or above the
	 * letter tiles will be added to the hash map.
	 */
	public void checkForNeutrals(ArrayList<LetterTile> neutralTiles)
	{
		if(neutralTiles.size() == 0)
		{
			return;
		}
		
	    removeUnaligned();
	    defragmentFormation();	    
	    fixIndices();
	    updateDirection();
	    
	    LinkedHashMap<LetterTile, Integer> tilesToAdd = new LinkedHashMap<>();
	    
	    for(LetterTile lt : letterTiles)
	    {
	    	
	        for(LetterTile neutral : neutralTiles)
	        {
	        	float xDistance = ScrabbleUtils.xDistanceBetween(lt, neutral);
	        	float yDistance = ScrabbleUtils.yDistanceBetween(lt, neutral);
	        	
	            if((xDistance == Tile.TILE_SIZE && direction == HORIZONTAL && yDistance == 0) ||
	               (yDistance == Tile.TILE_SIZE && direction == VERTICAL && xDistance == 0 ))
	            {
	                if((lt.getX() < neutral.getX() && direction == HORIZONTAL)|| 
	                   (lt.getY() < neutral.getY() && direction == VERTICAL))
	                {
	                    tilesToAdd.put(neutral, letterTiles.indexOf(lt) + 1);
	                }
	                else if((lt.getX() > neutral.getX() && direction == HORIZONTAL)|| 
	                        (lt.getY() > neutral.getY() && direction == VERTICAL))
	                {
	                	if(letterTiles.indexOf(lt) == 0)
	                	{
	                		tilesToAdd.put(neutral, 0);
	                	}
	                	else
	                	{
	                		tilesToAdd.put(neutral, letterTiles.indexOf(lt) - 1);
	                	}
	                }
	            }
	            else if(letterTiles.size() == 1 &&
	            	   ((xDistance == Tile.TILE_SIZE && yDistance == 0) ||
	            	    (xDistance == 0 && yDistance == Tile.TILE_SIZE)))
	            {
	            	if(lt.getX() < neutral.getX() || lt.getY() < neutral.getY())
	            	{
	            		tilesToAdd.put(neutral, 1);
	            	}
	            	else
	            	{
	            		tilesToAdd.put(neutral, 0);
	            	}
	            }
	        }
	    }
	    
	    for(Entry<LetterTile, Integer> entry : tilesToAdd.entrySet())
	    {
	    	if(!letterTiles.contains(entry.getKey()))
	    	{
	    		letterTiles.add(entry.getValue(), entry.getKey());
	    	}
	    }
	    
	    tilesToAdd.clear();	   
	}
	
	/**
	 * Clears the letter tiles from any copies
	 * of LetterTile objects(not just same letters)
	 */
	public void defragmentFormation()
	{
		Set<LetterTile> tempSet = new LinkedHashSet<>(letterTiles);
		letterTiles.clear();
		letterTiles.addAll(tempSet);
	}
	
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
		if(nRefTiles() == 1)
		{
			resetDirection();
		}
		else if(nRefTiles() == 2)
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
	
	/**
	 * 
	 * @return the number of 
	 * letter tiles in the formation
	 * containing a player reference
	 * (i.e. not neutral)
	 */
	public int nRefTiles()
	{
		int count = 0;
		for(LetterTile lt : letterTiles)
		{
			if(!lt.isNeutral())
			{
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * 
	 * @return the number of
	 * neutral tiles in the formation
	 */
	public int nNeutralTiles()
	{
	    return letterTiles.size() - nRefTiles();
	}
	
	private boolean middleTilePresent(Board board)
	{
	    for(LetterTile lt : letterTiles)
	    {
	        if(board.getTilemap().getLetterTileHolder(lt) != null &&
	           board.getTilemap().getLetterTileHolder(lt).getType() == Tile.MIDDLE_TILE)
	        {
	            return true;
	        }
	    }
	    
	    return false;
	}
	
	private void resetDirection()
	{
		direction = NONE;
	}
}
