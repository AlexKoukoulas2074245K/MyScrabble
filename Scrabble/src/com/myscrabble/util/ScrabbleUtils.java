package com.myscrabble.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import com.myscrabble.entities.LetterTile;
import com.myscrabble.entities.Tile;
import com.myscrabble.entities.TileFormation;
import com.myscrabble.entities.Tilemap;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * ScrabbleUtils contains hash maps and
 * useful methods concerning the logic of
 * the game, quantities of letters and point
 * values of letters 
 */

public class ScrabbleUtils
{
	private static HashMap<Character, Integer> noLetters;
	private static HashMap<Character, Integer> letterPoints;
	
	static
	{
		noLetters = new HashMap<Character, Integer>();
		noLetters.put('A', 9);  noLetters.put('B', 2);  noLetters.put('C', 2);
		noLetters.put('D', 4);  noLetters.put('E', 12); noLetters.put('F', 2);
		noLetters.put('G', 3);  noLetters.put('H', 2);  noLetters.put('I', 9);
		noLetters.put('J', 1);  noLetters.put('K', 1);  noLetters.put('L', 4);
		noLetters.put('M', 2);  noLetters.put('N', 6);  noLetters.put('O', 8);
		noLetters.put('P', 2);  noLetters.put('Q', 1);  noLetters.put('R', 6);
		noLetters.put('S', 4);  noLetters.put('T', 6);  noLetters.put('U', 4);
		noLetters.put('V', 2);  noLetters.put('W', 2);  noLetters.put('X', 1);
		noLetters.put('Y', 2);  noLetters.put('Z', 1);
		
		letterPoints = new HashMap<Character, Integer>();
		letterPoints.put('A', 1);  letterPoints.put('B', 3);  letterPoints.put('C', 3);
		letterPoints.put('D', 2);  letterPoints.put('E', 1);  letterPoints.put('F', 4);
		letterPoints.put('G', 2);  letterPoints.put('H', 4);  letterPoints.put('I', 1);
		letterPoints.put('J', 8);  letterPoints.put('K', 5);  letterPoints.put('L', 1);
		letterPoints.put('M', 3);  letterPoints.put('N', 1);  letterPoints.put('O', 1);
		letterPoints.put('P', 3);  letterPoints.put('Q', 10); letterPoints.put('R', 1);
		letterPoints.put('S', 1);  letterPoints.put('T', 1);  letterPoints.put('U', 1);
		letterPoints.put('V', 4);  letterPoints.put('W', 4);  letterPoints.put('X', 8);
		letterPoints.put('Y', 4);  letterPoints.put('Z', 10); 
	}
	
	public static Integer getNumberOf(final char letter)
	{
		return noLetters.get(letter);
	}
	
	public static Integer getValueOf(final char letter)
	{
		return letterPoints.get(letter);
	}
	
	/**
	 * 
	 * @param candidates The list of words to extract the biggest word from.
	 * @return the word with the biggest length from the supplied
	 * list.
	 */
	public static String getBiggestWord(ArrayList<String> candidates)
	{
		String max = new String();
		
		for(String candidate : candidates)
		{
			if(candidate.length() > max.length())
			{
				max = candidate;
			}
		}
		
		return max;
	}
	
	/**
	 * 
	 * @param candidates The list of words to extract the biggest common word from.
	 * @param scrabbleDict Scrabble dictionary to extract common words.
	 * @return the common word with the biggest length from the supplied.
	 * list
	 */
	public static String getBiggestCommon(ArrayList<String> candidates, ScrabbleDictionary scrabbleDict)
	{
		String max = new String();
		
		for(String candidate : candidates)
		{
			if(candidate.length() > max.length() && scrabbleDict.isCommon(candidate))
			{
				max = candidate;
			}
		}
		
		return max;
	}
	
	/**
	 * 
	 * @param candidates The list of words to extract a random common word from.
	 * @param scrabbleDict Scrabble dictionary to extract common words.
	 * @return a random common word found in the supplied words list.
	 */
	public static String getRandomCommon(ArrayList<String> candidates, ScrabbleDictionary scrabbleDict)
	{
		ArrayList<String> validCommonWords = new ArrayList<>();
		
		for(String candidate : candidates)
		{
			if(scrabbleDict.isCommon(candidate))
			{
				validCommonWords.add(candidate);
			}
		}
		
		int randomIndex = new Random().nextInt(validCommonWords.size());
		
		return validCommonWords.get(randomIndex);
	}
	
	/**
	 * 
	 * @param candidates The list of words to extract a random word from.
	 * @return A random word from the list of words supplied.
	 */
	public static String getRandomWord(ArrayList<String> candidates)
	{
		int randomIndex = new Random().nextInt(candidates.size());
		
		return candidates.get(randomIndex);
	}
	
	/**
	 * 
	 * @param letterTiles ArrayList of Letter Tiles to feed the data from
	 * @param direction the direction of the letter tiles
	 * @return the ascending coordinates of all the letters tiles
	 * in the supplied array list
	 */
	public static ArrayList<Float> getAscendingPositions(ArrayList<LetterTile> letterTiles, int direction)
	{
		ArrayList<Float> result = new ArrayList<>();
		
		for(LetterTile letterTile : letterTiles)
		{
			if(direction == TileFormation.HORIZONTAL)
			{
				result.add(letterTile.getX());
			}
			else
			{
				result.add(letterTile.getY());
			}
		}
		
		Collections.sort(result);

		return result;
	}
	
	/**
	 * 
	 * @param t1 
	 * @param t2
	 * @return absolute horizontal distance between the two letter tiles
	 */
	public static float xDistanceBetween(LetterTile t1, LetterTile t2)
	{
		return (float)Math.abs(t1.getX() - t2.getX());
	}
	
	/**
	 * 
	 * @param t1 
	 * @param t2
	 * @return absolute vertical distance between the two letter tiles
	 */
	public static float yDistanceBetween(LetterTile t1, LetterTile t2)
	{
		return (float)Math.abs(t1.getY() - t2.getY());
	}
	
	/**
	 * 
	 * @param tileFormation The tiles to be evaluated
	 * @return the total points of the word that
	 * the tiles produce along with any modifiers in the board
	 * (double word, triple letter etc.)
	 */
	public static int calculatePoints(ArrayList<LetterTile> letterTiles, Tilemap tilemap)
	{
	    int result = 0;
	    int modifier = 0;
	    
	    for(LetterTile letterTile : letterTiles)
	    {
	        
	        int letterPoints = letterTile.getPoints();
	        int tileType = tilemap.getLetterTileHolder(letterTile).getType();
	        
	        if(tileType == Tile.DOUBLE_LETTER || tileType == Tile.TRIPLE_LETTER)
	        {
	            result += letterPoints * tileType;
	        }
	        else
	        {
	            result += letterPoints;
	        }
	        
	        if(tileType == Tile.DOUBLE_WORD)
	        {
	            modifier += 2; 
	        }
	        else if(tileType == Tile.TRIPLE_WORD)
	        {
	            modifier += 3;
	        }
	        
	    }
	    
	    if(modifier > 0)
	    {
	        result *= modifier;
	    }
	    
	    return result;
	}
}
