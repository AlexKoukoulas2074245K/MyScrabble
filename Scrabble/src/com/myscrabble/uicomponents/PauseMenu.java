package com.myscrabble.uicomponents;

import java.awt.Rectangle;

import org.newdawn.slick.opengl.Texture;

import com.myscrabble.main.Main;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.KeyboardManager;
import com.myscrabble.managers.MouseManager;
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
	public enum PauseState
	{
		MAIN,
		OPTIONS,
		CONFIRMATION;
	}
	
    public enum PauseOption
    {
    	RESUME(0, "resume", PauseState.MAIN, true),
    	OPTIONS(1, "options", PauseState.MAIN, true),
    	MAIN_MENU(2, "main_menu", PauseState.MAIN, true),
    	YES(3, "yes", PauseState.CONFIRMATION, true),
    	NO(4, "no", PauseState.CONFIRMATION, true),
    	EXIT_WARN(5, "exit_warn", PauseState.CONFIRMATION, false),
    	SOUND(6, "sound", PauseState.OPTIONS, true),
    	BACK(7, "back", PauseState.OPTIONS, true);
    	
    	public PauseState optionState;
    	public boolean hasBounced;
    	public boolean highlighted;
    	public boolean available;
    	public int value;
    	public String name;
    	
    	private PauseOption(int value, String name, PauseState optionState, boolean available)
    	{
    		this.value = value;
    		this.name  = name;
    		this.optionState = optionState;
    		this.available = available;
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
    private static final float GOAL_Y = Main.getCenterDimensions()[1];
    private static final float SEC_GOAL_Y = GOAL_Y - 128;
    private static final float START_X_MARGIN = 48;
    private static final float START_Y_MARGIN = START_X_MARGIN;
    private static final float X_OFFSET = Main.getCenterDimensions()[0] - 128;
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
    
    /* Pause Menu state */
    private PauseState state;
    
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
        state = PauseState.MAIN;
    }
    
    private void loadTextures()
    {
    	textures = new Texture[PauseOption.values().length];
    	
    	for(PauseOption option : PauseOption.values())
    	{
    		if(option.name.equals("sound"))
    		{
    			String postFix = gsm.getSoundManager().isActive() ? "_on" : "_off";
    			textures[option.value] = gsm.getRes().loadTexture(TEX_DIR + option.name + postFix);
    		}
    		else
    		{
    			textures[option.value] = gsm.getRes().loadTexture(TEX_DIR + option.name);
    		}
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
    		float x = 0.0f;
    		float y = 0.0f;
    		
    		if(option.optionState == PauseState.MAIN)
    		{
    			x = - textures[option.value].getTextureWidth() / 2f - option.value * START_X_MARGIN;
    			y = Y_OFFSET + option.value * Y_MARGIN;
    		}
    		else if(option.optionState == PauseState.CONFIRMATION)
    		{
    			if(option.name.equals("yes"))
    			{
    				x = X_OFFSET;
    				y = - textures[option.value].getTextureHeight() / 2f;
    			}
    			else if(option.name.equals("no"))
    			{
    				x = X_OFFSET + textures[option.value].getTextureWidth();
    				y = - textures[option.value].getTextureHeight() / 2f - START_Y_MARGIN;
    			}
    			else
    			{
    				x = Main.getCenterDimensions()[0] - textures[option.value].getTextureWidth() / 4f;
    				y = - textures[option.value].getTextureHeight() / 2f - 3 * START_Y_MARGIN;
    			}
    		}
    		else if(option.optionState == PauseState.OPTIONS)
    		{
    			if(option.name.equals("sound"))
    			{
    				x = Main.getNormalDimensions()[0];
    				y = Y_OFFSET;
    			}
    			else if(option.name.equals("back"))
    			{
    				x = Main.getNormalDimensions()[0] + START_X_MARGIN;
    				y = Y_OFFSET + Y_MARGIN;
    			}
    		}
    		
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
        	if(state == PauseState.MAIN)
        	{
        		isActive = false;
        	}
        	else if(state == PauseState.CONFIRMATION || state == PauseState.OPTIONS)
        	{
        		state = PauseState.MAIN;
        		resetPosAndVel();
        	}
        }
        
        for(PauseOption option : PauseOption.values())
        {
        	if(option.optionState != state)
        	{
        		continue;
        	}
        	
        	option.highlighted = mouseOnOption(option) && option.available;
        	
        	if(MouseManager.isButtonPressed(MouseManager.LEFT_BUTTON) && option.highlighted)
        	{
        		executeFunction(option);
        		gsm.getSoundManager().playClip(SFX_NAME);
        	}
        }
    }
    
    private void executeFunction(PauseOption option)
    {
    	PauseState state0 = state;
    	
    	switch(option.name)
    	{
    	case "resume":
    		isActive = false;
    		break;
    	
    	case "options":
    		state = PauseState.OPTIONS;
    		break;
    	
    	case "main_menu":
    		state = PauseState.CONFIRMATION;
    		break;
    	
    	case "yes":
    		mainMenuRequest = true;
    		break;
    	
    	case "no":
    		state = PauseState.MAIN;
    		break;
    	
    	case "back":
    		state = PauseState.MAIN;
    		break;
    	
    	case "sound":
    		if(gsm.getSoundManager().isActive())
    		{
    			gsm.getCurrentState().mute();
    			textures[PauseOption.SOUND.value] = gsm.getRes().loadTexture(TEX_DIR + "sound_off");
    		}
    		else
    		{
    			gsm.getCurrentState().enableSounds();
    			textures[PauseOption.SOUND.value] = gsm.getRes().loadTexture(TEX_DIR + "sound_on");
    		}
    		break;
    	
    	default:
    		break;
    	}
    	
    	if(state != state0)
    	{
    		resetPosAndVel();
    	}
    }
    
    /* Letter "Physics" simulation */
    public void update()
    {
    	for(PauseOption option : PauseOption.values())
    	{
    		if(option.optionState != state)
    		{
    			continue;
    		}
    		
    		if(state == PauseState.MAIN)
    		{
    			updateMain(option);
    		}
    		else if(state == PauseState.CONFIRMATION)
    		{
    			updateConf(option);
    		}
    		else if(state == PauseState.OPTIONS)
    		{
    			updateOpts(option);
    		}
    	}
    }
    
    private void updateMain(PauseOption option)
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
    
    private void updateConf(PauseOption option)
    {
    	optionVelocities[option.value][1] += VEL_INCS;
		
		if(optionVelocities[option.value][1] > MAX_VEL)
		{
			optionVelocities[option.value][1] = MAX_VEL;
		}
		
		optionPositions[option.value][0] += optionVelocities[option.value][0];
		optionPositions[option.value][1] += optionVelocities[option.value][1];
		
		float goalY = 0.0f;
		if(option.name.equals("yes") || option.name.equals("no"))
		{
			goalY = GOAL_Y;
		}
		else
		{
			goalY = SEC_GOAL_Y;
		}
		
		if(optionPositions[option.value][1] >= goalY)
		{
			if(option.hasBounced)
			{
				optionPositions[option.value][1] = goalY;
			}
			else
			{
				optionVelocities[option.value][1] = BOUNCE;
				option.hasBounced = true;
			}
		}
    }
    
    public void updateOpts(PauseOption option)
    {
    	optionVelocities[option.value][0] -= VEL_INCS;
		
		if(optionVelocities[option.value][0] < -MAX_VEL)
		{
			optionVelocities[option.value][0] = -MAX_VEL;
		}
		
		optionPositions[option.value][0] += optionVelocities[option.value][0];
		optionPositions[option.value][1] += optionVelocities[option.value][1];
		
		if(optionPositions[option.value][0] <= GOAL_X)
		{
			if(option.hasBounced)
			{
				optionPositions[option.value][0] = GOAL_X;
			}
			else
			{
				optionVelocities[option.value][0] = -BOUNCE;
				option.hasBounced = true;
			}
		}
    }
    
    public void render()
    {
    	for(PauseOption option : PauseOption.values())
    	{
    		if(option.optionState != state)
    		{
    			continue;
    		}
    		
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
