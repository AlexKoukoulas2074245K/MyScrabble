package ai;

import java.util.ArrayList;
import java.util.Arrays;

import com.myscrabble.entities.Board;
import com.myscrabble.entities.LetterTile;
import com.myscrabble.entities.Player;
import com.myscrabble.entities.TileRack;
import com.myscrabble.util.ScrabbleDictionary;
import com.myscrabble.util.ScrabbleUtils;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A class concerned with the behaviour
 * of a computer-controlled player
 */
public class AIController
{	
	public enum AILevel
	{
		ROOKIE(0),
		AMATEUR(1),
		INTERMEDIATE(2),
		HARD(3);
		
		public int levelValue;
		
		private AILevel(int levelValue)
		{
			this.levelValue = levelValue;
		}
	}
	
	/* The ability of this AI controller */
	private AILevel aiLevel;
	
	/* References to core objects */
	private Player  aiPlayer;
	private Board   board;
	private ScrabbleDictionary dictionary;
	
	public AIController(AILevel aiLevel, Player aiPlayer, Board board, ScrabbleDictionary dictionary)
	{
		this.aiLevel = aiLevel;
		this.aiPlayer = aiPlayer;
		this.board = board;
		this.dictionary = dictionary;
	}
	
	public ArrayList<LetterTile> getSelection()
	{
		char[] selection = getWordSelection().toCharArray();
		ArrayList<LetterTile> result = new ArrayList<>();
		
		TileRack playerRack = aiPlayer.getTileRack();
		
		for(char character : selection)
		{
			for(LetterTile lt : playerRack.getLetterTiles())
			{
				if(lt.getLetter() == character)
				{
					result.add(lt);
					playerRack.removeTile(lt);
					break;
				}
			}
		}
		
		return result;
	}
	
	
	/**
	 * 
	 * @return The word selection made by the ai controller
	 * for this turn of the game. 
	 */
	private String getWordSelection()
	{
		String currentLetters = new String();
		
		for(LetterTile letterTile : aiPlayer.getTileRack().getLetterTiles())
		{
			currentLetters += letterTile.getLetter();
		}
		
		ArrayList<String> candidates = new ArrayList<>();
		
		for(String word : dictionary.getWords())
		{
			if(word.length() <= 1 || word.length() > currentLetters.length())
			{
				continue;
			}
			
			if(isValidWord(currentLetters, word))
			{
				candidates.add(word);
			}
		}
		
		if(aiLevel == AILevel.AMATEUR)
		{
			return ScrabbleUtils.getFirstCommon(candidates, dictionary);
		}
		else if(aiLevel == AILevel.ROOKIE)
		{
			return ScrabbleUtils.getBiggestCommon(candidates, dictionary);
		}
		else if(aiLevel == AILevel.INTERMEDIATE)
		{
			return ScrabbleUtils.getRandomWord(candidates);
		}
		else
		{
			return ScrabbleUtils.getBiggestWord(candidates);
		}
	}
	
	/**
	 * 
	 * @param currentLetters The String extracted from current formation of letter tiles
	 * @param word The word to be checked against the current letters
	 * @return Whether the word's characters are all present in the
	 * current letters
	 */
	private boolean isValidWord(String currentLetters, String word)
	{
		boolean charOnBoardUsed = false;
		
		for(int i = 0; i < word.length(); i++)
		{
			String charToString = String.valueOf(word.charAt(i));
			
			if(!currentLetters.contains(charToString))
			{
				if(!charOnBoardUsed && board.getValidNeutral(word.charAt(i), word) != null)
				{
					charOnBoardUsed = true;
				}
				else
				{
					return false;
				}
			}
		}
		
		if(charOnBoardUsed)
		{
			return true;
		}
		
		return false;
	}
}










