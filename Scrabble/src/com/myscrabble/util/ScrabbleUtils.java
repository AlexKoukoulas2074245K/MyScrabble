package com.myscrabble.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.myscrabble.entities.Tile;

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
	
	public static int calculatePoints(ArrayList<Tile> tileFormation)
	{
	    int result = 0;
	    int modifier = 0;
	    
	    for(Tile tile : tileFormation)
	    {
	        
	        int letterPoints = tile.getLetterTile().getPoints();
	        int tileType = tile.getType();
	        
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
