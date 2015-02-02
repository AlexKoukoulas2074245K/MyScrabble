package com.myscrabble.uicomponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;

import com.myscrabble.entities.Player;
import com.myscrabble.managers.ResourceManager;
import com.myscrabble.util.RenderUtils;

/**
 * 
 * @author Alex Koukoulas 2074245k
 * Class Description:
 * A class handling the score display 
 */
public class ScoreDisplay 
{
	private static final int FONT_SIZE = 24;
	private static final String REG_FONT_NAME = "font_bold";
	
    private static final String TEX_DIR = "/misc/score/";
    private static final int DEFAULT_STYLE = 1;
    
    private static final float TEXTURE_MARGIN   = 40.0f;
    private static final float[] renderingPos1  = new float[]{600.0f, 96.0f};
    private static final float[] renderingPos2  = new float[]{600.0f, 200.0f};
    private static final float[] player1NamePos = new float[]{610,66};
    private static final float[] player2NamePos = new float[]{610,170};
    
    private static final Color PLAYER_NAME_COL  = new Color(218, 255, 128);
    private static final float SCORE_SPEED = 0.4f;
    
    private ResourceManager rm;
    private ArrayList<Texture> scoreTextures;
    
    private TrueTypeFont regFont;
    
    private HashMap<Player, Float> currentPoints;
    private HashMap<Player, Float> pointsToAdd;
    private Player player1Ref;
    private Player player2Ref;
    
    private int style;
    
    public ScoreDisplay(ResourceManager rm, Player player1Ref, Player player2Ref)
    {
        this.rm = rm;
        
        this.player1Ref = player1Ref;
        this.player2Ref = player2Ref;
        initPointMaps(player1Ref, player2Ref);
        
        style = DEFAULT_STYLE;
        scoreTextures = rm.getAllTextures(TEX_DIR + style);
        
        loadFonts();
    }
    
    private void initPointMaps(Player player1Ref, Player player2Ref)
    {
    	currentPoints = new HashMap<Player, Float>();
    	currentPoints.put(player1Ref, 0.0f);
    	currentPoints.put(player2Ref, 0.0f);
    	
    	pointsToAdd = new HashMap<Player, Float>();    	
    	pointsToAdd.put(player1Ref, 0.0f);
    	pointsToAdd.put(player2Ref, 0.0f);
    }
    
    private void loadFonts()
    {
    	regFont  = rm.loadFont(REG_FONT_NAME, FONT_SIZE, true);
    }
    
    public void update()
    {
    	for(Entry<Player, Float> entry : pointsToAdd.entrySet())
    	{
    		float scoreToAdd = entry.getValue();
    		float currentScore = currentPoints.get(entry.getKey());
    		
    		if(entry.getValue() > 0)
    		{
    			scoreToAdd -= SCORE_SPEED;
    			currentScore += SCORE_SPEED;
    		}
    		
    		entry.setValue(scoreToAdd);
    		currentPoints.replace(entry.getKey(), currentScore);
    	}
    }
    
    public void render()
    {
        renderPlayerScore();
        renderPlayerNames();
    }
    
    private void renderPlayerScore()
    {
        
        char[] score1Comps = getComponents((int)((float)currentPoints.get(player1Ref)));
        
        for(int i = 0; i < score1Comps.length; i++)
        {
            Texture currentTexture = scoreTextures.get(Integer.parseInt(String.valueOf(score1Comps[i]))); 
            RenderUtils.renderTexture(currentTexture, renderingPos1[0] + i * TEXTURE_MARGIN, renderingPos1[1]);
        }
        
        char[] score2Comps = getComponents((int)((float)currentPoints.get(player2Ref)));
        
        for(int i = 0; i < score2Comps.length; i++)
        {
            Texture currentTexture = scoreTextures.get(Integer.parseInt(String.valueOf(score2Comps[i]))); 
            RenderUtils.renderTexture(currentTexture, renderingPos2[0] + i * TEXTURE_MARGIN, renderingPos2[1]);
        }
    }
    
    private void renderPlayerNames()
    {
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
        regFont.drawString(player1NamePos[0], player1NamePos[1], player1Ref.getName(), PLAYER_NAME_COL);
        regFont.drawString(player2NamePos[0], player2NamePos[1], player2Ref.getName(), PLAYER_NAME_COL);
        GL11.glPopAttrib();
    }
    
    private char[] getComponents(Integer score)
    {
        char[] result = new char[3];
        
        if(score < 10)
        {
            result[0] = '0';
            result[1] = '0';
            result[2] = String.valueOf(score).charAt(0);
        }
        else if(score < 100)
        {
            result[0] = '0';
            result[1] = String.valueOf(score).charAt(0);
            result[2] = String.valueOf(score).charAt(1);
        }
        else
        {
            result = String.valueOf(score).toCharArray();
        }
        
        return result;
    }
    
    public void addPoints(int points, Player player)
    {
    	pointsToAdd.replace(player, pointsToAdd.get(player) + points);
    }
}
