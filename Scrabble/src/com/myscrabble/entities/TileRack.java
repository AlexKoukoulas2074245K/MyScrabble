package com.myscrabble.entities;

import static com.myscrabble.managers.ResourceManager.STD_TEX_EXT;

import java.awt.Rectangle;
import java.util.ArrayList;

import com.myscrabble.main.Main;
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
	private ArrayList<LetterTile> letterTiles;
	private ArrayList<LetterTile> tilesToRemove;
	private ArrayList<LetterTile> tilesToAdd;
	
	private static final String RACK_BACK_PATH = "/board/stand" + STD_TEX_EXT;
	private static final String RACK_FRONT_PATH = "/board/standArm" + STD_TEX_EXT;
	
	private static final int RACK_BACK = 0;
	private static final int RACK_FRONT = 1;
	
	private float frontY;
	
	public TileRack(GameStateManager gsm)
	{
		super(gsm);
		
		letterTiles = new ArrayList<LetterTile>();
		tilesToRemove = new ArrayList<LetterTile>();
		tilesToAdd = new ArrayList<LetterTile>();
		
		LetterBag letterBag = new LetterBag(gsm);
		
		addTexture(RACK_BACK, RACK_BACK_PATH);
		addTexture(RACK_FRONT, RACK_FRONT_PATH);
		
		x = Main.getCenterDimensions()[0] - getTexture(RACK_FRONT).getTextureWidth()/2;
		y = Main.getNormalDimensions()[1] - (int)(1.5f * getTexture(RACK_BACK).getTextureHeight());
		frontY = y + getTexture(RACK_BACK).getTextureHeight() - getTexture(RACK_FRONT).getTextureHeight();
		
		for(int i = 0; i < 7; i++)
		{
			char letter   = letterBag.drawLetter();
			int letterValue = ScrabbleUtils.getValueOf(letter);
			float letterX = getLetterX(i);
			float letterY = y;
			letterTiles.add(new LetterTile(gsm, letter, letterValue, letterX, letterY));
		}
	
	}
	
	@Override
	public void update()
	{
		for(LetterTile lt : letterTiles)
		{
			lt.update();
		}
		
		if(!tilesIdle())
		{
			return;
		}
		
		for(LetterTile lt : tilesToRemove)
		{
			letterTiles.remove(lt);
		}

		tilesToRemove.clear();
		
		if(!holesExist())
		{
			for(LetterTile lt : tilesToAdd)
			{
				letterTiles.add(lt);
			}
			tilesToAdd.clear();
		}
				
	}
		
	public void renderBack()
	{
		RenderUtils.renderTexture(getTexture(RACK_BACK), x, y, false);
		
		for(LetterTile lt: letterTiles)
		{
			lt.render();
		}
	}
	
	public void renderFront()
	{
		RenderUtils.renderTexture(getTexture(RACK_FRONT), x, frontY, false);
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
		if(!tilesIdle())
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
				pushTilesRight(0);
			}
			else
			{
				pushTilesLeft(0);
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
			
			if(rightSideTile.canBeMoved(LetterTile.RIGHT))
			{
				pushTilesRight(letterTiles.indexOf(rightSideTile));
			}
			else if(leftSideTile.canBeMoved(LetterTile.LEFT))
			{
				leftSideTile.pushLeft();
			}
		}
	}
	
	public void pushTiles(final boolean left, int startingIndex)
	{	
		for(int i = startingIndex; i < letterTiles.size(); i++)
		{
			if(left)
			{
				letterTiles.get(i).pushLeft();
			}
			else
			{
				letterTiles.get(i).pushRight();
			}
		}
	}
	
	public void pushTilesLeft(int startingIndex)
	{
		pushTiles(true, startingIndex);
	}

	public void pushTilesRight(int startingIndex)
	{
		pushTiles(false, startingIndex);
	}
	
	public void addTile(final LetterTile lt)
	{
		char ch = lt.getLetter();
		int points = lt.getPoints();
		
		tilesToAdd.add(new LetterTile(gsm, ch, points, getLetterX(letterTiles.size()), y));
	}
	
	public void removeTile(final LetterTile lt)
	{
		tilesToRemove.add(lt);
	}
	
	public Rectangle getRect()
	{
		return new Rectangle((int)x, (int)y - Tile.TILE_SIZE / 2,
									 getTexture(RACK_BACK).getTextureWidth(), 
								   	 getTexture(RACK_BACK).getTextureHeight());
	}
	
	public int getNoCollisions(final LetterTile lt)
	{
		int noCollisions = 0;
		
		for(LetterTile other: letterTiles)
		{
			if(ScrabbleUtils.intersects(lt.getRect(), other.getRect()))
			{
				noCollisions++;
			}
		}
		
		return noCollisions;
	}
	
	public ArrayList<LetterTile> getCollidedTiles(final LetterTile lt)
	{
		ArrayList<LetterTile> result = new ArrayList<>();
		
		for(LetterTile other: letterTiles)
		{
			if(ScrabbleUtils.intersects(lt.getRect(), other.getRect()))
			{
				result.add(other);
			}
		}
		
		return result;
	}
	
	public float getLetterX(final int index)
	{
		return x + index * Tile.TILE_SIZE;
	}
	
	public int getTileIndex(LetterTile lt)
	{
		return letterTiles.indexOf(lt);
	}
	
	public boolean tilesIdle()
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
	
	public ArrayList<LetterTile> getLetterTiles()
	{
		return letterTiles;
	}
	
	public boolean holesExist()
	{
		if(letterTiles.size() == 0) return false;
		
		for(int i = 1; i < letterTiles.size(); i++)
		{
			if(letterTiles.get(i).getCenterX() - 
			   letterTiles.get(i - 1).getCenterX() != LetterTile.TILE_SIZE)
			{
				return true;
			}
		}
		
		return false;
	}
}
