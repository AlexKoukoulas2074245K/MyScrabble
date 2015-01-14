package com.myscrabble.entities;

import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.opengl.Texture;

import com.myscrabble.managers.GameStateManager;
import com.myscrabble.util.Animation;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * An abstraction for all gameObjects. Contains common fields
 * and methods that gameObjects have. Getters and Setters for these
 * fields are present as well.
 */
public abstract class GameObject 
{
	/* An instance of gsm is needed for I/O */
	protected GameStateManager gsm;
	
	/* An animation is provided for mult. frames */
	protected ArrayList<Animation> animations;
	
	/* Apart from animation an ArrayList of Textures is also available.
	 * Private HashMap to limit the control of the HashMap by the
	 * subclass. Instead two helper methods are provided below 
	 */
	private HashMap<Integer, Texture> textures;
	
	/* Protected positional and velocity fields */
	protected float x;
	protected float y;
	protected float x0;
	protected float y0;
	protected float vx;
	protected float vy;
	
	/* Centered Rendering */
	protected boolean centerRendering;
	
	protected GameObject(GameStateManager gsm)
	{
		this.gsm = gsm;
		textures = new HashMap<Integer, Texture>();
		animations = new ArrayList<Animation>();
	}
	
	protected void addAnimation(String dirPath, int aniDelay)
	{
		animations.add(gsm.getRes().loadAnimation(dirPath, aniDelay));
	}
	
	protected void addTexture(int index, String texturePath)
	{
		textures.put(index, gsm.getRes().loadTexture(texturePath));
	}
	
	protected Texture getTexture(int index)
	{
		return textures.get(index);
	}
	
	/* Common update and render methods */
	public void update(){}
	public void render(){}
	
	protected float approachPos(float goal, float current, float delta)
	{
		if(current + delta < goal)
		{
			return current + delta;
		}
		
		return goal;
	}
	
	protected float approachNeg(float goal, float current, float delta)
	{	
		if(current - delta > -goal)
		{
			return current - delta;
		}
		
		return goal;
	}
	
	public float getX() 
	{
		return x;
	}
	
	public float getY() 
	{
		return y;
	}
	
	public float getVx() 
	{
		return vx;
	}
	
	public float getVy() 
	{
		return vy;
	}

	public float getX0()
	{
		return x0;
	}
	
	public float getY0()
	{
		return y0;
	}
	
	public void setX(float x) 
	{
		this.x = x;
	}
	
	public void setY(float y) 
	{
		this.y = y;
	}

	public void setVx(float vx) 
	{
		this.vx = vx;
	}
	
	
	public void setVy(float vy) 
	{
		this.vy = vy;
	}
	
	
	public void setX0(float x0)
	{
		this.x0 = x0;
	}
	
	
	
	public void setY0(float y0)
	{
		this.y0 = y0;
	}
}
