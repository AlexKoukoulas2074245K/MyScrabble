package com.myscrabble.entities;

import static com.myscrabble.managers.ResourceManager.STD_TEX_EXT;

import java.awt.Rectangle;

import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.MouseManager;
import com.myscrabble.states.Play;
import com.myscrabble.util.RenderUtils;
/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A class that represents a single letter tile containing
 * a letter and respective points. NOT to be confused with
 * the Tile class which is related to the functionality 
 * of the non-visible tilemap
 */
public class LetterTile extends GameObject
{
    public enum Direction
    {
        LEFT(0),
        RIGHT(1),
        UP(2),
        DOWN(3);
        
        public int value;
        
        private Direction(int value)
        {
            this.value = value;
        }
    }

	
	/* Standard Tile Size (equals TileSize of Tile class) */
	public static final int TILE_SIZE = Tile.TILE_SIZE;
	
	/* LetterTile highlight status flags */
	public static final int HIGHLIGHT_IDLE = 0;
	public static final int HIGHLIGHT_SELECTED = 1;
	public static final int HIGHLIGHT_DESELECTED = 2;
	
	/* Default texture path for all letters */
	private static String DEFAULT_LETTER_PATH = "/tiles/" + Play.TILE_STYLE + "/";
	private static String EMPTY_LETTER_PATH = DEFAULT_LETTER_PATH + "NONE" + STD_TEX_EXT;
	
	/* GameObject's textures HashMap key to texture */
	private static final int LETTER_TEX = 0;
	private static final int NONE_TEX = 1;
	
	/* No movement goal */
	private static final int NO_MOVE_GOAL = -1;
	
	/* Standard vertical speed */
	private static final float V_SPEED = 2f;
	
	private Player playerRef;
	private boolean[] pushedFlags;
	
	private char letter;
	private int points;
	private int highlightStatus;
	private float movingGoalPos;
	
	private boolean grabbed;
	private boolean recentlyAdded;
	private boolean drawAnimating;
	
	private float aniGoalX;
	private float aniGoalY;
	private int finalIndex;
	
	public LetterTile(GameStateManager gsm, Player playerRef, char letter,
					       int points, boolean drawAnimating, int index)
	{
		super(gsm);
		
		this.playerRef = playerRef;
		this.letter = letter;
		this.points = points;
		this.drawAnimating = drawAnimating;
		this.finalIndex = index;
		
		positionalInitialization();
		
		highlightStatus = HIGHLIGHT_IDLE;
		
		grabbed = false;
		recentlyAdded = false;
		
		movingGoalPos = -1;
		
		pushedFlags = new boolean[4];
		pushedFlags[Direction.LEFT.value]  = true;
		pushedFlags[Direction.RIGHT.value] = false;
		pushedFlags[Direction.UP.value]    = false;
		pushedFlags[Direction.DOWN.value]  = false;
		
		loadTexture();
	}
 
	private void positionalInitialization()
	{
		if(drawAnimating)
		{
    		this.aniGoalX = TileRack.getTilePos(finalIndex, playerRef.isHuman())[0];
    		this.aniGoalY = TileRack.getTilePos(finalIndex, playerRef.isHuman())[1];
    		this.x = LetterBag.LETTER_X_OFFSET;
    		this.y = LetterBag.LETTER_Y_OFFSET;
    		this.vy = LetterBag.JUMP_START;
		}
		else
		{
		    this.x = TileRack.getTilePos(finalIndex, playerRef.isHuman())[0];
		    this.y = TileRack.getTilePos(finalIndex, playerRef.isHuman())[1]; 
		}
		this.x0 = x;
		this.y0 = y;
	}
			
	@Override
	public void update()
	{   
		if(highlightStatus != HIGHLIGHT_IDLE)
		{
			updateHighlight();
		}
		else
		{
		    y = TileRack.getTilePos(finalIndex, playerRef.isHuman())[1];
		}
		
		if(grabbed)
		{
			updateGrabbed();
		}
		
		if(movingGoalPos != NO_MOVE_GOAL)
		{
			updateMoving();
		}
	}
	
	public void updateDrawAnimation()
	{
	    vx = approach(LetterBag.getAppropriateXSpeed(finalIndex), vx, LetterBag.X_SPEED_INCS);
	    x += vx;
	    
	    vy += LetterBag.GRAVITY;
	    y += vy;
	    
	    if(x <= aniGoalX)
	    {
	        x = aniGoalX;
	        x0 = x;
	        y = aniGoalY;
	        y0 = y;
	        vx = 0;
            vy = 0;
	        drawAnimating = false;
	        highlightStatus = HIGHLIGHT_IDLE;
	    }      
	}
	
	/**
	 * Updates the letterTile until it reaches
	 * its position goal.
	 */
	private void updateMoving()
	{
		if(movingGoalPos != NO_MOVE_GOAL && movingGoalPos > x0)
		{
			x = approach(movingGoalPos, x, V_SPEED);
		}
		else if(movingGoalPos != NO_MOVE_GOAL && movingGoalPos < x0)
		{
			x = approach(movingGoalPos, x, V_SPEED);
		}
			
		if(movingGoalPos < x0 && x <= movingGoalPos)
		{
			x = movingGoalPos;
			x0 = x;
			movingGoalPos = NO_MOVE_GOAL;
		}
		else if(movingGoalPos > x0 && x >= movingGoalPos)
		{
			x = movingGoalPos;
			x0 = x;
			movingGoalPos = NO_MOVE_GOAL;
		}
	}
	
