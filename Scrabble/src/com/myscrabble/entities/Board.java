package com.myscrabble.entities;

import static com.myscrabble.managers.ResourceManager.STD_TEX_EXT;

import java.awt.Rectangle;

import com.myscrabble.main.Main;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.InputManager;
import com.myscrabble.util.OutOfBoardException;
import com.myscrabble.util.RenderUtils;
/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * Board represents the board used for playing the game.
 * It has a default color, texture and a tile map containing
 * place holders for letter tiles amongst others
 */

public class Board extends GameObject 
{
	/* Board Dimensions */
	public static final int WIDTH = 512;
	public static final int HEIGHT = 512;
	
	/* The drawing position of the game board */
	public static final int X_OFFSET = 2 * Tile.TILE_SIZE;
	public static final int Y_OFFSET = 2 * Tile.TILE_SIZE;
	
	/* The dimensions of the sides of the board */
	public static final int SIDE_WIDTH  = 15;
	public static final int SIDE_HEIGHT = 15;
	
	/* Standard rows and columns of the game board */
	public static final int BOARD_ROWS = 15;
	public static final int BOARD_COLS = 15;
	
	/* A matrix representation of the board with the special tiles included */ 
	static final int[][] boardLayout = new int[][]{
		{ 4 , 0 , 0 , 1 , 0 , 0 , 0 , 4 , 0 , 0 , 0 , 1 , 0 , 0 , 4 },
		{ 0 , 3 , 0 , 0 , 0 , 2 , 0 , 0 , 0 , 2 , 0 , 0 , 0 , 3 , 0 },
		{ 0 , 0 , 3 , 0 , 0 , 0 , 1 , 0 , 1 , 0 , 0 , 0 , 3 , 0 , 0 },
		{ 1 , 0 , 0 , 3 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 3 , 0 , 0 , 1 },
		{ 0 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 0 },
		{ 0 , 2 , 0 , 0 , 0 , 2 , 0 , 0 , 0 , 2 , 0 , 0 , 2 , 0 , 0 },
		{ 0 , 0 , 1 , 0 , 0 , 0 , 1 , 0 , 1 , 0 , 0 , 1 , 0 , 0 , 0 },
		{ 4 , 0 , 0 , 1 , 0 , 0 , 0 , 3 , 0 , 0 , 1 , 0 , 0 , 0 , 4 },
		{ 0 , 0 , 1 , 0 , 0 , 0 , 1 , 0 , 1 , 0 , 0 , 1 , 0 , 0 , 0 },
		{ 0 , 2 , 0 , 0 , 0 , 2 , 0 , 0 , 0 , 2 , 0 , 0 , 2 , 0 , 0 },
		{ 0 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 0 },
		{ 1 , 0 , 0 , 3 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 3 , 0 , 0 , 1 },
		{ 0 , 0 , 3 , 0 , 0 , 0 , 1 , 0 , 1 , 0 , 0 , 0 , 3 , 0 , 0 },
		{ 0 , 3 , 0 , 0 , 0 , 2 , 0 , 0 , 0 , 2 , 0 , 0 , 0 , 3 , 0 },
		{ 4 , 0 , 0 , 1 , 0 , 0 , 0 , 4 , 0 , 0 , 0 , 1 , 0 , 0 , 4 }
	};
	
	/* Texture Paths */
	private static final String BOARD_TEX_PATH = "/board/board_trans" + STD_TEX_EXT;
	private static final String BOARD_COL_PATH = "/board/boardColors/1" + STD_TEX_EXT;
	
	/* Texture Flags */
	private static final int BOARD_TEXTURE = 0;
	private static final int BOARD_COLOR = 1;
	
	private Tilemap tilemap;
	private TileIndicator tileIndicator;
	
	public Board(GameStateManager gsm)
	{
		super(gsm);
		
		addTexture(BOARD_TEXTURE, BOARD_TEX_PATH);
		addTexture(BOARD_COLOR, BOARD_COL_PATH);
		
		x = X_OFFSET;
		y = Y_OFFSET;
		
		tilemap = new Tilemap();
		tileIndicator = new TileIndicator(gsm);
		
	}
	
