package com.myscrabble.entities;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * This class holds all the fields for a particular
 * tile on the playing board. 
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
		this.isEmpty = true;
		x = col * TILE_SIZE;
		y = row * TILE_SIZE;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public int getCol()
	{
		return col;
	}

	public void setCol(int col)
	{
		this.col = col;
	}

	public int getRow()
	{
		return row;
	}

	public void setRow(int row)
	{
		this.row = row;
	}

	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	public boolean isEmpty()
	{
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty)
	{
		this.isEmpty = isEmpty;
	}
}
