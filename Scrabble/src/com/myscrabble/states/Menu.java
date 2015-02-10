package com.myscrabble.states;

import org.newdawn.slick.opengl.Texture;

import com.myscrabble.entities.Board;
import com.myscrabble.main.Main;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.KeyboardManager;
import com.myscrabble.user.UserProfile;
import com.myscrabble.util.RenderUtils;

/**
 * @author Alex Koukoulas
 * Class Description:
 */

public class Menu extends GameState
{
    private UserProfile currentUserProfile;
    private Board board;
    
	public Menu(GameStateManager gsm)
	{
		super(gsm);
		
		currentUserProfile = new UserProfile("Alex");
		board = new Board(gsm);
	}

	@Override
	public void handleInput() 
	{
		if(KeyboardManager.isKeyDown(KeyboardManager.K_SPACE))
		{
		    finished = true;
		}
	}

	@Override
	public void update() 
	{
		
	}

	@Override
	public void render() 
	{
		for(int i = 0; i < board.getBackgroundTextures().size(); i++)
		{
		    if(currentUserProfile.getBackgroundsUnlocked()[i])
		    {
		        RenderUtils.renderTexture(board.getBackgroundTextures().get(i), i * (Main.getNormalDimensions()[0] / board.getBackgroundTextures().size()), 0, Main.getNormalDimensions()[0] / board.getBackgroundTextures().size(), Main.getNormalDimensions()[0] / board.getBackgroundTextures().size());
		    }
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
}
