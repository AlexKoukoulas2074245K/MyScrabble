package com.myscrabble.entities;

import java.awt.Rectangle;

import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.InputManager;
import com.myscrabble.states.Play;
import com.myscrabble.util.RenderUtils;
import com.myscrabble.util.ScrabbleUtils;

import static com.myscrabble.managers.ResourceManager.STD_TEX_EXT;
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
	public static final int TILE_SIZE = Tile.TILE_SIZE;
	public static final int HIGHLIGHT_IDLE = 0;
	public static final int HIGHLIGHT_SELECTED = 1;
	public static final int HIGHLIGHT_DESELECTED = 2;
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	/* Default texture path for all letters */
	private static String DEFAULT_LETTER_PATH = "/tiles/" + Play.TILE_STYLE + "/";
	
	private static final int LETTER_TEX = 0;
	private static final int NO_MOVE_GOAL = -1;
	
	private static final float V_SPEED = 2f;
	
	
	private boolean[] pushedFlags;
	
	private char letter;
	private int points;
	
	private int highlightStatus;
	private boolean grabbed;
	private float movingGoalPos;
	
	public LetterTile(GameStateManager gsm, char letter, int points, float x, float y)
	{
		super(gsm);
		
		this.letter = letter;
		this.points = points;
		
		this.x = x;
		this.y = y;
		this.x0 = x;
		this.y0 = y;
		
		highlightStatus = HIGHLIGHT_IDLE;
		
		grabbed = false;
		movingGoalPos = -1;
		
		pushedFlags = new boolean[2];
		pushedFlags[LEFT] = false;
		pushedFlags[RIGHT] = false;
		
		loadTexture();
	}
	
	@Override
	public void update()
	{
	
		if(highlightStatus != HIGHLIGHT_IDLE)
		{
			updateHighlight();
		}
		
		if(grabbed)
		{
			updateGrabbed();
		}
		
		if(movingGoalPos != NO_MOVE_GOAL)
		{
			updateMoving();
		}
	
//		x += vx;
//		y += vy;
		
	}
	
	/**
	 * Updates the letterTile until it reaches
	 * its position goal.
	 */
	private void updateMoving()
	{
		if(movingGoalPos != NO_MOVE_GOAL && movingGoalPos > x0)
		{
			x = approachPos(movingGoalPos, x, V_SPEED);
		}
		else if(movingGoalPos != NO_MOVE_GOAL && movingGoalPos < x0)
		{
			x = approachNeg(movingGoalPos, x, V_SPEED);
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
			y = approachNeg(y0 - TILE_SIZE / 2, y, V_SPEED);
			
			if(y <= y0 - TILE_SIZE / 2)
			{
				y = y0 - TILE_SIZE / 2;
			}
		}
		else if(highlightStatus == HIGHLIGHT_DESELECTED)
		{
			y = approachPos(y0 + TILE_SIZE / 2, y, V_SPEED);
			
			if(y >= y0)
			{
				highlightStatus = HIGHLIGHT_IDLE;
			}
		}
	}
	//TODO REMOVE!
	public float getMoveGoal()
	{
		return movingGoalPos;
	}
	
	private void updateGrabbed()
	{
		x = InputManager.getX() - TILE_SIZE/2;
		y = InputManager.getY() - TILE_SIZE/2;
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
		if(ScrabbleUtils.getContainment(mouseX, mouseY, getRect()))
		{
			if(highlightStatus != LetterTile.HIGHLIGHT_SELECTED)
			{
				highlightStatus = LetterTile.HIGHLIGHT_SELECTED;
			}
		}
		else
		{
			if(highlightStatus == LetterTile.HIGHLIGHT_SELECTED &&
			   !ScrabbleUtils.getContainment(mouseX, 
					   						 mouseY, 
					   						 getOriginalRect()))
			{
				highlightStatus = LetterTile.HIGHLIGHT_DESELECTED;
			}
		}
	}
	
	@Override
	public void render()
	{
		RenderUtils.renderTexture(getTexture(LETTER_TEX), x, y);
	}
	
	
	public void pushLeft()
	{
		push(LEFT);
	}
	
	public void pushRight()
	{
		push(RIGHT);
	}
	
	/* Getters / Setters */
	public char getLetter()
	{
		return letter;
	}
	
	public boolean getGrabbed()
	{
		return grabbed;
	}
	
	public boolean getIdle()
	{
		return movingGoalPos == NO_MOVE_GOAL;
	}
	
	public boolean canBeMoved(int direction)
	{
		return !pushedFlags[direction];
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
	
	public void setHighlightStatus(int highlightStatus)
	{
		this.highlightStatus = highlightStatus;
	}
	
	public void setGrabbed(boolean grabbed)
	{
		this.grabbed = grabbed;
	}
	
	public void setPushDir(int direction, boolean status)
	{
		pushedFlags[direction] = status;
	}
	
	/**
	 * 
	 * @param direction. The direction of the push
	 * Before moving it asserts that the tile will
	 * not move in the same direction twice in a row
	 */
	private void push(int direction)
	{
		if(direction == LEFT && !pushedFlags[LEFT])
		{
			pushedFlags[RIGHT] = false;
			pushedFlags[LEFT] = true;
			movingGoalPos = x - TILE_SIZE;
		}
		else if(direction == RIGHT && !pushedFlags[RIGHT])
		{
			pushedFlags[RIGHT] = true;
			pushedFlags[LEFT] = false;
			movingGoalPos = x + TILE_SIZE;
		}	
	}
	
	private void loadTexture()
	{
		String fullPath = DEFAULT_LETTER_PATH + letter + STD_TEX_EXT;
		addTexture(LETTER_TEX, fullPath);
	}
	
}