package com.myscrabble.managers;

import java.awt.Rectangle;
import java.io.File;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;

import com.myscrabble.user.UserProfile;
import com.myscrabble.util.RenderUtils;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * Profile Manager manages
 * user created profiles
 */
public class ProfileManager
{	
	public static final org.newdawn.slick.Color STD_FONT_COLOR = 
	                new org.newdawn.slick.Color(196, 196, 196);
	public static final org.newdawn.slick.Color DIM_FONT_COLOR = 
     	            new org.newdawn.slick.Color(148, 148, 148);
	public static final org.newdawn.slick.Color WHITE_FONT_COLOR = 
	 		        new org.newdawn.slick.Color(255, 255, 255);
	
	/* Asset directories and paths */
	private static final String FONT_NAME         = "font_regular";
	private static final String PROF_CRE_TEX      = "/menu/profileCreation";
	private static final String PROF_VAL_TEX      = "/menu/profileCreationValid";
	private static final String PROF_VAL_HIGH_TEX = "/menu/profileCreationValidHigh";
	private static final String NEW_PROFILE_TEX   = "/menu/createNewProfile";
	private static final String NEW_PROFILE_HIGH  = "/menu/createNewProfileHigh";
	
	private static final int MAX_PROFILES = 3;
	
	/* Positional Constants */
	private static final float   FONT_SIZE           = 34;
	private static final float[] PROF_RECT_SIZE      = new float[]{256, 32};
	private static final float[] PROF_CRE_POS        = new float[]{120, 264};
	private static final float[] NEW_NAME_POS        = new float[]{PROF_CRE_POS[0] + 76, PROF_CRE_POS[1] + 150};
	private static final float[] VALID_PROF_TEXT_POS = new float[]{NEW_NAME_POS[0] - 70, NEW_NAME_POS[1] + 64};
	private static final float[] SEL_PROF_TEXT_POS   = new float[]{PROF_CRE_POS[0] + 24, PROF_CRE_POS[1] - 48};
	private static final float[] PROF_FOUND_POS      = new float[]{SEL_PROF_TEXT_POS[0], PROF_CRE_POS[1]};
	private static final float[] FIRST_PROF_POS      = new float[]{PROF_FOUND_POS[0] + 24, PROF_FOUND_POS[1] + 48};
	private static final float[] CRE_NEW_PROF_POS    = new float[]{220, 480};
	
	private static final float PROF_Y_MARGIN = 48;
	

	
	private static final int MAX_PROFILE_NAME_SIZE = 16;
	
	private GameStateManager gsm;
	
	/* All user attributes needed */
	private UserProfile[] allProfiles;
	private String[] profileNames;
	private boolean[] profilesHighlighted;
	private Rectangle[] profileRects;
	private Rectangle createNewProfileRect;
	
	private Texture profileCreationTex;
	private Texture profileCreationValidTex;
	private Texture profileCreationValidHigh;
	private Texture newProfileTex;
	private Texture newProfileHigh;

	private boolean validTextHigh;
	private boolean createNewHigh;
	
	private String newProfileName;
	
	private boolean profilesFound;
	private boolean validProfileName;
	
	private boolean finishedCreation;
	private boolean createNewProfileRequest;
	
	private UserProfile selectedProfile;
	private TrueTypeFont font;

	
	public ProfileManager(GameStateManager gsm)
	{
		this.gsm = gsm;
		searchForProfiles();
		createFont();
		loadTextures();
		newProfileName   = "";
		validProfileName = false;
		validTextHigh    = false;
		finishedCreation = false;
		createNewHigh    = false;
		selectedProfile  = null;
		
		createNewProfileRect = new Rectangle((int)CRE_NEW_PROF_POS[0],
				  						     (int)CRE_NEW_PROF_POS[1],
				  						     newProfileTex.getTextureWidth(),
				  						     newProfileTex.getTextureHeight());
	}
	
	/**
	 * Searches for existing
	 * user profiles
	 */
	private void searchForProfiles()
	{
		File saveDir = new File(ResourceManager.SAV_DIR);
	
		if(!saveDir.isDirectory())
		{
			saveDir.mkdir();
		}
		
		profileNames = ResourceManager.getFileNames(
			           ResourceManager.SAV_DIR);
		
		profilesFound = profileNames.length != 0 &&
				        profileNames != null;
		
		allProfiles = new UserProfile[profileNames.length];
		profilesHighlighted = new boolean[profileNames.length];
		profileRects = new Rectangle[profileNames.length];
		
		if(profilesFound)
		{
			for (int i = 0;
				 i < profileNames.length;
				 i++)
			{
				String strpName = profileNames[i].split(ResourceManager.DOT_REGEX)[0];
				allProfiles[i] = new UserProfile(strpName);			
				
				profileRects[i] = new Rectangle((int)FIRST_PROF_POS[0],
						                        (int)FIRST_PROF_POS[1] + i * (int)PROF_Y_MARGIN,
						                        (int)PROF_RECT_SIZE[0],
						                        (int)PROF_RECT_SIZE[1]);
			}
		}
	}
	
	private void createFont()
	{
		font = gsm.getRes().loadFont(FONT_NAME, FONT_SIZE, true);
	}
	
