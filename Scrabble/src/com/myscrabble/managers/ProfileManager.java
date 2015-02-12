package com.myscrabble.managers;

import com.myscrabble.user.UserProfile;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * Profile Manager manages
 * user created profiles
 */
public class ProfileManager
{
	private UserProfile[] allProfiles;
	private String[] profileNames;
	private boolean profilesFound;
	
	public ProfileManager()
	{
		searchForProfiles();
	}
	
	/**
	 * Searches for existing
	 * user profiles
	 */
	private void searchForProfiles()
	{
		profileNames = ResourceManager.getFileNames(
			           ResourceManager.SAV_DIR);
		
		profilesFound = profileNames.length == 0 ||
				        profileNames == null;
		
		allProfiles = new UserProfile[profileNames.length];
		
		for (int i = 0;
			 i < profileNames.length;
			 i++)
		{
			String strpName = profileNames[i].split(ResourceManager.DOT_REGEX)[0];
			allProfiles[i] = new UserProfile(strpName);
		}
	}
	
	public boolean profilesFound()
	{
		return profilesFound;
	}
	
	public UserProfile[] getAllProfiles()
	{
		return allProfiles;
	}
}
