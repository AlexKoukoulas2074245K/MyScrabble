package com.myscrabble.uicomponents;

import org.newdawn.slick.TrueTypeFont;

import com.myscrabble.managers.KeyboardManager;
import com.myscrabble.managers.ResourceManager;

public class PauseMenu 
{
    private static final String FONT_NAME = "font_bold";
    private static final int FONT_SIZE = 36;
    private static final boolean ANTI_ALIAS = true;
    
    private TrueTypeFont font;
    private boolean isActive;
    
    public PauseMenu(ResourceManager rm)
    {
        isActive = false;
        font = rm.loadFont(FONT_NAME, FONT_SIZE, ANTI_ALIAS);
    }
    
    public void handleInput()
    {
        if(KeyboardManager.isKeyPressed(KeyboardManager.K_ESCAPE))
        {
            isActive = false;
        }
    }
    
    public void render()
    {
        font.drawString(400, 400, "PAUSED");
    }
    
    public boolean isActive()
    {
        return isActive;
    }
    
    public void setActive(boolean isActive)
    {
        this.isActive = isActive;
    }
}
