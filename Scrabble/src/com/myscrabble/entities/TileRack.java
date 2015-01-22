package com.myscrabble.entities;

import static com.myscrabble.managers.ResourceManager.STD_TEX_EXT;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.myscrabble.entities.LetterTile.Direction;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.util.RenderUtils;
import com.myscrabble.util.ScrabbleUtils;
/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A class that represents a tile stand like the original
 * Scrabble game. It has a 1-1 relationship with a player
 */

public class TileRack extends GameObject
{
	/* Standard max number of tiles for a rack */
	public static final int MAX_NO_TILES = 7;
	public static final int MAX_INDEX = MAX_NO_TILES - 1;
	
	private static final String RACK_BACK_PATH = "/rack/stand" + STD_TEX_EXT;
	private static final String RACK_FRONT_PATH = "/rack/standArm" + STD_TEX_EXT;
	private static final String RACK_BACK_REV_PATH = "/rack/standRev" + STD_TEX_EXT;
	
	private static final int RACK_BACK = 0;
	private static final int RACK_FRONT = 1;
	
	private static final float PLAYER_1_POS_X = 192.0f;
	private static final float PLAYER_1_POS_Y = 592.0f;
	
	private static final float PLAYER_2_POS_X = PLAYER_1_POS_X;
	private static final float PLAYER_2_POS_Y = 16.0f; 
	        
	public static float[] getTilePos(final int index, final boolean isHuman)
	{
	    if(isHuman)
	    {
	        return new float[]{PLAYER_1_POS_X + index * Tile.TILE_SIZE, PLAYER_1_POS_Y};
	    }
	    else
	    {
	        return new float[]{PLAYER_2_POS_X + index * Tile.TILE_SIZE, PLAYER_2_POS_Y};
	    }
	}
	 
	/* Core Letter Tiles on the rack*/
    private ArrayList<LetterTile> letterTiles;
    
    /* Removal through other ArrayList to 
     * avoid concurrent modification exceptions */
    private ArrayList<LetterTile> tilesToRemove;
    
    /* Addition through a HashMap where key = LetterTile,
     * value = index that the tile needs to be inserted at */
    private HashMap<LetterTile, Integer> tilesToAdd; 
    
    /* Temporary storage of LetterTile attributes */
    private ArrayList<Float> tempStoredPositions;
    private ArrayList<boolean[]> tempStoredFlags;
     
    private LetterBag letterBag;
    private Player playerRef;
    private float frontY;
    
    private boolean tilesAnimating;
    
	public TileRack(GameStateManager gsm, Player playerRef, LetterBag letterBag)
	{
		super(gsm);
		
		this.playerRef = playerRef;
		this.letterBag = letterBag;
		
		letterTiles = new ArrayList<LetterTile>();
		tilesToRemove = new ArrayList<LetterTile>();
		tilesToAdd = new HashMap<LetterTile, Integer>();
		
		tempStoredPositions = new ArrayList<Float>();
		tempStoredFlags = new ArrayList<boolean[]>();
		

		
		if(playerRef.isHuman())
		{
		    addTexture(RACK_BACK, RACK_BACK_PATH);
		    addTexture(RACK_FRONT, RACK_FRONT_PATH);
		    
    		x = PLAYER_1_POS_X;
    		y = PLAYER_1_POS_Y;
    		
	        frontY = y + getTexture(RACK_BACK).getTextureHeight() - getTexture(RACK_FRONT).getTextureHeight();
	        
	        for(int i = 0; i < 7; i++)
	        {
	            drawLetterTile(i);
	        }
		}
		else
		{
		    addTexture(RACK_BACK, RACK_BACK_REV_PATH);
		    addTexture(RACK_FRONT, RACK_FRONT_PATH);
		    x = PLAYER_2_POS_X;
		    y = PLAYER_2_POS_Y;
		    
		    frontY = y;
		    
		    for(int i = 0; i < 7; i++)
            {
                drawLetterTile(i);
            }
		}
		

	}
	
	@Override
	public void update()
	{
		if(!playerRef.isHuman())
		{
		    return;
		}
		
		coreTileUpdate();
		
		if(!tilesAreIdle())
		{
			return;
		}
		
		/** Remove tiles */
		for(LetterTile lt : tilesToRemove)
		{
			letterTiles.remove(lt);
		}

		tilesToRemove.clear();
		
		if(tilesToAdd.size() == 0) return;
		
		/** Add tiles */
		for(Entry<LetterTile, Integer> entry : tilesToAdd.entrySet())
		{
			letterTiles.add(entry.getValue(), entry.getKey());
			
		}
		
		resetFlags();
		
		tilesToAdd.clear();			
	}
	
	private void coreTileUpdate()
	{
		animatingTilesUpdate();
	    if(tilesAnimating)
	    {	        
	        return;
	    }
	    
		storeTempAttribs();
		
		for(LetterTile lt : letterTiles)
		{
			lt.update();
		}
		
		checkForMerges();
	}
	
