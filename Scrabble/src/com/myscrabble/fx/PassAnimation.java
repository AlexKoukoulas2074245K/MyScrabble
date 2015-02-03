package com.myscrabble.fx;

import java.util.HashSet;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import com.myscrabble.main.Main;
import com.myscrabble.managers.ResourceManager;
import com.myscrabble.util.RenderUtils;
import com.myscrabble.util.ScrabbleUtils;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A decorating animation when one
 * of the players passes
 */
public class PassAnimation implements Effect
{
	private static final Color LETTER_COL  = Color.black;
	private static final String REG_FONT_NAME = "font_bold";
	private static final int FONT_SIZE = 30;
	private static final int LETTER_MARGIN = 30;
	
	private static final float X_OFFSET = 12;
	private static final float MAX_VEL = 2.23f;
	private static final float GRAVITY = 1.0f;
	private static final float SCALE = 1.5f;
		
	private TrueTypeFont font;
	private String word;
	private HashSet<LetterBlock> letterPositions;
	private LetterBlock lastBlock;
	private boolean finished;
	
	public PassAnimation(ResourceManager rm, String playerName)
	{
		finished = false;
		font = rm.loadFont(REG_FONT_NAME, FONT_SIZE, true);
		assembleString(playerName);
		setLetterPositions();
	}
	
	private void assembleString(String playerName)
	{
		word = ScrabbleUtils.reverse(playerName + " passed");
	}
	
	private void setLetterPositions()
	{
		letterPositions = new HashSet<LetterBlock>();
		System.out.println(word);
		for(int i = word.length() - 1; i >= 0; i--)
		{
			if(i == 0)
			{
				lastBlock = new LetterBlock(word.charAt(i),
								new float[]{X_OFFSET, - ((word.length() - i) * LETTER_MARGIN)});
				
				letterPositions.add(lastBlock);
			}
			else
			{
				letterPositions.add(new LetterBlock(word.charAt(i), 
									new float[]{X_OFFSET, - ((word.length() - i) * LETTER_MARGIN)}));
			}
		}
	}
	
	@Override
	public void update()
	{
		for(LetterBlock block : letterPositions)
		{
			block.vel[1] += GRAVITY;
			
			if(block.vel[1] > MAX_VEL)
			{
			    block.vel[1] = MAX_VEL;
			}
			
			block.pos[1] += block.vel[1];

			if(lastBlock.pos[1] > Main.getNormalDimensions()[1])
			{
				finished = true;
			}
		}
	}
	
	@Override
	public void render()
	{
		RenderUtils.pushMatrix();
		GL11.glScalef(SCALE, SCALE, SCALE);
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
		for(LetterBlock block : letterPositions)
		{
			font.drawString(block.pos[0], block.pos[1], 
						    String.valueOf(block.character), LETTER_COL);
		}
		GL11.glPopAttrib();
		RenderUtils.popMatrix();
	}
	
	@Override
	public boolean isFinished()
	{
		return finished;
	}
}
