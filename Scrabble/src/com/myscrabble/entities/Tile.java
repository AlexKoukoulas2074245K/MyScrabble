package com.myscrabble.entities;

import java.awt.Rectangle;
import java.util.Random;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * This class holds all the fields for a particular
 * tile on the playing board. Not to be confused
 * with LetterTile which is an actual game object
 * child. This class serves on the other hand
 * serves the role of the place holder on the game
 * board.
 */

public class Tile 
{
	public static final int TILE_SIZE = 32;
	
	public static final int PLAIN         = 0;
	public static final int DOUBLE_LETTER = 1;
	public static final int TRIPLE_LETTER = 2;
	public static final int DOUBLE_WORD   = 3;
	public static final int TRIPLE_WORD   = 4;
	
	private int type;
	private int col;
	private int row;
	private float x;
	private float y;
	private boolean isEmpty;
	
	public Tile(int col, int row, int type)
	{
		this.col = col;
		this.row = row;
		this.type = type;
		this.isEmpty = new Random().nextInt(2) == 1;
		
		x = Board.SIDE_WIDTH + Board.X_OFFSET + col * TILE_SIZE;
		y = Board.SIDE_HEIGHT + Board.Y_OFFSET + row * TILE_SIZE;
	}
	
	public Rectangle getRect()
	{
		return new Rectangle((int)x, (int)y, TILE_SIZE, TILE_SIZE);
	}
	
	public int getType()
	{
		return type;
	}
	
	public boolean isEmpty()
	{
		return isEmpty;
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}

	public int getCol()
	{
		return col;
	}

	public int getRow()
	{
		return row;
	}

	public void setType(int type)
	{
		this.type = type;
	}
	
	public void setEmpty(boolean isEmpty)
	{
		this.isEmpty = isEmpty;
	}
	
	public void setX(float x)
	{
		this.x = x;
	}
	
	public void setY(float y)
	{
		this.y = y;
	}

	public void setCol(int col)
	{
		this.col = col;
	}
	
	public void setRow(int row)
	{
		this.row = row;
	}
}
