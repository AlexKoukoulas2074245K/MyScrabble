package com.myscrabble.entities;

import static com.myscrabble.managers.ResourceManager.STD_TEX_EXT;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import org.newdawn.slick.opengl.Texture;

import com.myscrabble.managers.GameStateManager;
import com.myscrabble.util.RenderUtils;
import com.myscrabble.util.ScrabbleUtils;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * This entity represents the cloth bag used to
 * draw letter tiles from. When a draw request
 * is done a LetterTile is returned.
 */

public class LetterBag extends GameObject
{
	/* Standard english alphabet letters */
	private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	/* Texture Paths */
	private static final String NORM_TEX_PATH = "/misc/scrabbleBag" + STD_TEX_EXT;
	private static final String SEL_TEX_PATH  = "/misc/scrabbleBagSel" + STD_TEX_EXT;
	
	/* Textue Flags */
	private static final int NORMAL_TEX  = 0;
	private static final int SEL_TEX     = 1;
	
	/* Positional Constants */
	private static final float X_OFFSET = 604;
	private static final float Y_OFFSET = 500;
	public static final float LETTER_X_OFFSET = X_OFFSET + 40;
	public static final float LETTER_Y_OFFSET = 592;
	
	/* Drawing Animation Variables */
	public static final float MAX_X_SPEED = -5f;
    public static final float X_SPEED_INCS = 0.8f;
    public static final float JUMP_START  = -5f;
    public static final float GRAVITY = 0.203f;
	private static final float CONSTANT_INDEX_MODIFIER = 0.8f;
	private static final float INCREMENTAL_INDEX_MODIFIER = 0.02f; 
	
	
	/* A collection of letters that the players can draw from */
	private ArrayList<Character> letters;
	
	private boolean highlighted;
	
	/**
	 * 
	 * @param tileRackIndex the goal index for this drawing animation
	 * @return the appropriate horizontal speed needed for a tile
	 * to reach its goal index smoothly
	 * <br>
	 * Explanation of how the result is found:
	 * Taken the constant maximum x speed (-3.0f moving west),
	 * we decrement it by a linear amount (more substantial
	 * subtraction the smaller the index is) and then
	 * we furthermore decrement it by an incremental amount based
	 * on the index(again more substantial amount subtracted the
	 * smaller the goal index is).
	 */
	public static final float getAppropriateXSpeed(int tileRackIndex)
	{
	    return (float)(MAX_X_SPEED - (CONSTANT_INDEX_MODIFIER * (TileRack.MAX_NO_TILES - tileRackIndex)) 
	                               - INCREMENTAL_INDEX_MODIFIER * (TileRack.MAX_NO_TILES - tileRackIndex));
	}
	
	public LetterBag(GameStateManager gsm)
	{
		super(gsm);
		
		fillBag();
		shuffleBag();
		loadTextures();
		
		highlighted = false;
		
		x = X_OFFSET;
		y = Y_OFFSET;
	}
	
	public void update()
	{

	}
	
	@Override
	public void render()
	{
		if(highlighted)
		{
			RenderUtils.renderTexture(getTexture(SEL_TEX), x, y);
		}
		else
		{
			RenderUtils.renderTexture(getTexture(NORMAL_TEX), x, y);
		}
		
		//RenderUtils.renderTexture(tex, texX, texY);
	}
	
	public LetterTile drawLetter(Player playerRef, int index)
	{
		/* selection of random index */
		int randIndex = new Random().nextInt(letters.size());
		
		/* accumulation of the random letter */
		char chosenLetter = letters.get(randIndex);
		
		/* pop the random letter from bag */
		letters.remove(randIndex);
		
		int letterPoints = ScrabbleUtils.getValueOf(chosenLetter);
		
		return new LetterTile(gsm, playerRef, chosenLetter, letterPoints, true, index);
	}
	
	private void fillBag()
	{
		letters = new ArrayList<Character>();
		
		for(int i = 0; i < alphabet.length(); i++)
		{
			for(int n = 0; n < ScrabbleUtils.getNumberOf(alphabet.charAt(i)); n++)
			{
				letters.add(alphabet.charAt(i));
			}
		}
	}
	
	private void shuffleBag()
	{
		Collections.shuffle(letters);
	}
	
	private void loadTextures()
	{
		addTexture(NORMAL_TEX, NORM_TEX_PATH);
		addTexture(SEL_TEX, SEL_TEX_PATH);
	}
	
	public void highlight(boolean highlighted)
	{
		this.highlighted = highlighted;
	}
	
	/* Getters / Setters */
	public boolean getHighlighted()
	{
		return highlighted;
	}
	
	public Rectangle getRect()
	{
		return new Rectangle((int)x, (int)y, 
							 getTexture(NORMAL_TEX).getTextureWidth(), 
							 getTexture(NORMAL_TEX).getTextureHeight());
	}
}
