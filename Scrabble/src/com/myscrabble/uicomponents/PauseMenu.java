package com.myscrabble.uicomponents;

import java.awt.Rectangle;

import org.newdawn.slick.opengl.Texture;

import com.myscrabble.main.Main;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.KeyboardManager;
import com.myscrabble.managers.MouseManager;
import com.myscrabble.managers.ResourceManager;
import com.myscrabble.managers.SoundManager;
import com.myscrabble.managers.SoundManager.SoundType;
import com.myscrabble.rendering.Shader;
import com.myscrabble.rendering.Shader.ShaderType;
import com.myscrabble.util.RenderUtils;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * An class representing a pause menu.
 * Options are accessed from here.
 */
public class PauseMenu 
{
    public enum PauseOption
    {
    	RESUME(0, "resume"),
    	OPTIONS(1, "options"),
    	MAIN_MENU(2, "main_menu");
    	
    	public boolean hasBounced;
    	public boolean highlighted;
    	public int value;
    	public String name;
    	
    	private PauseOption(int value, String name)
    	{
    		this.value = value;
    		this.name  = name;
    		this.hasBounced = false;
    		this.highlighted = false;
    	}
    }
    
    /* Sound effect name */
    private static final String SFX_NAME = "buttonClick";
    
    /* Default path for all the textures needed */
    private static final String TEX_DIR = "/misc/pauseMenu/";
    
    /* Option textures all are center rendered */ 
    private static final boolean CENTER_REND = true;
    
    /* Pause menu is not affected by play's shading program */
    private static final float DARKNESS_FACTOR = 1.0f;
    
    /* Positional Constants */
    private static final float GOAL_X = Main.getCenterDimensions()[0] - 64;
    private static final float START_X_MARGIN = 48;
    private static final float Y_OFFSET = 200;
    private static final float Y_MARGIN = 128;
    
    /* Velocity Constants */
    private static final float VEL_INCS = 0.5f;
    private static final float MAX_VEL  = 12.0f;
    private static final float BOUNCE   = -4.0f;
    
    /* GameStateManager reference */
    private GameStateManager gsm;
    
    /* Option textures */
    private Texture[] textures;
    
    /* Option Highlighting Shader */
    private Shader highlightShader;
    
    /* Positional and velocity data about the options */
    private float[][] optionPositions;
    private float[][] optionVelocities;
    
    /* is the pause menu active? */
    private boolean isActive;
    
    /* Whether a main menu request was made */
    private boolean mainMenuRequest;
    
    public PauseMenu(GameStateManager gsm)
    {
    	this.gsm = gsm;
    	loadTextures();
    	loadSoundEffect();
    	resetPosAndVel();
        isActive = false;
        mainMenuRequest = false;
        highlightShader = new Shader(ShaderType.HIGHLIGHTING);
    }
    
    private void loadTextures()
    {
    	textures = new Texture[PauseOption.values().length];
    	
    	for(PauseOption option : PauseOption.values())
    	{
    		textures[option.value] = gsm.getRes().loadTexture(TEX_DIR + option.name);
    	}
    }
    
    private void loadSoundEffect()
    {
    	gsm.getSoundManager().loadClip(SFX_NAME, SoundType.SOUND_EFFECT);
    }
    
    private void resetPosAndVel()
    {
    	optionPositions  = new float[PauseOption.values().length][2];
    	optionVelocities = new float[PauseOption.values().length][2];
    	
    	for(PauseOption option : PauseOption.values())
    	{
    		float x = - textures[option.value].getTextureWidth() / 2f - option.value * START_X_MARGIN;
    		float y = Y_OFFSET + option.value * Y_MARGIN;
    		
    		optionPositions[option.value] = new float[]{x, y};
    		optionVelocities[option.value] = new float[2];
    		option.hasBounced = false;
    		option.highlighted = false;
    	}
    }
    
    public void handleInput()
    {
        if(KeyboardManager.isKeyPressed(KeyboardManager.K_ESCAPE))
        {
            isActive = false;
        }
        
        for(PauseOption option : PauseOption.values())
        {
        	option.highlighted = mouseOnOption(option);
        	
        	if(MouseManager.isButtonPressed(MouseManager.LEFT_BUTTON) && option.highlighted)
        	{
        		executeFunction(option);
        		gsm.getSoundManager().playClip(SFX_NAME);
        	}
        }
    }
    
    private void executeFunction(PauseOption option)
    {
    	if(option.name.equals("resume"))
    	{
    		isActive = false;
    	}
    	else if(option.name.equals("main_menu"))
    	{
    		mainMenuRequest = true;
    	}
    }
    
    /* Letter "Physics" simulation */
    public void update()
    {
    	for(PauseOption option : PauseOption.values())
    	{	
    		optionVelocities[option.value][0] += VEL_INCS;
    		
    		if(optionVelocities[option.value][0] > MAX_VEL)
    		{
    			optionVelocities[option.value][0] = MAX_VEL;
    		}
    		
    		optionPositions[option.value][0] += optionVelocities[option.value][0];
    		optionPositions[option.value][1] += optionVelocities[option.value][1];
    		
    		if(optionPositions[option.value][0] >= GOAL_X)
    		{
    			if(option.hasBounced)
    			{
    				optionPositions[option.value][0] = GOAL_X;
    			}
    			else
    			{
    				optionVelocities[option.value][0] = BOUNCE;
    				option.hasBounced = true;
    			}
    		}
    	}
    }
    
    public void render()
    {
    	for(PauseOption option : PauseOption.values())
    	{
    		if(option.highlighted)
    		{
    			highlightShader.useProgram();
    			highlightShader.setUniformb("fullWhite", Shader.TRUE);
    			highlightShader.setUniformb("highlighted", Shader.TRUE);
    			highlightShader.setUniform3f("darknessFactor", new float[]{
    																DARKNESS_FACTOR,
    																DARKNESS_FACTOR,
    																DARKNESS_FACTOR});
    		}
    		
    		RenderUtils.renderTexture(textures[option.value], 
    								  optionPositions[option.value][0], 
    								  optionPositions[option.value][1], 
    								  textures[option.value].getTextureWidth(),
    								  textures[option.value].getTextureHeight(),
    								  CENTER_REND);
    		
    		highlightShader.stopProgram();
    	}
    }
    
    private boolean mouseOnOption(PauseOption option)
    {
    	Rectangle optionRect = new Rectangle((int)optionPositions[option.value][0] - 
    												textures[option.value].getTextureWidth() / 2, 
    									     (int)optionPositions[option.value][1] - 
    									     		textures[option.value].getTextureHeight() / 2,
    									     textures[option.value].getTextureWidth(),
    									     textures[option.value].getTextureHeight());
    	
    	return optionRect.contains(MouseManager.getX(), MouseManager.getY());
    }
    
    public boolean isActive()
    {
        return isActive;
    }
    
    public boolean getMainMenuRequest()
    {
    	return mainMenuRequest;
    }
    
    public void setActive(boolean isActive)
    {
        this.isActive = isActive;
        resetPosAndVel();
    }
}
