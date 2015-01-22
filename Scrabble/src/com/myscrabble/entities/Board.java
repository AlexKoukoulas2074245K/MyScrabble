package com.myscrabble.entities;

import static com.myscrabble.managers.ResourceManager.STD_TEX_EXT;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.MouseManager;
import com.myscrabble.rendering.Shader;
import com.myscrabble.rendering.Shader.ShaderType;
import com.myscrabble.util.RenderUtils;
import com.myscrabble.util.ScrabbleUtils;
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
    public enum BoardColor
    {
        DARK_GREEN(54, 149, 57),
        DARK_BLUE(70, 86, 107),
        BROWN(92, 77, 56),
        ORANGE(189, 141, 7),
        PURPLE(90, 9, 97);
        
        private float[] colour;
        
        private BoardColor(int r, int g, int b)
        {
            colour = RenderUtils.getGLColor(r, g, b);
        }
        
        public float[] getColor()
        {
            return colour;
        }
    }
    
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
		{ 5 , 0 , 0 , 2 , 0 , 0 , 0 , 5 , 0 , 0 , 0 , 2 , 0 , 0 , 5 },
		{ 0 , 4 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 4 , 0 },
		{ 0 , 0 , 4 , 0 , 0 , 0 , 2 , 0 , 2 , 0 , 0 , 0 , 4 , 0 , 0 },
		{ 2 , 0 , 0 , 4 , 0 , 0 , 0 , 2 , 0 , 0 , 0 , 4 , 0 , 0 , 2 },
		{ 0 , 0 , 0 , 0 , 4 , 0 , 0 , 0 , 0 , 0 , 4 , 0 , 0 , 0 , 0 },
		{ 0 , 3 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 3 , 0 , 0 , 3 , 0 , 0 },
		{ 0 , 0 , 2 , 0 , 0 , 0 , 2 , 0 , 2 , 0 , 0 , 2 , 0 , 0 , 0 },
		{ 5 , 0 , 0 , 2 , 0 , 0 , 0 , 4 , 0 , 0 , 2 , 0 , 0 , 0 , 5 },
		{ 0 , 0 , 2 , 0 , 0 , 0 , 2 , 0 , 2 , 0 , 0 , 2 , 0 , 0 , 0 },
		{ 0 , 3 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 3 , 0 , 0 , 3 , 0 , 0 },
		{ 0 , 0 , 0 , 0 , 4 , 0 , 0 , 0 , 0 , 0 , 4 , 0 , 0 , 0 , 0 },
		{ 2 , 0 , 0 , 4 , 0 , 0 , 0 , 2 , 0 , 0 , 0 , 4 , 0 , 0 , 2 },
		{ 0 , 0 , 4 , 0 , 0 , 0 , 2 , 0 , 2 , 0 , 0 , 0 , 4 , 0 , 0 },
		{ 0 , 4 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 4 , 0 },
		{ 5 , 0 , 0 , 2 , 0 , 0 , 0 , 5 , 0 , 0 , 0 , 2 , 0 , 0 , 5 }
	};
	
	/* Texture Paths */
	private static final String BOARD_TEX_PATH = "/board/board_trans" + STD_TEX_EXT;
	
	/* Texture Flags */
	private static final int BOARD_TEXTURE = 0;
	
	private BoardColor backgroundColor;
	
	private Tilemap tilemap;
	private TileIndicator tileIndicator;
	private Shader coloringShader;
	
	/* Used to keep track of players' current letter
	 * formations. (i.e. the word created so far in 
	 * the board by a player) */
	private HashMap<Player, TileFormation> playerFormations;
	
	public Board(GameStateManager gsm)
	{
		super(gsm);
		
		addTexture(BOARD_TEXTURE, BOARD_TEX_PATH);
		
		x = X_OFFSET;
		y = Y_OFFSET;
		
		tilemap = new Tilemap();
		tileIndicator = new TileIndicator(gsm);
		
		playerFormations = new HashMap<Player, TileFormation>();
		
		coloringShader = new Shader(ShaderType.COLORING);
		
		backgroundColor = BoardColor.DARK_GREEN;
	}
	
	@Override
	public void update()
	{
		tileIndicator.update();
		if(playerFormations.size() == 1)
		{
		    for(Entry<Player, TileFormation> entry : playerFormations.entrySet())
		    {
		        System.out.println(entry.getValue().getWord());
		    }
		}
	}
	
	@Override
	public void render()
	{
		drawColoredBackground();
		RenderUtils.renderTexture(getTexture(BOARD_TEXTURE), x, y);
		tilemap.render();
		tileIndicator.render();
	}
	
	private void drawColoredBackground()
	{
	    coloringShader.useProgram();
        coloringShader.setUniform3f("inputColor", backgroundColor.getColor());
        RenderUtils.renderRectangle(x, y, getTexture(BOARD_TEXTURE).getTextureWidth(), getTexture(BOARD_TEXTURE).getTextureHeight());
        coloringShader.stopProgram();
	}
	
	/**
	 * Response to hovering over the game board 
	 * with the mouse holding a letter tile.
	 */
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
	
	/**
	 * 
	 * @param player Player requesting move finalisation.
	 * Finalises the player's move.
	 * Also removes the player's formation from
	 * playerFormations HashMap;
	 */
	public void makeMove(Player player)
	{  
	    playerFormations.get(player).releaseTiles();
	    playerFormations.remove(player);
	}
	
	/**
	 * 
	 * @param player To be cross checked with the letterTiles
	 * on the board for equality in references <br>
	 * Response to hovering over the gameboard without
	 * holding a letter tile
	 */
	public void hoveredOverWithoutTile(Player player)
	{
		if(getTileOnMouse() == null)
		{
			return;
		}
		
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
	
	/**
	 * 
	 * @return Whether or not the tile indicator
	 * points to the active player's letter tiles
	 * in the game board
	 */
	public boolean checkForTileWithdrawal()
	{	
		return tileIndicator.getStatus() == TileIndicator.NORMAL;
	}
	
	/**
	 * 
	 * @param playerRef Reference to the player
	 * @return The withdrawn letter tile from the
	 * game board.
	 */
	public LetterTile withdrawTile(Player playerRef)
	{
		LetterTile result = null;
		Tile targetTile = null;
		
		if(tileIndicator.getStatus() == TileIndicator.NORMAL)
		{
			disableIndicator();
			targetTile = tilemap.getTile(tileIndicator.getCol(), tileIndicator.getRow());
			result = targetTile.getLetterTile();
		}
		
		if(result != null && targetTile != null)
		{
		
		    popFromFormation(result, playerRef);
		    targetTile.clearTile();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param letterTile the letterTile to be added to the current playerFormation
	 * @param playerRef reference to the player that requeststed a letter addition
	 */
	public void addLetterTile(LetterTile letterTile, Player playerRef)
	{
		tilemap.addLetterTile(letterTile, tileIndicator);
		addToFormation(letterTile, playerRef);
	}
	
	public void disableIndicator()
	{
		tileIndicator.setStatus(TileIndicator.NONE);
	}
	
	/* Getters / Setters */
	public Rectangle getRect()
	{
		return new Rectangle((int)x + SIDE_WIDTH, (int)y + SIDE_HEIGHT, 
							 getTexture(BOARD_TEXTURE).getTextureWidth() - 2 * SIDE_WIDTH,
						     getTexture(BOARD_TEXTURE).getTextureHeight() - 2 * SIDE_HEIGHT);	
	}
	
	/**
	 * 
	 * @param player The Player requesting the calculation
	 * @return the points corresponding to the player's
	 * current word form their tile formation
	 */
	public int calculatePoints(Player player)
	{
	    return ScrabbleUtils.calculatePoints(playerFormations.get(player).getTiles(), tilemap);
	}
	
	/**
	 * 
	 * @param playerRef The player that requests his/her current
	 * word from their current tile formation.
	 * @return The player's current word from
	 * their current tile formation
	 */
	public String getCurrentWord(Player playerRef)
	{
		return playerFormations.get(playerRef).getWord();
	}
	
	/**
	 * 
	 * @param playerRef Player reference to check
	 * @return Whether player is registered in the
	 * playerFormations HashMap.
	 */
	public boolean getPlayerRegistered(Player playerRef)
	{
		return playerFormations.containsKey(playerRef);
	}
	
	/**
	 * 
	 * @param playerRef The player for which the tile formation will be checked
	 * @return Whether the player's tile formation has no gaps and all tiles
	 * are either in the same horizontal or vertical line in the game board.
	 */
	public boolean isCurrentFormationValid(Player playerRef)
	{
		return playerFormations.get(playerRef).isValidFormation(this);
	}
	
	/**
	 * 
	 * @param letterTile To be removed from the player's tile formation
	 * @param playerRef The player from whose tile formation the operation will be performed 
	 */
	private void popFromFormation(LetterTile letterTile, Player playerRef)
	{
	    playerFormations.get(playerRef).removeTile(letterTile);
	}
	
	/**
	 * 
	 * @param letterTile To be added to the player's tile formation
	 * @param playerRef The player on whose tile formation the letter tile
	 * will be appended
	 */
	private void addToFormation(LetterTile letterTile, Player playerRef)
	{
		if(!playerFormations.containsKey(playerRef))
		{	
			playerFormations.put(playerRef, new TileFormation());
		}
		
		playerFormations.get(playerRef).addTile(letterTile);
		playerFormations.get(playerRef).checkForNeutrals(getNeutralLetterTiles());
	}
	
	public ArrayList<LetterTile> getNeutralLetterTiles()
	{
	    return tilemap.getNeutralLetterTiles();
	}
	
	/**
	 * 
	 * @return The tile that corresponds to the present
	 * mouse coordinates
	 */
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
	
	public LetterTile getLetterTile(float x, float y)
	{
	    int col = (int)((x - X_OFFSET - SIDE_WIDTH) / Tile.TILE_SIZE);
	    int row = (int)((y - Y_OFFSET - SIDE_HEIGHT) / Tile.TILE_SIZE);
	    
	    return tilemap.getTile(col, row).getLetterTile();
	}
	
	public boolean getEmptyLetter()
	{
		return getTileOnMouse().isEmpty();
	}
	
	public float[] getTilePosMouse()
	{
		return getTileOnMouse().getPos();
	}
	
	/* Transformed mouse x and y coordinates which are
	 * used to extract a tile from the tile map */
	public int getTransfMouseX()
	{
		return MouseManager.getX() - X_OFFSET - SIDE_WIDTH;
	}
	
	public int getTransfMouseY()
	{
		return MouseManager.getY() - Y_OFFSET + SIDE_WIDTH;
	}
	
	/* The column and row that correspond to the
	 * current mouse coordiantes
	 */
	public int getTransfMouseCol()
	{
		return getTransfMouseX() / Tile.TILE_SIZE;
	}
	
	public int getTransfMouseRow()
	{
		return getTransfMouseY() / Tile.TILE_SIZE - 1;
	}
}