	@Override
	public void update()
	{
		tileIndicator.update();
	}
	
	@Override
	public void render()
	{
		RenderUtils.renderTexture(getTexture(BOARD_COLOR), x, y);
		RenderUtils.renderTexture(getTexture(BOARD_TEXTURE), x, y);
		tilemap.render();
		tileIndicator.render();
	}
	 
	public void hoveredOverWithTile()
	{
		
		Tile targetTile = tilemap.getTile(getTransfMouseCol(), getTransfMouseRow());
		
		if(targetTile == null)
		{
			return;
		}
		
		if(targetTile.isEmpty())
		{
			tileIndicator.setStatus(TileIndicator.SUCCESS);
		}
		else
		{
			tileIndicator.setStatus(TileIndicator.FAILURE);
		}
		
		tileIndicator.setPos(targetTile.getPos());
		tileIndicator.setCol(targetTile.getCol());
		tileIndicator.setRow(targetTile.getRow());
	}
	
	public void hoveredOverWithoutTile(Player player)
	{
		if(!getEmptyLetter() && getTileOnMouse().getLetterTile().getPlayerRef() == player)
		{
			tileIndicator.setStatus(TileIndicator.NORMAL);
			tileIndicator.setPos(getTilePosMouse());
			tileIndicator.setCol(getTileOnMouse().getCol());
			tileIndicator.setRow(getTileOnMouse().getRow());
		}
		else
		{
			disableIndicator();
		}
	}
	
	public boolean checkForTileWithdrawal()
	{	
		if(tileIndicator.getStatus() == TileIndicator.NORMAL)
		{
			return true;
		}
		
		return false;
	}
	
	public LetterTile getTileWithdrawal()
	{
		LetterTile result = null;
		
		if(tileIndicator.getStatus() == TileIndicator.NORMAL)
		{
			disableIndicator();
			Tile targetTile = tilemap.getTile(tileIndicator.getCol(), tileIndicator.getRow());
			result = targetTile.getLetterTile();
			
			targetTile.clearTile();
		}
		
		return result;
	}
	
	public void disableIndicator()
	{
		tileIndicator.setStatus(TileIndicator.NONE);
	}
	
	public void addLetterTile(LetterTile letterTile)
	{
		tilemap.addLetterTile(letterTile, tileIndicator);
	}
	
	/* Getters / Setters */
	public Rectangle getRect()
	{
		return new Rectangle((int)x + SIDE_WIDTH, (int)y + SIDE_HEIGHT, 
							 getTexture(BOARD_TEXTURE).getTextureWidth() - 2 * SIDE_WIDTH,
						     getTexture(BOARD_TEXTURE).getTextureHeight() - 2 * SIDE_HEIGHT);
		
	}
	
	private Tile getTileOnMouse()
	{
		if(tilemap.getTile(getTransfMouseCol(), getTransfMouseRow()) == null)
		{
			return null;
		}
		
		return tilemap.getTile(getTransfMouseCol(), getTransfMouseRow());
	}
	
	public TileIndicator getIndicator()
	{
		return tileIndicator;
	}
	
	public boolean getEmptyLetter()
	{
		return getTileOnMouse().isEmpty();
	}
	
	public float[] getTilePosMouse()
	{
		return getTileOnMouse().getPos();
	}
	
	public int getTransfMouseX()
	{
		return InputManager.getX() - X_OFFSET - SIDE_WIDTH;
	}
	
	public int getTransfMouseY()
	{
		return InputManager.getY() - Y_OFFSET + SIDE_WIDTH;
	}
	
	public int getTransfMouseCol()
	{
		return getTransfMouseX() / Tile.TILE_SIZE;
	}
	
	public int getTransfMouseRow()
	{
		return getTransfMouseY() / Tile.TILE_SIZE - 1;
	}
}
