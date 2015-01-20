package com.myscrabble.uicomponents;

import java.awt.Rectangle;
import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

import com.myscrabble.entities.Player;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.util.RenderUtils;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * An class representing 
 * an interactive Button.
 */
public abstract class Button
{
	public static final int INVALID = 0;
	public static final int VALID = 1;
	
	/* Default directory for button textures */
	public static final String BUTTON_TEX_DIR = "/misc/buttons/";
	
	/* Instance of gsm */
	protected GameStateManager gsm;
	
	/* Button name */
	protected String name;
	/* All the button textures */
	protected ArrayList<Texture> textures;
	
	/* Positional variables */
	protected float x;
	protected float y;
	
	/* Button Variables */
	protected boolean highlighted;
	protected boolean pressed;
	
	protected int status;
	protected int currentTexture;
	
	public Button(String name, GameStateManager gsm)
	{
		this.name = name;
		this.gsm = gsm;
		
		loadTextures();
		currentTexture = 0;
	}
	
	private void loadTextures()
	{
		textures = gsm.getRes().getAllTextures(BUTTON_TEX_DIR + name);
	}
	
	/* Standard methods to be implemented by all children */
	public abstract void handleInput();
	public abstract void executeFunction();
	
	public void update(Player playerRef)
	{
		
	}
	
	public void render()
	{
		RenderUtils.renderTexture(textures.get(currentTexture), x, y);
	}
	
	public Rectangle getRect()
	{
		return new Rectangle((int)x, (int)y, 
							 textures.get(0).getTextureWidth(),
							 textures.get(0).getTextureHeight());
	}
	
	public boolean getHighlighted()
	{
		return highlighted;
	}
	
	public boolean getPressed()
	{
		return pressed;
	}
	
	public int getStatus()
	{
		return status;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
	}
}
