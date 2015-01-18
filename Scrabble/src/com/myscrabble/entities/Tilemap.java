package com.myscrabble.entities;



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
	
	public void render()
	{
		for(Tile[] allTiles : tiles)
		{
			for(Tile tile : allTiles)
			{
				tile.render();
			}
		}
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

	/**
	 * 
	 * @param letterTile. The letter tile to be added to the tile map
	 * @param tileIndicator. The board's tile indicator used to 
	 * extract the positional information needed to store the letter tile.
	 */
	public void addLetterTile(LetterTile letterTile, TileIndicator tileIndicator)
	{
		/* grab target tile */
		Tile targetTile = getTile(tileIndicator.getCol(), tileIndicator.getRow());
		
		/* set position of letter tile */
		letterTile.setX(targetTile.getX());
		letterTile.setY(targetTile.getY());
		
		/* place the letter tile on top of the tile */
		targetTile.setTile(letterTile);
		
		/* reset tile indicator status to none */
		tileIndicator.setStatus(TileIndicator.NONE);
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
	
	/* Getters / Setters */
	public boolean isTileEmpty(int x, int y)
	{
		return getTile(x, y).isEmpty();
	}
	
}
