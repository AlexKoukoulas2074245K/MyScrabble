package com.myscrabble.states;

import org.newdawn.slick.opengl.Texture;

import com.myscrabble.main.Main;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.ProfileManager;
import com.myscrabble.rendering.Shader;
import com.myscrabble.rendering.Shader.ShaderType;
import com.myscrabble.uicomponents.Customize;
import com.myscrabble.uicomponents.MainOption;
import com.myscrabble.uicomponents.MainOption.OptionName;
import com.myscrabble.user.UserProfile;
import com.myscrabble.util.RenderUtils;

/**
 * @author Alex Koukoulas
 * Class Description:
 * Main menu gamestate
 */

public class Menu extends GameState
{
	enum MenuState
	{
		PROFILE_CREATION,
		PROFILE_SELECTION,
		MAIN_MENU,
		PLAY,
		CUSTOMIZE,
		EXIT;
	}
	
	enum TransState
	{
		IDLE,
		DARKEN,
		BRIGHTEN;
	}
	
	private static final String TEX_DIR = "/menu/";
	private static final String BG_TEX_NAME = "background.png";
	private static final String TITLE_TEX_NAME = "title.png";
	
	private static final float[] TITLE_POS = new float[]{123.0f, 120.0f,};
	
	private static final float MAX_BRIGHT_FACT = 2.0f;
	private static final float MIN_BRIGHT_FACT = 0.5f;
	private static final float BRIGHT_INTERVS  = 0.02f;
	private static final float TRANS_INTERVS   = 0.05f;
	private static final float MAX_ALPHA_FACT  = 1.5f;
	private static final float MIN_ALPHA_FACT  = 0.0f;
	
	private static boolean firstInit = true;
	
	private MenuState state;
	private MenuState nextState;
	
	private ProfileManager profileManager;
    private UserProfile currentUserProfile;
    private Customize custom;
    private Texture backgroundTex;
    private Texture titleTex;
    private MainOption[] options;
    private Shader titleShader;
    
    private float brightnessFactor;
    private boolean brightRaiseFlag;
    
	private Shader transShader;
	private TransState transState;
	private float alphaFactor;
	
	public Menu(GameStateManager gsm, UserProfile currentUser)
	{
		super(gsm);
		this.currentUserProfile = currentUser;
		
		profileManager = new ProfileManager(gsm);

		titleShader = new Shader(ShaderType.AUTO_BRIGHTNESS);
		
		loadTextures();
		createOptions();
		decideState();
		
		brightnessFactor = MIN_BRIGHT_FACT;
		brightRaiseFlag  = true;
		
		alphaFactor = 1.0f;
		transState  = TransState.BRIGHTEN;
		transShader = new Shader(ShaderType.TRANSITION);
	}
	
	private void loadTextures()
	{
		backgroundTex = gsm.getRes().loadTexture(TEX_DIR + BG_TEX_NAME);
		titleTex = gsm.getRes().loadTexture(TEX_DIR + TITLE_TEX_NAME);
	}
	
	private void createOptions()
	{
		options = new MainOption[3];
		options[0] = new MainOption(gsm, OptionName.PLAY);
		options[1] = new MainOption(gsm, OptionName.CUSTOMIZE);
		options[2] = new MainOption(gsm, OptionName.EXIT);
	}
	
	private void decideState()
	{
		if(firstInit)
		{
			firstInit = false;
			state =  profileManager.profilesFound() ? MenuState.PROFILE_SELECTION :
				 								      MenuState.PROFILE_CREATION;
		}
		else
		{
			state = MenuState.MAIN_MENU;
		}
		
		nextState = state;
	}
	
	@Override
	public void handleInput() 
	{
		if(nextState != null)
		{
			return;
		}
		
		switch(state)
		{
		case PROFILE_CREATION:
			profileManager.handleInputCreation();
			break;
		
		case PROFILE_SELECTION :
			profileManager.handleInputSelection();
			break;
		
		case MAIN_MENU:
			handleInputOptions();
			break;
			
		case PLAY:
			break;
		
		case CUSTOMIZE:
			custom.handleInput();
			break;
			
		case EXIT:
			break;
		}
	}
	
	private void handleInputOptions()
	{
		for(MainOption option : options)
		{
			option.handleInput();
		}
		
		if(options[0].isSelected())
		{
			setState(MenuState.PLAY);
		}
		else if(options[1].isSelected())
		{
			setState(MenuState.CUSTOMIZE);
		}
		else if(options[2].isSelected())
		{
			setState(MenuState.EXIT);
		}
	}
	
