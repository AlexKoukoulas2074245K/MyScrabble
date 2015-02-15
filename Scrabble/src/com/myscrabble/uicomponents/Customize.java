package com.myscrabble.uicomponents;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;

import com.myscrabble.entities.Board;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.KeyboardManager;
import com.myscrabble.managers.MouseManager;
import com.myscrabble.managers.ProfileManager;
import com.myscrabble.user.UserProfile;
import com.myscrabble.util.RenderUtils;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * Customize lets the Player change backgrounds,
 * tile styles etc..
 */
public class Customize
{
	private static final String SEL_TEX_DIR = "/menu/bgSelector";
	private static final String FONT_NAME   = "font_bold";
	private static final int FONT_SIZE      = 32;
	private static final boolean FONT_ALIAS = true;
	private static final int BG_MARGIN      = 95;
	
	private static final float[] BG_UPPER_POS = new float[]{150, 265};
    private static final float[] BG_LOWER_POS = new float[]{150, 360};
    private static final float[] INFO_POS     = new float[]{145, 220};
    
	private static final Rectangle GO_BACK_RECT = new Rectangle(400, 490, 100, 32);
	
    private static final int BG_SIZE       = 80;
	private static final int SELECTOR_SIZE = 84;
	
    private GameStateManager gsm;
	private UserProfile user;
	private boolean isFinished;
	private boolean backHi;
	
	private ArrayList<Texture> bgTextures;
	private HashMap<Texture, Rectangle> bgRects;
	
	private Texture bgSelector;
	
	private TrueTypeFont font;
	
	public Customize(GameStateManager gsm, UserProfile user)
	{
		this.gsm  = gsm;
		this.user = user;
		font = gsm.getRes().loadFont(FONT_NAME, FONT_SIZE, FONT_ALIAS);
		
		loadTextures();
		createRects();
	}
	
	private void loadTextures()
	{
		bgTextures = new Board(gsm, 0).getBackgroundTextures();
		bgSelector = gsm.getRes().loadTexture(SEL_TEX_DIR);
	}
	
	private void createRects()
	{
		bgRects = new HashMap<Texture, Rectangle>();
		
		for(int i = 0; i < bgTextures.size(); i++)
		{
			float x = i < bgTextures.size() / 2 ? BG_UPPER_POS[0] + i * BG_MARGIN :
				    							  BG_UPPER_POS[0] + (i - 5) * BG_MARGIN;
			
			float y = i < bgTextures.size() / 2 ? BG_UPPER_POS[1] : BG_LOWER_POS[1];
			
			Rectangle textureRect = new Rectangle((int)x, (int)y, BG_SIZE, BG_SIZE);
			bgRects.put(bgTextures.get(i), textureRect);
		}
	}
	
	public void handleInput()
	{
		for(Texture tex : bgTextures)
		{
			if(bgRects.get(tex).contains(MouseManager.getX(), MouseManager.getY()))
			{
				if(MouseManager.isButtonPressed(MouseManager.LEFT_BUTTON))
				{
					user.setLastBackgroundUsed(bgTextures.indexOf(tex));
				}
			}
		}
		
		backHi = GO_BACK_RECT.contains(MouseManager.getX(), MouseManager.getY());
		
		if(backHi && MouseManager.isButtonPressed(MouseManager.LEFT_BUTTON))
		{
			isFinished = true;
		}
	}
	
	public void update()
	{
		
	}
	
	public void render()
	{
		for(int i = 0; i < bgTextures.size(); i++)
		{
			Rectangle bgRect = bgRects.get(bgTextures.get(i));
			
			RenderUtils.renderTexture(bgTextures.get(i),
					                  bgRect.x,
					                  bgRect.y,
					                  bgRect.width,
					                  bgRect.height);
			
			if(user.getLastBackgroundUsed() == i)
			{
				RenderUtils.renderTexture(bgSelector,
										 bgRect.x - 2,
										 bgRect.y - 2,
										 SELECTOR_SIZE,
										 SELECTOR_SIZE);
			}
		}
		
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
		
		font.drawString(INFO_POS[0],
						INFO_POS[1],
						"Select game background!",
						ProfileManager.DIM_FONT_COLOR);
		
		org.newdawn.slick.Color backCol = backHi ? ProfileManager.WHITE_FONT_COLOR :
												   ProfileManager.DIM_FONT_COLOR; 
		font.drawString(GO_BACK_RECT.x,
						GO_BACK_RECT.y,
						"Go Back",
						backCol);
		
		GL11.glPopAttrib();
	}
	
	public boolean isFinished()
	{
		return isFinished;
	}
}
