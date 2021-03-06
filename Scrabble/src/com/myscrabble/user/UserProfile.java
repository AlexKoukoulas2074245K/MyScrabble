package com.myscrabble.user;

import java.io.File;
import java.util.IllegalFormatException;

import com.myscrabble.managers.ResourceManager;
import com.myscrabble.util.ScrabbleUtils;

/**
 * 
 * @author 2074245k
 * A user profile holds all the data
 * for a user including current points
 * and unlocked features
 */
public class UserProfile 
{
    private static final String USER_SAVE_EXT = ".user";
    private static final String USER_SAVE_DIR = ResourceManager.SAV_DIR;
    
    private String userName;
    private int currentTokens;
    private boolean[] backgroundsUnlocked;
    private int lastBgUsed;
    private int timePlayed;
    
    public UserProfile(String userName)
    {
        this.userName = userName;
        
        String fileContent = loadUserProfile();
        parseFileContent(fileContent);
    }
    
    private String loadUserProfile()
    {
        boolean fileAccess = true;
        
        File saveDirectory = new File(USER_SAVE_DIR);
        
        if(!saveDirectory.isDirectory())
        {    
            fileAccess = false;
        }
        
        File userSaveFile = new File(USER_SAVE_DIR + "/" + userName + USER_SAVE_EXT);
        
        if(!userSaveFile.isFile())
        {
            fileAccess = false;
        }
        
        if(!fileAccess)
        {
            createUserProfile();
        }
        
        return ResourceManager.loadFileAsString(userSaveFile.getAbsolutePath(), true, true);       
    }
    
    private void createUserProfile()
    {
        File saveDirectory = new File(USER_SAVE_DIR);
        
        if(!saveDirectory.isDirectory())
        {
            saveDirectory.mkdir();
        }
        
        File userFile = new File(USER_SAVE_DIR + "/" + userName + USER_SAVE_EXT);
        
        if(!userFile.isFile())
        {
            StringBuilder content = new StringBuilder();
            
            content.append("0"); content.append(System.lineSeparator());
            content.append("0 0 0 0 0 0 0 0 0 1"); content.append(System.lineSeparator());
            content.append("9"); content.append(System.lineSeparator());
            content.append("0"); content.append(System.lineSeparator());
            
            ResourceManager.writeToFile(userFile, content.toString());
        }
    }
    
    private void parseFileContent(String fileContent)
    {
        String[] contentLines = fileContent.split(System.lineSeparator());
        
        try
        {
            currentTokens = Integer.parseInt(contentLines[0]);
                  
            String[]splitBackgroundInfo = contentLines[1].split("\\s+");
            backgroundsUnlocked = new boolean[splitBackgroundInfo.length];
            
            for(int i = 0; i < splitBackgroundInfo.length; i++)
            {
                backgroundsUnlocked[i] = splitBackgroundInfo[i].equals("1");
            }
            
            lastBgUsed = Integer.parseInt(contentLines[2]);
            timePlayed = Integer.parseInt(contentLines[3]);
            
        }
        catch(IllegalFormatException e)
        {
            System.err.println("Corrupted save file: " + USER_SAVE_DIR + "/" + userName + USER_SAVE_EXT);
            System.exit(1);
        }
    }
    
    public void save()
    {
        File userFile = new File(USER_SAVE_DIR + "/" + userName + USER_SAVE_EXT);
        
        if(!userFile.isFile())
        {
            return;
        }
        
        StringBuilder content = new StringBuilder();
        
        content.append(currentTokens); content.append(System.lineSeparator());
        
        for(int i = 0; i < backgroundsUnlocked.length; i++)
        {
            if(backgroundsUnlocked[i])
            {
                content.append("1");
            }
            else
            {
                content.append("0");
            }
            
            if(i != backgroundsUnlocked.length - 1)
            {
                content.append(" ");
            }
        }content.append(System.lineSeparator());
        
        content.append(lastBgUsed); content.append(System.lineSeparator());
        content.append(timePlayed); content.append(System.lineSeparator());
        
        ResourceManager.writeToFile(userFile, content.toString());
    }
    
    @Override
    public String toString()
    {
    	return userName + " (played for: " + ScrabbleUtils.getTimeRepresentation(timePlayed) + ")";
    }
    
    public String getName()
    {
    	return userName;
    }
    
    public int getTimePlayed()
    {
        return timePlayed;
    }
    
    public int getCurrentTokens()
    {
        return currentTokens;
    }
    
    public int getLastBackgroundUsed()
    {
    	return lastBgUsed;
    }
    
    public boolean[] getBackgroundsUnlocked()
    {
        return backgroundsUnlocked;
    }
    
    public void addTimePlayed(int time)
    {
        this.timePlayed += time;
    }
    
    public void addTokens(int tokens)
    {
    	currentTokens += tokens;
    }
    
    public void setLastBackgroundUsed(int lastBgUsed)
    {
    	this.lastBgUsed = lastBgUsed;
    }
}