	@Override
	public void update() 
	{
		if(nextState != null)
		{
			transState();
			return;
		}
		
		updateBrightnessFactor();
		
		if(state != MenuState.MAIN_MENU)
		{
			updateState();
		}
	}
	
	private void transState()
	{
		if(transState == TransState.DARKEN)
		{	
			if(alphaFactor < MAX_ALPHA_FACT)
			{
				alphaFactor += TRANS_INTERVS;
			}
			else
			{
				alphaFactor = MAX_ALPHA_FACT;
				transState = TransState.BRIGHTEN;
				state = nextState;
				
				if(state == MenuState.PLAY)
				{
					finished = true;
				}
				else if(state == MenuState.EXIT)
				{
					Main.endGame();
				}
				else if(state == MenuState.CUSTOMIZE)
				{
					custom = new Customize(gsm, currentUserProfile);
				}
				else if(state == MenuState.MAIN_MENU)
				{
					custom = null;
				}
			}
		}
		else if(transState == TransState.BRIGHTEN)
		{
			if(alphaFactor > MIN_ALPHA_FACT)
			{
				alphaFactor -= TRANS_INTERVS;
			}
			else
			{
				alphaFactor = MIN_ALPHA_FACT;
				transState = TransState.IDLE;
				nextState = null;
			}
		}
	}
	
	private void updateState()
	{
		switch(state)
		{
		case PROFILE_CREATION:
			if(profileManager.getFinishedCreation())
			{
				profileManager = new ProfileManager(gsm);
				setState(MenuState.PROFILE_SELECTION);
			}
			break;
		
		case PROFILE_SELECTION:
			profileManager.updateSelection();
			
			if(profileManager.getSelectedProfile() != null)
			{
				currentUserProfile = profileManager.getSelectedProfile();
				setState(MenuState.MAIN_MENU);
			}
			else if(profileManager.getCreateNewRequest())
			{
				setState(MenuState.PROFILE_CREATION);
			}
			break;
		
		case MAIN_MENU:
			break;
			
		case PLAY:
			break;
		
		case CUSTOMIZE:
			custom.update();
			if(custom.isFinished())
			{
				setState(MenuState.MAIN_MENU);
			}
			break;
			
		case EXIT:
			break;
		}
	}
	
	private void updateBrightnessFactor()
	{
		if (brightRaiseFlag)
		{
			brightnessFactor += BRIGHT_INTERVS;
			
			if (brightnessFactor >= MAX_BRIGHT_FACT)
			{
				brightRaiseFlag = !brightRaiseFlag;
			}
		}
		else
		{
			brightnessFactor -= BRIGHT_INTERVS;
			
			if (brightnessFactor <= MIN_BRIGHT_FACT)
			{
				brightRaiseFlag = !brightRaiseFlag;
			}
		}
	}
	
	@Override
	public void render() 
	{
		RenderUtils.renderTexture(backgroundTex,
				0, 0, Main.getNormalDimensions()[0], Main.getNormalDimensions()[1]);
		
		titleShader.useProgram();
		titleShader.setUniformf("brightnessFactor", brightnessFactor);
		RenderUtils.renderTexture(titleTex, TITLE_POS[0], TITLE_POS[1]);
		titleShader.stopProgram();
		
		renderState();
		
		if(nextState != null)
		{
			transShader.useProgram();
			transShader.setUniformf("alphaFactor", alphaFactor);
			RenderUtils.renderRectangle(0, 0, Main.getNormalDimensions()[0], Main.getNormalDimensions()[1]);
			transShader.stopProgram();
		}
	}

	private void renderState()
	{
		switch(state)
		{
		case PROFILE_CREATION:
			profileManager.renderCreation();
			break;
	
		case PROFILE_SELECTION:
			profileManager.renderSelection();
			break;
		
		case MAIN_MENU:
			renderOptions();
			break;
			
		case CUSTOMIZE:
			custom.render();
			break;
				
		case PLAY:
			break;
		
		
		case EXIT:
			break;
		}
	}

	private void renderOptions()
	{
		for(MainOption option : options)
		{
			option.render();
		}
	}
	
    @Override
    public boolean isPaused()
    {
        return false;
    }
    
    @Override
    public UserProfile getCurrentUser()
    {
        return currentUserProfile;
    }
    
    private void setState(MenuState state)
    {
    	transState = TransState.DARKEN;
    	alphaFactor += TRANS_INTERVS;
    	
    	for(MainOption option : options)
    	{
    		option.setHighlighted(false);
    	}
    	
    	this.nextState = state;
    }
}
