package ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.myscrabble.entities.Board;
import com.myscrabble.entities.LetterTile;
import com.myscrabble.entities.LetterTile.Direction;
import com.myscrabble.entities.LetterTile.Movement;
import com.myscrabble.entities.Player;
import com.myscrabble.entities.Tile;
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
	
	public enum AIState
	{
		WORD_SELECTION,
		RACK_UPDATE,
		VALIDATING,
		FINISHING,
		PASS;
	}
	
	/* The ability of this AI controller */
	private AILevel aiLevel;
	
	/* The current state of this controller */
	private AIState aiState;
	
	/* References to core objects */
	private Player  aiPlayer;
	private Board   board;
	private ScrabbleDictionary dictionary;
	
	/* The missing letter tiles per word that is are candidates */
	private HashMap<String,LetterTile> missingTilePerWord; 
	
	/* The final selection for missing letter tile */
	private LetterTile finalMissingTile;
	
	/* Reference to the last word selection done by this AI controller */
	private ArrayList<LetterTile> lastAISelection;
	

	private int nextLetterTileIndex;
	
	public AIController(AILevel aiLevel, Player aiPlayer, Board board, ScrabbleDictionary dictionary)
	{
		this.aiLevel = aiLevel;
		this.aiPlayer = aiPlayer;
		this.board = board;
		this.dictionary = dictionary;

		aiState = AIState.WORD_SELECTION;
		nextLetterTileIndex = 0;
		missingTilePerWord = new HashMap<>();
	}
	
	/**
	 * 
	 * @return the calculated points for the
	 * last ai selection recorded
	 */
	public int calculatePoints()
	{
	    if(aiState == AIState.PASS)
	    {
	        return 0;
	    }
	    else
	    {
	        return ScrabbleUtils.calculatePoints(lastAISelection, board.getTilemap());
	    }
	}
	
	/**
	 * Makes a move respective to the AI controlled 
	 * player's level.
	 */
	public void update()
	{	
		if(aiState == AIState.WORD_SELECTION)
		{
			lastAISelection = getSelection();
			
			aiState = AIState.RACK_UPDATE;
		}
		else if(aiState == AIState.RACK_UPDATE)
		{	
			if(aiPlayer.getTileRack().tilesAreIdle() &&
			   nextLetterTileIndex != lastAISelection.size())
			{
				removeNextLetterTile();
			}
			
			if(nextLetterTileIndex == lastAISelection.size() && 
			   aiPlayer.getTileRack().tilesAreIdle())
			{
				aiState = AIState.VALIDATING;
			}
			
			aiPlayer.getTileRack().update();
		}
		else if(aiState == AIState.VALIDATING)
		{
			validateLetterTiles();
			aiState = AIState.FINISHING;
		}
	}
	
	private void removeNextLetterTile()
	{
		LetterTile nextLetterTile = lastAISelection.get(nextLetterTileIndex);
		
		if(aiPlayer.getTileRack().contains(nextLetterTile))
		{
			int ltIndex = aiPlayer.getTileRack().getTileIndex(nextLetterTile);
			aiPlayer.getTileRack().removeTile(nextLetterTile);
			aiPlayer.getTileRack().resetAllFlagsAI(ltIndex);
			aiPlayer.getTileRack().pushTiles(Direction.LEFT, ltIndex + 1);
			
			positionTile(nextLetterTile);
		}
		
		nextLetterTileIndex++;
	}
	
	private void positionTile(LetterTile lt)
	{
		float boardLetterX = finalMissingTile.getX();
		float boardLetterY = finalMissingTile.getY();
		int boardLetterIndex = lastAISelection.indexOf(finalMissingTile);
		int currentIndex = lastAISelection.indexOf(lt);
		
		if(finalMissingTile.getAIMovement() == Movement.HORIZONTAL)
		{
			lastAISelection.get(currentIndex).setX(boardLetterX + (currentIndex - boardLetterIndex) * Tile.TILE_SIZE);
			lastAISelection.get(currentIndex).setY(boardLetterY);
		}
		else
		{
			lastAISelection.get(currentIndex).setX(boardLetterX);
			lastAISelection.get(currentIndex).setY(boardLetterY + (currentIndex - boardLetterIndex) * Tile.TILE_SIZE);
		}
		
		board.addLetterTileAI(lastAISelection.get(currentIndex));
	}
	
	private void validateLetterTiles()
	{		
		finalMissingTile.setAIDirectionFreedom(null);
		finalMissingTile.setAIMovement(Movement.NONE);
		finalMissingTile = null;
		missingTilePerWord.clear();
		nextLetterTileIndex = 0;
	}
	
	private void cancelTurn()
	{
	    aiState = AIState.PASS;
	}
	
	private ArrayList<LetterTile> getSelection()
	{	
	    String wordSelection = getWordSelection();
	    
	    finalMissingTile = missingTilePerWord.get(wordSelection);
	    
	    if(finalMissingTile == null ||
	       finalMissingTile.getAIMovement() == Movement.NONE)
	    {
	        cancelTurn();
	    }
	    
		char[] selection = wordSelection.toCharArray();
		
		ArrayList<LetterTile> result = new ArrayList<>();
		
		TileRack playerRack = aiPlayer.getTileRack();
		
		for(char character : selection)
		{
		    if(character == finalMissingTile.getLetter() && !
		       result.contains(finalMissingTile))
		    {
		        result.add(finalMissingTile);
		        continue;
		    }
			for(LetterTile lt : playerRack.getLetterTiles())
			{
				if(lt.getLetter() == character && !result.contains(lt))
				{
					result.add(lt);
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
			return ScrabbleUtils.getRandomCommon(candidates, dictionary);
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
	    List<Character> charList = new ArrayList<Character>();
	    
	    for(int i = 0; i < currentLetters.length(); i++)
	    {
	    	charList.add(currentLetters.charAt(i));
	    }
	    
		boolean charOnBoardUsed = false;
		
		for(int i = 0; i < word.length(); i++)
		{
			if(!charList.contains(word.charAt(i)))
			{
				if(!charOnBoardUsed && board.getValidNeutral(word.charAt(i), word) != null)
				{
					charOnBoardUsed = true;
					missingTilePerWord.put(word, board.getValidNeutral(word.charAt(i), word));
				}
				else
				{
					return false;
				}
			}
			else
			{
			    removeCharFromList(charList, word.charAt(i));
			}
		}
		
		if(charOnBoardUsed)
		{
			return true;
		}
		
		return false;
	}
	
	private void removeCharFromList(List<Character> charList, char character)
	{
	    charList.remove(charList.indexOf(character));
	}
	
	public AIState getState()
	{
		return aiState;
	}
	
	public void setState(AIState aiState)
	{
		this.aiState = aiState;
	}
}