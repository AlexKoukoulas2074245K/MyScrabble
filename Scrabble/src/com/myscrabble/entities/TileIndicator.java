package com.myscrabble.entities;

import com.myscrabble.managers.GameStateManager;
import com.myscrabble.util.RenderUtils;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * Tile selector represents an object passed around to the
 * player objects that highlights, selects and moves letter tiles
 * around the board
 */
public class TileIndicator extends GameObject
{
	/* TileIndicator's status flags */
	public static final int NONE    = -1;
	public static final int FAILURE = 0;
	public static final int SUCCESS = 1;
	public static final int NORMAL  = 2;
	
	
	/* Root animation directories */
	private static final String TEX_FAILURE_ROOT_DIR = "/tiles/selection/failure/";
	private static final String TEX_NORMAL_ROOT_DIR  = "/tiles/selection/normal";
	private static final String TEX_SUCCESS_ROOT_DIR = "/tiles/selection/success/";
	
	/* Default animation delay */
	private static final int ANI_DELAY = 5;
	
	private int currentAnimation;
	private int status;
	private int col;
	private int row;
	
	public TileIndicator(GameStateManager  gsm)
	{
		super(gsm);
		loadAnimations();
		
		status = NONE;
		currentAnimation = NONE;
		
	}
	
	private void loadAnimations()
	{
		addAnimation(TEX_FAILURE_ROOT_DIR, ANI_DELAY);
		addAnimation(TEX_NORMAL_ROOT_DIR,  ANI_DELAY);
		addAnimation(TEX_SUCCESS_ROOT_DIR, ANI_DELAY);
	}
	
	@Override
	public void update()
	{	
		if(currentAnimation != NONE)
		{
			animations.get(currentAnimation).update();
		}
	}
	
	@Override
	public void render()
	{		
		if(currentAnimation != NONE)
		{
			RenderUtils.renderTexture(animations.get(currentAnimation).getCurrentFrame(), x, y);
		}
	}

	public int getStatus()
	{
		return status;
	}
	
	public int getCol()
	{
		return col;
	}
	
	public int getRow()
	{
		return row;
	}
	
	public void setCurrentAnimation(int currentAnimation)
	{
		this.currentAnimation = currentAnimation;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
		currentAnimation = status;
	}
	
	public void setCol(int col)
	{
		this.col = col;
	}
	
	public void setRow(int row)
	{
		this.row = row;
	}
	
	public void setPos(float[] pos)
	{
		if(pos.length < 2 || pos.length > 2) return;

		this.x = pos[0];
		this.y = pos[1];
	}
}
