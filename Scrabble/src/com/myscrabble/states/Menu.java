package com.myscrabble.states;

import org.newdawn.slick.opengl.Texture;

import com.myscrabble.entities.Board;
import com.myscrabble.main.Main;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.KeyboardManager;
import com.myscrabble.managers.ProfileManager;
import com.myscrabble.rendering.Shader;
import com.myscrabble.rendering.Shader.ShaderType;
import com.myscrabble.user.UserProfile;
import com.myscrabble.util.RenderUtils;

/**
 * @author Alex Koukoulas
 * Class Description:
 * Main menu gamestate
 */

public class Menu extends GameState
{
	private static final String TEX_DIR = "/menu/";
	private static final String BG_TEX_NAME = "background.png";
	private static final String TITLE_TEX_NAME = "title.png";
	
	private static final float MAX_BRIGHT_FACT = 2.0f;
	private static final float MIN_BRIGHT_FACT = 0.5f;
	private static final float BRIGHT_INTERS   = 0.02f;
	
	private ProfileManager profileManager;
    private UserProfile currentUserProfile;
    private Texture backgroundTex;
    private Texture titleTex;
    private Shader titleShader;
    
    private float brightnessFactor;
    private boolean brightRaiseFlag;
    
	public Menu(GameStateManager gsm)
	{
		super(gsm);
		
		profileManager = new ProfileManager();
		currentUserProfile = new UserProfile("Alex");
		titleShader = new Shader(ShaderType.AUTO_BRIGHTNESS);
		
		loadTextures();
		
		brightnessFactor = MIN_BRIGHT_FACT;
		brightRaiseFlag  = true;
	}
	
	private void loadTextures()
	{
		backgroundTex = gsm.getRes().loadTexture(TEX_DIR + BG_TEX_NAME);
		titleTex = gsm.getRes().loadTexture(TEX_DIR + TITLE_TEX_NAME);
	}
	
	@Override
	public void handleInput() 
	{
		if (KeyboardManager.isKeyDown(KeyboardManager.K_SPACE))
		{
		    finished = true;
		}
	}

	@Override
	public void update() 
	{
		updateBrightnessFactor();
	}

	private void updateBrightnessFactor()
	{
		if (brightRaiseFlag)
		{
			brightnessFactor += BRIGHT_INTERS;
			
			if (brightnessFactor >= MAX_BRIGHT_FACT)
			{
				brightRaiseFlag = !brightRaiseFlag;
			}
		}
		else
		{
			brightnessFactor -= BRIGHT_INTERS;
			
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
		RenderUtils.renderTexture(titleTex, 120, 100);
		titleShader.stopProgram();
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
}
