package com.myscrabble.util;

import java.util.HashSet;
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
	private static final String DICT_DIR = "/specs/dictionary.dict";
	
	private Set<String> words;
	private String content;
	
	public ScrabbleDictionary(ResourceManager rm)
	{
		loadContent(rm);
		createSet();
	}
	
	private void loadContent(ResourceManager rm)
	{
		content = rm.loadFileAsString(DICT_DIR);
	}
	
	private void createSet()
	{
		words = new HashSet<String>();
		
		for(String word : content.split(System.lineSeparator()))
		{
			words.add(word);
		}
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
}
