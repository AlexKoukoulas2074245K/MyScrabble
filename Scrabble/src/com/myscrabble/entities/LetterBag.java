package com.myscrabble.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.myscrabble.managers.GameStateManager;
import com.myscrabble.util.ScrabbleUtils;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * This entity represents the cloth bag used to
 * draw letter tiles from. When a draw request
 * is done a LetterTile is returned.
 */

public class LetterBag extends GameObject
{
	/* Standard english alphabet letters */
	private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	/* A collection of letters that the players can draw from */
	private ArrayList<Character> letters;
	
	public LetterBag(GameStateManager gsm)
	{
		super(gsm);
		fillBag();
		shuffleBag();
	}
	
	public char drawLetter()
	{
		/* selection of random index */
		int randIndex = new Random().nextInt(letters.size());
		
		/* accumulation of the random letter */
		char chosenLetter = letters.get(randIndex);
		
		/* pop the random letter from bag */
		letters.remove(randIndex);
		
		return chosenLetter;
	}
	
	private void fillBag()
	{
		letters = new ArrayList<Character>();
		
		for(int i = 0; i < alphabet.length(); i++)
		{
			for(int n = 0; n < ScrabbleUtils.getNumberOf(alphabet.charAt(i)); n++)
			{
				letters.add(alphabet.charAt(i));
			}
		}
	}
	
	private void shuffleBag()
	{
		Collections.shuffle(letters);
	}
}