	/**
	 * Highlight jumping of letter tiles on mouse hovering over them.
	 * Linear interpolation is used to approach each end.
	 */
	private void updateHighlight()
	{
		if(highlightStatus == HIGHLIGHT_SELECTED)
		{
		    float goal = y0 - TILE_SIZE / 2;
			y = approach(goal, y, V_SPEED);
			
		}
		else if(highlightStatus == HIGHLIGHT_DESELECTED)
		{
		    float goal = y0 + TILE_SIZE / 2;
			y = approach(goal, y, V_SPEED);
			
			if(y >= y0)
			{
				highlightStatus = HIGHLIGHT_IDLE;
			}
		}
	}
	
	private void updateGrabbed()
	{
		x = MouseManager.getX() - TILE_SIZE/2;
		y = MouseManager.getY() - TILE_SIZE/2;
	}
	
	/**
	 * Explanation: Check if the mouse is hovering over.
	 * if the mouse is hovering over this tile and the status is not selected already
	 * make the tile jump.
	 * if the mouse is NOT hovering over this tile and it is also not near the starting 
	 * position of this tile(Original Rectangle) and this tile is selected(from previous
	 * frames) make it fall.
	 */
	public void highlightResponse(float mouseX, float mouseY)
	{
		if(getRect().contains(mouseX, mouseY))
		{
			if(highlightStatus != LetterTile.HIGHLIGHT_SELECTED && !recentlyAdded)
			{
				highlightStatus = LetterTile.HIGHLIGHT_SELECTED;
			}
		}
		else
		{
			if(highlightStatus == LetterTile.HIGHLIGHT_SELECTED &&
			   !getOriginalRect().contains(mouseX, mouseY)) 
					   						 
			{
				highlightStatus = LetterTile.HIGHLIGHT_DESELECTED;
			}
			
			if(recentlyAdded)
			{
				recentlyAdded = false;
			}
		}
	}
	
	@Override
	public void render()
	{		
		RenderUtils.renderTexture(getTexture(LETTER_TEX), x, y);
	}
	
	public void emptyRender()
	{
	    RenderUtils.renderTexture(getTexture(NONE_TEX), x, y);
	}
	
	/**
	 * 
	 * @param direction. The direction of the push
	 * Before moving it asserts that the tile will
	 * not move in the same direction twice in a row
	 */
	public void push(Direction direction)
	{
		if(direction == Direction.LEFT && !pushedFlags[Direction.LEFT.value])
		{
			pushedFlags[Direction.RIGHT.value] = false;
			pushedFlags[Direction.LEFT.value] = true;
			movingGoalPos = x - TILE_SIZE;
		}
		else if(direction == Direction.RIGHT && !pushedFlags[Direction.RIGHT.value])
		{
			pushedFlags[Direction.RIGHT.value] = true;
			pushedFlags[Direction.LEFT.value] = false;
			movingGoalPos = x + TILE_SIZE;
		}	
	}
	
	private void loadTexture()
	{
		String fullPath = DEFAULT_LETTER_PATH + letter + STD_TEX_EXT;
		addTexture(LETTER_TEX, fullPath);
		addTexture(NONE_TEX, EMPTY_LETTER_PATH);
	}
	
	/* Getters / Setters */
	public Player getPlayerRef()
	{
		return playerRef;
	}
	
	public char getLetter()
	{
		return letter;
	}
	
	public boolean isNeutral()
	{
	    return playerRef == null;
	}
	
	public boolean getGrabbed()
	{
		return grabbed;
	}
	
	public boolean getIdle()
	{
		return movingGoalPos == NO_MOVE_GOAL;
	}
	
	public boolean[] getFlags()
	{
		return pushedFlags;
	}
	
	public boolean canBeMoved(Direction direction)
	{
		return !pushedFlags[direction.value];
	}
	
	public int getPoints()
	{
		return points;
	}
	
	public int getHighlightStatus()
	{
		return highlightStatus;
	}
	
	public float getCenterX()
	{
		return (float)getRect().getCenterX();
	}
	
	public float getCenterY()
	{
		return (float)getRect().getCenterY();
	}
	
	/* Current Rectangle */
	public Rectangle getRect()
	{
		return new Rectangle((int)x, (int)y, 
							 (int)Tile.TILE_SIZE, (int)Tile.TILE_SIZE);
	}
	
	/* I.E. The original rectangle formed from x0, y0 */
	public Rectangle getOriginalRect()
	{
		return new Rectangle((int)x0, (int)y0,
							 (int)Tile.TILE_SIZE, (int)Tile.TILE_SIZE);
	}
	
	public boolean isRecentlyAdded()
	{
		return recentlyAdded;
	}
	
	public boolean getDrawAnimating()
	{
	    return drawAnimating;
	}
	
	public void clearPlayerRef()
	{
	    playerRef = null;
	}
	
	public void setPlayerRef(Player playerRef)
	{
		this.playerRef = playerRef;
	}
	
	public void setHighlightStatus(int highlightStatus)
	{
		this.highlightStatus = highlightStatus;
	}
	
	public void setGrabbed(boolean grabbed)
	{
		this.grabbed = grabbed;
	}
	
	public void setPushDir(Direction direction, boolean status)
	{
		pushedFlags[direction.value] = status;
	}
	
	public void setFlags(boolean[] pushedFlags)
	{
		this.pushedFlags = pushedFlags;
	}
	
	public void setRecentlyAdded(boolean recentlyAdded)
	{
		this.recentlyAdded = recentlyAdded;
	}
	
	public void setDrawAnimating(boolean drawAnimating)
	{
	    this.drawAnimating = drawAnimating;
	}
}
