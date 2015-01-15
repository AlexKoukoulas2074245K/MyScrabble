package com.myscrabble.entities;

import com.myscrabble.managers.InputManager;
import com.myscrabble.util.OutOfBoardException;


/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A class containing information about the tile map 
 * constructed from the board layout given (default array from Board class)
 * Letter-Tile accessibility is done from this class
 */

public class Tilemap 
{
	private Tile[][] tiles;
	
	public Tilemap()
	{			
		createTilemap();
	}
	
	/**
	 * 
	 * @param col
	 * @param row
	 * @return The corresponding Tile on the tile map
	 * according to the indices provided. If incorrect indices are
	 * supplied an OutOfBoardException will be thrown.
	 */
	public Tile getTile(int col, int row)
	{
		if(col >= Board.BOARD_COLS || row >= Board.BOARD_ROWS ||
		   col < 0 || row < 0)
		{	
			return null;
		}
			
		return tiles[row][col];
	}

	public boolean isTileEmpty(int x, int y)
	{
		return getTile(x, y).isEmpty();
	}
	
	private void createTilemap()
	{
		tiles = new Tile[Board.BOARD_ROWS][Board.BOARD_COLS];
		
		for(int row = 0; row < Board.BOARD_ROWS; row++)
		{
			for(int col = 0; col < Board.BOARD_COLS; col++)
			{
				tiles[row][col] = new Tile(col, row, Board.boardLayout[row][col]);
			}
		}
	}	
}
