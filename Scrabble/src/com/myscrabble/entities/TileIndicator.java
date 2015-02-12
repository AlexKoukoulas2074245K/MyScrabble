package com.myscrabble.entities;

import com.myscrabble.managers.GameStateManager;
import com.myscrabble.rendering.Shader;
import com.myscrabble.rendering.Shader.ShaderType;
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
	private static final String TEX_FAILURE_PATH = "/tiles/selection/failure.png";
	private static final String TEX_SUCCESS_PATH = "/tiles/selection/success.png";
	private static final String TEX_NORMAL_PATH  = "/tiles/selection/normal.png";
	
	private static final float MAX_BRIGHT_FACT = 1.5f;
	private static final float MIN_BRIGHT_FACT = 0.5f;
	private static final float BRIGHT_INTERS   = 0.02f;
	
	private Shader brightnessShader;
	private boolean brightRaiseFlag;
	private float brightnessFactor;
	
	private int currentAnimation;
	private int status;
	private int col;
	private int row;
	
	public TileIndicator(GameStateManager  gsm)
	{
		super(gsm);
		loadTextures();
		
		brightnessShader = new Shader(ShaderType.AUTO_BRIGHTNESS); 
		status = NONE;
		currentAnimation = NONE;
		
	}
	
	private void loadTextures()
	{
		addTexture(FAILURE, TEX_FAILURE_PATH);
		addTexture(SUCCESS, TEX_SUCCESS_PATH);
		addTexture(NORMAL, TEX_NORMAL_PATH);
	}
	
	@Override
	public void update()
	{	
		if (brightRaiseFlag)
		{
			brightnessFactor += BRIGHT_INTERS;
			
			if (brightnessFactor >= MAX_BRIGHT_FACT)
			{
				brightRaiseFlag = !brightRaiseFlag;
			}
		}
		else
		{
			brightnessFactor -= BRIGHT_INTERS;
			
			if (brightnessFactor <= MIN_BRIGHT_FACT)
			{
				brightRaiseFlag = !brightRaiseFlag;
			}
		}
	}
	
	@Override
	public void render()
	{		
		if(currentAnimation != NONE)
		{
			brightnessShader.useProgram();
			brightnessShader.setUniformf("brightnessFactor", brightnessFactor);
			RenderUtils.renderTexture(getTexture(currentAnimation), x, y);
			brightnessShader.stopProgram();
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
