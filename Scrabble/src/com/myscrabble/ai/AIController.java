package com.myscrabble.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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
	
	/* Holds the previous word selections that need to be avoided */
	private HashSet<String> blacklist;

	private int nextLetterTileIndex;
	
	/* A check done in higher AI levels instead of passing
	 * attempting to find a word where a prefix can be placed
	 */
	private boolean prefixCheck;
	
	/* The prefix addition was succesful */
	private boolean prefixSuccesful;
	
	public AIController(AILevel aiLevel, Player aiPlayer, Board board, ScrabbleDictionary dictionary)
	{
		this.aiLevel = aiLevel;
		this.aiPlayer = aiPlayer;
		this.board = board;
		this.dictionary = dictionary;

		aiState = AIState.WORD_SELECTION;
		nextLetterTileIndex = 0;
		missingTilePerWord = new HashMap<>();
		
		prefixCheck = false;
		prefixSuccesful = false;
		
		blacklist = new HashSet<>();
	}
	
	/**
	 * 
	 * @return the calculated points for the
	 * last ai selection recorded
	 */
	public int calculatePoints()
	{
	    return ScrabbleUtils.calculatePoints(lastAISelection, board.getTilemap());
	}
	
	/**
	 * Makes a move respective to the AI controlled 
	 * player's level.
	 */
	public void update()
	{	
		if(aiState == AIState.WORD_SELECTION)
		{
		    prefixCheck = false;
		    prefixSuccesful = false;
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
		}
	}
	
	private void removeNextLetterTile()
	{
		if(!prefixCheck && (finalMissingTile.getAIMovement() == Movement.NONE || finalMissingTile == null))
		{
			aiState = AIState.WORD_SELECTION;
			return;
		}

		LetterTile nextLetterTile = lastAISelection.get(nextLetterTileIndex);
		
		if(aiPlayer.getTileRack().contains(nextLetterTile))
		{
			int ltIndex = aiPlayer.getTileRack().getTileIndex(nextLetterTile);
			aiPlayer.getTileRack().removeTile(nextLetterTile);
			aiPlayer.getTileRack().resetAllFlagsAI(ltIndex);
			aiPlayer.getTileRack().pushTiles(Direction.LEFT, ltIndex + 1);
			
			if(!prefixCheck)
			{
			    positionTile(nextLetterTile);
			}
			else
			{
			    board.addLetterTileAI(nextLetterTile);
			    prefixSuccesful = true;
			}
		}
		
		nextLetterTileIndex++;
	}
	
	/**
	 * 
	 * @param lt LetterTile to be positioned.
	 * <br>
	 * Positions the tile to its respective position in the 
	 * game board based on the only neutral tile's position
	 * and index.
	 */
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
	
	/**
	 * A validation check and a prompt to search
	 * for prefixes as a last attempt to find a valid
	 * move. Also resets the core variables for the next
	 * round.
	 */
	private void validateLetterTiles()
	{
	    if(finalMissingTile != null)
	    {
    		finalMissingTile.setAIDirectionFreedom(null);
    		finalMissingTile.setAIMovement(Movement.NONE);
    		finalMissingTile = null;
    		aiState = AIState.FINISHING;
	    }
	    else
	    {
	        cancelChoice();
	    }
	        
	    missingTilePerWord.clear();
        nextLetterTileIndex = 0;
        blacklist.clear();
	}
	
	/**
	 * Determines whether the AI level is high 
	 * enough to search for a prefix-addition as 
	 * a valid move, instead of passing this turn.
	 */
	private void cancelChoice()
	{
	    if((aiLevel == AILevel.HARD || aiLevel == AILevel.INTERMEDIATE) && !prefixCheck)
	    {
	        prefixAdditionCheck();
	    }
	    else
	    {
	        if(!prefixSuccesful)
	        {
	            aiState = AIState.PASS;
	        }
	        else
	        {
	            aiState = AIState.FINISHING;
	        }
	    }
	}
	
	/**
	 * Searches every word played, against every
	 * possible prefix that ScrabbleDictionary provides.
	 * If a valid combination from the word and the prefix is 
	 * found, the ai tile rack contains the appropriate tiles and
	 * enough empty tiles are next to the word so that the prefix
	 * tiles can be placed, this combination is added to possible
	 * candidates.
	 */
	private void prefixAdditionCheck()
	{
	    prefixCheck = true;
	    
	    if(board.getRegisteredWords().size() < 1)
	    {
	        aiState = AIState.PASS;
	        cancelChoice();
	    }
	    
	    ArrayList<LetterTile[]> prefixCandidates = new ArrayList<>();
	    
	    for(LetterTile[] tiles : board.getRegisteredWords())
	    {
	        StringBuilder sb = new StringBuilder();
	        
	        for(LetterTile lt : tiles)
	        {
	            sb.append(lt.getLetter());
	        }
	        
	        Iterator<String> prefixIter = dictionary.getCommonPrefixes();
	        
	        while(prefixIter.hasNext())
	        {
	            String prefix = prefixIter.next();

	            if(dictionary.wordExists(sb.toString() + prefix) && 
	               ScrabbleUtils.formationContains(aiPlayer.getTileRack().getLetterTiles(), prefix))
	            {
	                System.out.println(sb.toString() + prefix);
	                addPrefixCandidate(prefixCandidates, tiles, prefix);
	            }
	        }
	    }
	    
	    
	    if(prefixCandidates.size() < 1)
	    {
	        aiState = AIState.PASS;
	        cancelChoice();
	    }
	    else
	    {
	        LetterTile[] decision = prefixCandidates.get(0);
	        lastAISelection.clear();
	        
	        for(LetterTile lt : decision)
	        {
	            lastAISelection.add(lt);
	        }
	        
	        aiState = AIState.RACK_UPDATE;
	    }
	}
	
	/**
	 * 
	 * @param prefixCandidates list of candidates found
	 * @param tiles all the neutral tiles that the target word is composed of
	 * @param prefix the prefix to add to the target word
	 * <br>
	 * Gets the direction of the target word, determines whether there is enough
	 * space after it for the prefix tiles, positions the new tiles(that the prefix is composed of)
	 * and adds them to the final result.
	 */
	private void addPrefixCandidate(ArrayList<LetterTile[]> prefixCandidates, LetterTile[] tiles, String prefix)
	{
	    int tileDist = (int)ScrabbleUtils.xDistanceBetween(tiles[0], tiles[1]);
	    
	    Movement wordDir = tileDist == Tile.TILE_SIZE ? Movement.HORIZONTAL : Movement.VERTICAL;
	    
	    int freedomSpaceReq = prefix.length() + 1;
	    int currentFreedom  = 0;
	    
	    Tile lastHolder = board.getTilemap().getLetterTileHolder(tiles[tiles.length - 1]);
	    
	    if(wordDir == Movement.HORIZONTAL)
	    {
	        for(int col = lastHolder.getCol(); col <= col + freedomSpaceReq; col++)
	        {
	            if(board.getTilemap().isTileEmpty(col, lastHolder.getRow()))
	            {
	                currentFreedom++;
	            }
	        }
	    }
	    else if(wordDir == Movement.VERTICAL)
	    {
	        for(int row = lastHolder.getCol(); row <= row + freedomSpaceReq; row++)
            {
                if(board.getTilemap().isTileEmpty(lastHolder.getCol(), row))
                {
                    currentFreedom++;
                }
            }
	    }
	    
	    if(currentFreedom >= freedomSpaceReq)
	    {
	        LetterTile[] result = new LetterTile[tiles.length + prefix.length()];
	        
	        for(int i = 0; i < tiles.length; i++)
	        {
	            result[i] = tiles[i];
	        }
	        
	        for(int i = 0; i < prefix.length(); i++)
	        {
	            result[tiles.length + i] = ScrabbleUtils.getFirstOccurence(prefix.charAt(i), aiPlayer.getTileRack().getLetterTiles());
	            
	            if(wordDir == Movement.HORIZONTAL)
	            {
	                result[tiles.length + i].setX(tiles[tiles.length + i - 1].getX() + Tile.TILE_SIZE);
	                result[tiles.length + i].setY(tiles[tiles.length + i - 1].getY());
	            }
	            else if(wordDir == Movement.VERTICAL)
	            {
	                result[tiles.length + i].setX(tiles[tiles.length + i - 1].getX());
	                result[tiles.length + i].setY(tiles[tiles.length + i - 1].getY() + Tile.TILE_SIZE);
	            }
	        }
	        
	        prefixCandidates.add(result);
	    }
	}
	
	/**
	 * 
	 * @return Selection of LetterTiles based
	 * on the word currently chosen
	 */
	private ArrayList<LetterTile> getSelection()
	{	
	    String wordSelection = getWordSelection();
	    
	    blacklist.add(wordSelection);
	    
	    finalMissingTile = missingTilePerWord.get(wordSelection);
	    
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
		
		Iterator<String> dictWords = dictionary.getWords();
		
		while(dictWords.hasNext())
		{
		    String word = dictWords.next();
		    
			if(word.length() <= 1 || word.length() > currentLetters.length())
			{
				continue;
			}
			
			if(isValidWord(currentLetters, word))
			{
				candidates.add(word);
			}
		}
		
		for(String word : blacklist)
		{
			if(candidates.contains(word))
			{
				candidates.remove(word);
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
				    missingTilePerWord.remove(word);
					return false;
				}
			}
			else
			{
			    removeCharFromList(charList, word.charAt(i));
			}
		}
		
		return charOnBoardUsed;
	}
	
	private void removeCharFromList(List<Character> charList, char character)
	{
	    charList.remove(charList.indexOf(character));
	}
	
	public AIState getState()
	{
		return aiState;
	}
	
	public ArrayList<LetterTile> getLastSelection()
	{
	    return lastAISelection;
	}
	
	public void setState(AIState aiState)
	{
		this.aiState = aiState;
	}
}