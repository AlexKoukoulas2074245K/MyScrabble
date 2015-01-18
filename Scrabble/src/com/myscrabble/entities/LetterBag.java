package com.myscrabble.entities;

import static com.myscrabble.managers.ResourceManager.STD_TEX_EXT;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
	private static final int X_OFFSET = 604;
	private static final int Y_OFFSET = 500;
	
	/* A collection of letters that the players can draw from */
	private ArrayList<Character> letters;
	
	private boolean highlighted;
	
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
	}
	
	public char drawLetter()
	{
		/* selection of random index */
		int randIndex = new Random().nextInt(letters.size());
		
		/* accumulation of the random letter */
		char chosenLetter = letters.get(randIndex);
		
		/* pop the random letter from bag */
		letters.remove(randIndex);
		
		return chosenLetter;
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