	private void animatingTilesUpdate()
	{
		tilesAnimating = false;
	    for(LetterTile lt : letterTiles)
	    {
	        if(lt.getDrawAnimating())
	        {
	            lt.updateDrawAnimation();
	            tilesAnimating = true;
	        }
	    }
	}
			
	public void renderBack()
	{
	    RenderUtils.renderTexture(getTexture(RACK_BACK), x, y);
		
		for(LetterTile lt: letterTiles)
		{
		    if(playerRef.isHuman())
		    {
		        lt.render();
		    }
		    else
		    {
		        lt.emptyRender();
		    }
		}
	}
	
	public void renderFront()
	{
		RenderUtils.renderTexture(getTexture(RACK_FRONT), x, frontY);
	}
	
	/**
	 * 
	 * @param lt Selected LetterTile
	 * Pushes or splits the tiles in the rack
	 * to create space for the selected LetterTile.
	 * - In the case of a single collision the selected tile
	 * is in either in contact with the first or last tile 
	 * in the rack. And hence all the rack tiles must
	 * be pushed in either direction
	 * - In the case of double collision the selected tile
	 * attempts to split two tiles in the rack. If the right
	 * hand side contacted tile can move to the right, it moves
	 * to the right along with all other tiles to the right of it.
	 * If the right hand side tile cannot move right, then the left
	 * hand side tile needs to travel one spot to the left.
	 */
	public void reformTiles(final LetterTile lt)
	{
		if(!tilesAreIdle())
		{
			return;	
		}
		
		int collisions = getNoCollisions(lt);
		ArrayList<LetterTile> collTiles = getCollidedTiles(lt);
		
		if(collisions == 1 && 
	      ((letterTiles.indexOf(collTiles.get(0)) == 0  &&
		    letterTiles.get(0).getCenterX() > lt.getCenterX())
								||
		  ((letterTiles.indexOf(collTiles.get(0)) == letterTiles.size() - 1 && 
		    letterTiles.get(letterTiles.size() - 1).getCenterX() < lt.getCenterX()))))
		{
			if(lt.getCenterX() < collTiles.get(0).getCenterX())
			{
				pushTiles(Direction.RIGHT, 0);
			}
			else
			{
				pushTiles(Direction.LEFT, 0);
			}
		}
		else if(collisions == 2)
		{
			LetterTile rightSideTile = null;
			LetterTile leftSideTile = null;
			
			if(lt.getCenterX() < collTiles.get(0).getCenterX())
			{
				rightSideTile = collTiles.get(0);
				leftSideTile = collTiles.get(1);
			}
			else
			{
				rightSideTile = collTiles.get(1);
				leftSideTile = collTiles.get(0);
			}
			
			if(rightSideTile.canBeMoved(Direction.RIGHT))
			{
				pushTiles(Direction.RIGHT, letterTiles.indexOf(rightSideTile));
			}
			else if(leftSideTile.canBeMoved(Direction.LEFT))
			{
				leftSideTile.push(Direction.LEFT);
			}
		}
	}
	
	public void pushTiles(final Direction direction, int startingIndex)
	{	
		for(int i = startingIndex; i < letterTiles.size(); i++)
		{
			letterTiles.get(i).push(direction);
		}
	}
	
	private void storeTempAttribs()
	{
		for(LetterTile lt : letterTiles)
		{
			tempStoredPositions.add(lt.getX());
			tempStoredFlags.add(lt.getFlags());
		}
	}
	
	
	/* Resets the positions of two tiles if 
	 * intersection of their rectangles is
	 * found
	 */
	private void checkForMerges()
	{
		boolean mergeSpotted = false;
		
		for(int i = 1; i < letterTiles.size(); i++)
		{
			LetterTile ltPrev = letterTiles.get(i - 1);
			LetterTile ltCurr = letterTiles.get(i);
			
			if(ltPrev.getRect().intersects(ltCurr.getRect()) &&
			   !ltPrev.getGrabbed() && !ltCurr.getGrabbed())
			{
				ltPrev.setX(tempStoredPositions.get(i - 1));
				ltPrev.setFlags(tempStoredFlags.get(i - 1));
				ltCurr.setX(tempStoredPositions.get(i));
				ltCurr.setFlags(tempStoredFlags.get(i));
				
				mergeSpotted = true;
			}
		}
		
		
		tempStoredPositions.clear();
		tempStoredFlags.clear();
		
		if(mergeSpotted)
		{
		    pushTiles(Direction.LEFT, 0);
		}
		
	}
	
	private void resetFlags()
	{
	    for(LetterTile lt : letterTiles)
	    {
	        boolean[] newFlags = new boolean[4];
	        newFlags[Direction.LEFT.value] = true;
	        lt.setFlags(newFlags);
	    }
	}
	
	public void drawLetterTile()
	{
		drawLetterTile(letterTiles.size());
	}
	
	/**
	 * 
	 * @param index The index for the next letter
	 * tile to be placed at.
	 * <br>
	 * Draws a LetterTile for the bag and places it
	 * to the requested index
	 */
	public void drawLetterTile(int index)
	{
		letterTiles.add(letterBag.drawLetter(playerRef, index));
	}
	