	private void loadTextures()
	{
		profileCreationTex       = gsm.getRes().loadTexture(PROF_CRE_TEX);
		profileCreationValidTex  = gsm.getRes().loadTexture(PROF_VAL_TEX);
		profileCreationValidHigh = gsm.getRes().loadTexture(PROF_VAL_HIGH_TEX);
		newProfileTex            = gsm.getRes().loadTexture(NEW_PROFILE_TEX);
		newProfileHigh           = gsm.getRes().loadTexture(NEW_PROFILE_HIGH);
	}
	
	public void handleInputCreation()
	{
		feedProfileName();
		validProfileName = newProfileName.length() > 0;
		validTextHigh = isValidTextHighlighted();
		
		if((validTextHigh && MouseManager.isButtonPressed(MouseManager.LEFT_BUTTON)) ||
			KeyboardManager.isKeyPressed(KeyboardManager.K_ENTER))
		{
			new UserProfile(newProfileName); //just need to register the profile no need to assign it somewhere
			finishedCreation = true;
		}
	}

	public void handleInputSelection()
	{
		for(int i = 0; i < allProfiles.length; i++)
		{
			if(profilesHighlighted[i] && MouseManager.isButtonPressed(MouseManager.LEFT_BUTTON))
			{
				selectedProfile = allProfiles[i];
			}
		}
		
		createNewHigh = createNewProfileRect.contains(MouseManager.getX(), MouseManager.getY()) &&
				        allProfiles.length < MAX_PROFILES;
		
		if(createNewHigh && MouseManager.isButtonPressed(MouseManager.LEFT_BUTTON))
		{
			createNewProfileRequest = true;
		}
	}
	
	public void updateSelection()
	{
		for(int i = 0; i < allProfiles.length; i++)
		{
			profilesHighlighted[i] = profileRects[i].contains(MouseManager.getX(),
														      MouseManager.getY());
		}
	}
	
	private void feedProfileName()
	{
		char nextChar = KeyboardManager.getTyped();
		
		if(nextChar != '\0') // nothing typed
		{
			if(nextChar == '-') // deletion
			{
				if(newProfileName.length() > 0)
				{
					newProfileName = newProfileName.substring(0, newProfileName.length() - 1);
				}
			}
			else // append
			{
				if(newProfileName.length() < MAX_PROFILE_NAME_SIZE)
				{
					newProfileName += nextChar;
				}
			}
		}	
	}
	
	public void renderCreation()
	{
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
		RenderUtils.renderTexture(profileCreationTex, 
				                  PROF_CRE_POS[0],
				                  PROF_CRE_POS[1]);
		
		font.drawString(NEW_NAME_POS[0], NEW_NAME_POS[1], newProfileName, STD_FONT_COLOR);
		
		if(validProfileName && !validTextHigh)
		{
			RenderUtils.renderTexture(profileCreationValidTex,
					                  VALID_PROF_TEXT_POS[0],
					                  VALID_PROF_TEXT_POS[1]);
		}
		if(validProfileName && validTextHigh)
		{
			RenderUtils.renderTexture(profileCreationValidHigh,
					                  VALID_PROF_TEXT_POS[0],
					                  VALID_PROF_TEXT_POS[1]);
		}
		
		GL11.glPopAttrib();
	}
	
	public void renderSelection()
	{
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
		font.drawString(SEL_PROF_TEXT_POS[0], SEL_PROF_TEXT_POS[1], "Selected a profile to play with.");
		font.drawString(PROF_FOUND_POS[0], PROF_FOUND_POS[1], "Profiles found:");
		
		
		for(int i = 0; i < profileNames.length; i++)
		{
			if(profilesHighlighted[i])
			{
				font.drawString(profileRects[i].x, 
								profileRects[i].y,
								allProfiles[i].toString(),
								WHITE_FONT_COLOR);
			}
			else
			{
				font.drawString(profileRects[i].x, 
							    profileRects[i].y,
						        allProfiles[i].toString(),
						        DIM_FONT_COLOR);
			}
		}
		GL11.glPopAttrib();
		
		if(createNewHigh)
		{
			RenderUtils.renderTexture(newProfileHigh, 
					                  CRE_NEW_PROF_POS[0],
					                  CRE_NEW_PROF_POS[1]);
		}
		else
		{
			RenderUtils.renderTexture(newProfileTex,
								      CRE_NEW_PROF_POS[0],
								      CRE_NEW_PROF_POS[1]);
		}
		
		GL11.glPopAttrib();
	}
	
	public boolean getFinishedCreation()
	{
		return finishedCreation;
	}
	
	public boolean profilesFound()
	{
		return profilesFound;
	}
	
	public boolean getCreateNewRequest()
	{
		return createNewProfileRequest;
	}
	
	public UserProfile getSelectedProfile()
	{
		return selectedProfile;
	}
	
	public UserProfile[] getAllProfiles()
	{
		return allProfiles;
	}
	
	public boolean isValidTextHighlighted()
	{
		return new Rectangle((int)VALID_PROF_TEXT_POS[0],(int)VALID_PROF_TEXT_POS[1],
				             profileCreationValidTex.getTextureWidth(),
				             profileCreationValidTex.getTextureHeight()).
				             contains(MouseManager.getX(), MouseManager.getY());
	}
}
