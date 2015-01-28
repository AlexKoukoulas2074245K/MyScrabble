package com.myscrabble.uicomponents;

import java.awt.Rectangle;
import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

import com.myscrabble.entities.GameObject;
import com.myscrabble.entities.Player;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.MouseManager;
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
	
	/**
	 * Default button input handling.
	 * Responds to mouse movement
	 * and presses.
	 */
	public void handleInput()
	{
		int mouseX = MouseManager.getX();
		int mouseY = MouseManager.getY();
		
		if(getRect().contains(mouseX, mouseY))
		{
			highlighted = true;
			
			if(MouseManager.isButtonPressed(MouseManager.LEFT_BUTTON))
			{
				pressed = true;
			}
			else if(MouseManager.isButtonReleased(MouseManager.LEFT_BUTTON) && pressed)
			{
				executeFunction();
				pressed = false;
				highlighted = false;
			}
		}
		else
		{
			highlighted = false;
			
			if(MouseManager.isButtonReleased(MouseManager.LEFT_BUTTON))
			{
				pressed = false;
			}
		}
	}
	
	/* Standard method to be implemented by all children */	
	public abstract void executeFunction();
	
	public void update(Player playerRef)
	{
		
	}
	
	public void render()
	{
	    if(highlighted && !pressed)
	    {
	        GameObject.highlightProgram.useProgram();
	    }
		RenderUtils.renderTexture(textures.get(currentTexture), x, y);
		
		GameObject.highlightProgram.stopProgram();
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
