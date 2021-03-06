package com.myscrabble.entities;

import java.util.ArrayList;

import com.myscrabble.entities.LetterTile.Movement;



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
	
    public boolean isTileEmptyAI(int x, int y)
    {
        if(x < 0 || y < 0 || x >= Board.BOARD_COLS || y >= Board.BOARD_ROWS)
        {
            return true;
        }
        
        return getTile(x, y).isEmpty();
    }
    
    public boolean isTileEmpty(int x, int y)
    {
        if(x < 0 || y < 0 || x >= Board.BOARD_COLS || y >= Board.BOARD_ROWS)
        {
            return false;
        }
        
        return getTile(x, y).isEmpty();
    }
	
	/**
	 * 
	 * @param letterTile to be searched in the tile map
	 * @return The tile holding that letter tile
	 */
	public Tile getLetterTileHolder(LetterTile letterTile)
	{
	    for(Tile[] allTiles : tiles)
	    {
	        for(Tile tile : allTiles)
	        {
	            if(tile.getLetterTile() == letterTile)
	            {
	                return tile;
	            }
	        }
	    }
	    
	    System.out.println("Tile not found!");
	    return null;
	}
	
	/**
	 * 
	 * @param lt The LetterTile to check
	 * @return the maximum between the number of horizontal
	 * and vertical free tiles with lt being the origin
	 */
	public int getFreedomSpace(LetterTile lt)
	{
		//direction freedom accumulators
		int verUpFreedom = 0;
		int verDownFreedom = 0;
		int horLeftFreedom = 0;
		int horRightFreedom = 0;
		
		Tile tileHolder = getLetterTileHolder(lt);
		
		int holderCol = tileHolder.getCol();
		int holderRow = tileHolder.getRow();
		
		for(int i = holderCol + 1; i < Board.BOARD_COLS; i++)
		{
			if(isTileEmptyAI(i, holderRow) && isTileEmptyAI(i + 1, holderRow) &&
			   isTileEmptyAI(i, holderRow + 1) && isTileEmptyAI(i, holderRow - 1))
			{
				horRightFreedom++;
			}
			else
			{
				break;
			}
		}
		
		for(int i = holderCol - 1; i >= 0; i--)
		{
			if(isTileEmptyAI(i, holderRow) && isTileEmptyAI(i - 1, holderRow) &&
			   isTileEmptyAI(i, holderRow + 1) && isTileEmptyAI(i, holderRow - 1))
			{
				horLeftFreedom++;
			}
			else
			{
				break;
			}
		}
		
		for(int i = holderRow + 1; i < Board.BOARD_ROWS; i++)
		{
			if(isTileEmptyAI(holderCol, i) && isTileEmptyAI(holderCol, i + 1) &&
			   isTileEmptyAI(holderCol + 1, i) && isTileEmptyAI(holderCol - 1, i))
			{
				verDownFreedom++;
			}
			else
			{
				break;
			}
		}
		
		for(int i = holderRow - 1; i >= 0; i--)
		{
			if(isTileEmptyAI(holderCol, i) && isTileEmptyAI(holderCol, i - 1) &&
			   isTileEmptyAI(holderCol + 1, i) && isTileEmptyAI(holderCol - 1, i))
			{
				verUpFreedom++;
			}
			else
			{
				break;
			}
		}
		
		
		int totalHorFreedom = horLeftFreedom + horRightFreedom;
		int totalVerFreedom = verUpFreedom + verDownFreedom;
		
		if(totalHorFreedom >= totalVerFreedom && 
		   isTileEmpty(holderCol + 1, holderRow) && 
		   isTileEmpty(holderCol - 1, holderRow))
		{
		    lt.setAIMovement(Movement.HORIZONTAL); 
		    lt.setAIDirectionFreedom(new int[]{horLeftFreedom, horRightFreedom});
		    return totalHorFreedom;
		}
		else if(totalHorFreedom <= totalVerFreedom &&
		        isTileEmpty(holderCol, holderRow + 1) &&
		        isTileEmpty(holderCol, holderRow - 1))
		{
		    lt.setAIMovement(Movement.VERTICAL);
		    lt.setAIDirectionFreedom(new int[]{verUpFreedom, verDownFreedom});
		    return totalVerFreedom;
		}
		else
		{
		    return 0;
		}
	}
	
	public ArrayList<LetterTile> getNeutralLetterTiles()
	{
	    ArrayList<LetterTile> result = new ArrayList<>();
	    
	    for(Tile[] allTiles : tiles)
	    {
	        for(Tile tile : allTiles)
	        {
	            if(tile.getLetterTile() != null && tile.getLetterTile().isNeutral())
	            {
	                result.add(tile.getLetterTile());
	            }
	        }
	    }
	    
	    return result;
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
		letterTile.setHighlightStatus(LetterTile.HIGHLIGHT_IDLE);
		
		/* place the letter tile on top of the tile */
		targetTile.setTile(letterTile);
		
		/* reset tile indicator status to none */
		tileIndicator.setStatus(TileIndicator.NONE);
	}
	
	/**
	 * 
	 * @param letterTile To be added
	 * Does not use tile indicator to find the 
	 * correct position of that tile (i.e. the tile
	 * needs to be in the correct position prior
	 * to addition) 
	 */
	public void addLetterTile(LetterTile letterTile)
	{
		int targetCol = ((int)letterTile.getX() - Board.SIDE_WIDTH - Board.X_OFFSET) / Tile.TILE_SIZE;
		int targetRow = ((int)letterTile.getY() - Board.SIDE_HEIGHT - Board.Y_OFFSET) / Tile.TILE_SIZE;
		
		Tile targetTile = getTile(targetCol, targetRow);
		
		targetTile.setTile(letterTile);
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