	/* Add / Remove tiles from rack */
	public void addTile(final LetterTile lt, final int index)
	{
		char ch = lt.getLetter();
		int points = lt.getPoints();
		
		LetterTile newTile = new LetterTile(gsm, playerRef, ch, points, lt.getDrawAnimating(), index);
		newTile.setRecentlyAdded(true);
		
		tilesToAdd.put(newTile, index);
	}
	
	public void removeTile(final LetterTile lt)
	{
		tilesToRemove.add(lt);
	}
	
	/* Getters / Setters */
	public Rectangle getRect()
	{
		return new Rectangle((int)x, (int)y - Tile.TILE_SIZE / 2,
									 getTexture(RACK_BACK).getTextureWidth(), 
								   	 getTexture(RACK_BACK).getTextureHeight());
	}
	
	/**
	 * 
	 * @param lt to be checked against
	 * @return the number of collisions
	 * of letter tiles currently on the rack
	 * with the requested external lt.
	 */
	public int getNoCollisions(final LetterTile lt)
	{
		int noCollisions = 0;
		
		for(LetterTile other: letterTiles)
		{
			if(lt.getRect().intersects(other.getRect()))
			{
				noCollisions++;
			}
		}
		
		return noCollisions;
	}
	
	/**
	 * 
	 * @param lt lt to be checked against.
	 * @return the tiles on the rack that 
	 * collide with the requested letter tile.
	 */
	public ArrayList<LetterTile> getCollidedTiles(final LetterTile lt)
	{
		ArrayList<LetterTile> result = new ArrayList<>();
		
		for(LetterTile other: letterTiles)
		{
			if(lt.getRect().intersects(other.getRect()))
			{
				result.add(other);
			}
		}
		
		return result;
	}
	
	public boolean getTilesAnimating()
	{
	    return tilesAnimating;
	}
	
	public int getTileIndex(LetterTile lt)
	{
		return letterTiles.indexOf(lt);
	}
	
	public boolean tilesAreIdle()
	{
		for(LetterTile lt: letterTiles)
		{
			if(!lt.getIdle())
			{
				return false;
			}
		}
		
		return true;
	}
	
	public int nTiles()
	{
		return letterTiles.size();
	}
	
	public ArrayList<LetterTile> getLetterTiles()
	{
		return letterTiles;
	}
	
	/**
	 * Attempts to find and return a hole in the formation
	 * of the tiles in the rack (i.e. a spot -in between, at
	 * the start or at the end of the letter tiles- where a new
	 * tile could come in)
	 */
	public TileRackHole getLetterTileFormationHole()
	{
		if(letterTiles.size() == 0)
		{
			return TileRackHole.getDefaultHole(playerRef);
		}
		else if(letterTiles.size() == MAX_NO_TILES) /* this should never happen */
		{
			return null;
		}
		else
		{
			if(letterTiles.get(0).getX() != getTilePos(0, playerRef.isHuman())[0])
			{
				return TileRackHole.getDefaultHole(playerRef);
			}
			
			for(int i = 1; i < letterTiles.size(); i++)
			{
				if(letterTiles.get(i).getCenterX() -
				   letterTiles.get(i- 1).getCenterX() != LetterTile.TILE_SIZE)
				{
					return new TileRackHole(i, getTilePos(i, playerRef.isHuman()));
				}
			}
			
			if(letterTiles.size() < MAX_NO_TILES)
			{
				return new TileRackHole(letterTiles.size(), getTilePos(letterTiles.size(), playerRef.isHuman()));
			}
			
			return TileRackHole.getDefaultHole(playerRef);
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < letterTiles.size(); i++)
		{
			sb.append(letterTiles.get(i).getY());
			sb.append("|");
		}
		
		return sb.toString();
	}
}

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A Struct representing
 * a hole in the LetterTiles' formation
 * while standing in the TileRack. This information
 * is needed because it may be a valid candidate for
 * a letter tile hovering over the rack. 
 * Has an index and a position.
 */
class TileRackHole
{
	public static TileRackHole getDefaultHole(Player playerRef)
	{
		return new TileRackHole(0, TileRack.getTilePos(0, playerRef.isHuman()));
	}
	
	public static boolean isDefaultHole(TileRackHole hole, Player playerRef)
	{
		return hole.equals(getDefaultHole(playerRef));
	}
	
	private int index;
	private float[] pos;
	
	public TileRackHole(int index, float[] pos)
	{
		this.index = index;
		this.pos = pos;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(!(object instanceof TileRackHole) || object == null)
		{
			return false;
		}
		else
		{
			TileRackHole other  = (TileRackHole)object;
			
			return other.getIndex()  == this.index &&
			 	   other.getPos()[0] == this.pos[0];
		}
	}
	
	@Override
	public String toString()
	{
		return index + "     |     " + "[" + pos[0] + ", " + pos[1] + "}";
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public float[] getPos()
	{
		return pos;
	}
}
