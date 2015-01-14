package com.myscrabble.entities;

import static com.myscrabble.managers.ResourceManager.STD_TEX_EXT;

import java.awt.Rectangle;

import com.myscrabble.main.Main;
import com.myscrabble.managers.GameStateManager;
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
	public static final int BOARD_ROWS = 15;
	public static final int BOARD_COLS = 15;
	
	/* A matrix representation of the board with the special tiles included */ 
	public static final int[][] boardLayout = new int[][]{
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
	
	public Board(GameStateManager gsm)
	{
		super(gsm);
		
		addTexture(BOARD_TEXTURE, BOARD_TEX_PATH);
		addTexture(BOARD_COLOR, BOARD_COL_PATH);
		
		x = Main.getCenterDimensions()[0];
		y = Main.getCenterDimensions()[1];
		
		centerRendering = true;
		
		tilemap = new Tilemap();
	}
	
	@Override
	public void update()
	{
		
	}
	
	@Override
	public void render()
	{
		RenderUtils.renderTexture(getTexture(BOARD_COLOR), x, y, centerRendering);
		RenderUtils.renderTexture(getTexture(BOARD_TEXTURE), x, y, centerRendering);
	}
	
	public Rectangle getRect()
	{
		return new Rectangle((int)x - getTexture(BOARD_TEXTURE).getTextureWidth() / 2, 
							 (int)y - getTexture(BOARD_TEXTURE).getTextureHeight() / 2, 
							 		  getTexture(BOARD_TEXTURE).getImageWidth(),
									  getTexture(BOARD_TEXTURE).getImageHeight());
	}
	
}
