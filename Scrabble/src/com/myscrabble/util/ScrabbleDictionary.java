package com.myscrabble.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.myscrabble.managers.ResourceManager;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * Scrabble dictionary saves all the valid words
 * contained in a predefined dictionary file and is
 * used to check the validity of players's input.
 */
public class ScrabbleDictionary
{
    private static Set<String> commonPrefixes;
    
    static
    {
        commonPrefixes = new HashSet<String>();
        commonPrefixes.add("S");  commonPrefixes.add("ED"); commonPrefixes.add("D");
        commonPrefixes.add("ER");
    }
    
	private static final String DICT_DIR = "/specs/dictionary.dict";
	private static final String COMMON_DIR = "/specs/common.dict";
	
	private Set<String> words;
	private Set<String> common;
	
	private String mainContent;
	private String commonContent;
	
	public ScrabbleDictionary()
	{
		loadContent();
		createSets();
	}

	private void loadContent()
	{
		mainContent = ResourceManager.loadFileAsString(DICT_DIR);
		commonContent = ResourceManager.loadFileAsString(COMMON_DIR);
	}
	
	private void createSets()
	{
		words = new HashSet<String>();
		
		for(String word : mainContent.split(System.lineSeparator()))
		{
			words.add(word.toUpperCase());
		}
		
		common = new HashSet<String>();
		
		for(String word : commonContent.split(System.lineSeparator()))
		{
			common.add(word.toUpperCase());
		}
	}
	
	/**
	 * 
	 * @return The whole set
	 * of words
	 */
	public Iterator<String> getWords()
	{
		return words.iterator();
	}
	
    
	public Iterator<String> getCommonPrefixes()
	{
	    return commonPrefixes.iterator();
	}
	
	/**
	 * 
	 * @param word String word to check.
	 * @return Whether that word is a valid
	 * choice for the game.
	 */
	public boolean wordExists(String word)
	{
		return words.contains(word);
	}
	
	/**
	 * 
	 * @param word to check 
	 * @return whether the word is present
	 * in the common words dictionary
	 */
	public boolean isCommon(String word)
	{
		return common.contains(word);
	}
}
