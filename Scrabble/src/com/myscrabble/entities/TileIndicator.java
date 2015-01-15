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
	public static final int NONE_ANI    = -1;
	public static final int FAILURE_ANI = 0;
	public static final int SUCCESS_ANI = 1;
	public static final int NOMRAL_ANI  = 2;
	

	private static final String TEX_FAILURE_ROOT_DIR = "/tiles/selection/failure/";
	private static final String TEX_NORMAL_ROOT_DIR  = "/tiles/selection/normal";
	private static final String TEX_SUCCESS_ROOT_DIR = "/tiles/selection/success/";
	
	private static final int ANI_DELAY = 5;
	
	private int currentAnimation;
	
	public TileIndicator(GameStateManager  gsm)
	{
		super(gsm);
		loadAnimations();
		
		currentAnimation = NONE_ANI;
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
		if(currentAnimation != NONE_ANI)
		{
			animations.get(currentAnimation).update();
		}
	}
	
	@Override
	public void render()
	{
		if(currentAnimation != NONE_ANI)
		{
			RenderUtils.renderTexture(animations.get(currentAnimation).getCurrentFrame(), x, y);
		}
	}
	
	public void setCurrentAnimation(int currentAnimation)
	{
		this.currentAnimation = currentAnimation;
	}
}
