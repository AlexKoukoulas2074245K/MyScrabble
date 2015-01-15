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
		tileIndicator.render();
	}
	 
	public void hoveredOver()
	{
		int mouseX = InputManager.getX() - X_OFFSET - SIDE_WIDTH;
		int mouseY = InputManager.getY() - Y_OFFSET + SIDE_WIDTH;
		
		Tile targetTile = tilemap.getTile(mouseX / Tile.TILE_SIZE, mouseY / Tile.TILE_SIZE - 1);
		
		if(targetTile == null)
		{
			return;
		}
		
		if(targetTile.isEmpty())
		{
			tileIndicator.setCurrentAnimation(TileIndicator.SUCCESS_ANI);
			tileIndicator.setX(targetTile.getX());
			tileIndicator.setY(targetTile.getY());
		}
		else
		{
			tileIndicator.setCurrentAnimation(TileIndicator.FAILURE_ANI);
			tileIndicator.setX(targetTile.getX());
			tileIndicator.setY(targetTile.getY());
		}
	}
	
	public void disableIndicator()
	{
		tileIndicator.setCurrentAnimation(TileIndicator.NONE_ANI);
	}
	
	public Rectangle getRect()
	{
		return new Rectangle((int)x + SIDE_WIDTH, (int)y + SIDE_HEIGHT, 
							 getTexture(BOARD_TEXTURE).getTextureWidth() - 2 * SIDE_WIDTH,
						     getTexture(BOARD_TEXTURE).getTextureHeight() - 2 * SIDE_HEIGHT);
		
	}
	
}
