package com.myscrabble.states;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

import com.myscrabble.buttons.BWordSelection;
import com.myscrabble.buttons.Button;
import com.myscrabble.entities.Board;
import com.myscrabble.entities.GameObject;
import com.myscrabble.entities.LetterBag;
import com.myscrabble.entities.Player;
import com.myscrabble.main.Main;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.rendering.Shader;
import com.myscrabble.util.RenderUtils;
import com.myscrabble.util.ScrabbleDictionary;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 */

public class Play extends GameState
{
	/* TEMP */
	public static final int NO_PLAYERS = 1;
	public static final int TILE_STYLE = 1;
	private static final String BG_DIR = "/board/boardBackgrounds/wood.png";
	private static final String SHADING_FACTOR_NAME = "darknessParam";
	
	/* All the GameObjects that need to be drawn and 
	 * updated on screen
	 */
	private ArrayList<GameObject> gameObjects;
	
	/* All active players */
	private ArrayList<Player> players;
	
	/* All active buttons */
	private ArrayList<Button> buttons;
	
	/* Instance of game Board */
	private Board board;
	
	/* Letter bag */
	private LetterBag letterBag;
	
	private Texture backgroundTexture;
	
	/* Dictionary Reference */
	private ScrabbleDictionary scrabbleDict;
	 
	//TODO: remove
	private Shader shader;
	private float darknessFactor;
	
	public Play(GameStateManager gsm)
	{
		super(gsm);
		
		scrabbleDict = new ScrabbleDictionary(gsm.getRes());
		board = new Board(gsm);
		letterBag = new LetterBag(gsm);
		
		players = new ArrayList<Player>();
		players.add(new Player(gsm, board, scrabbleDict, letterBag));
		
		gameObjects = new ArrayList<GameObject>();
		gameObjects.add(board);
		gameObjects.add(letterBag);
		
		buttons = new ArrayList<Button>();
		buttons.add(new BWordSelection(gsm));
		
		
		//TODO: remove
		backgroundTexture = gsm.getRes().loadTexture(BG_DIR);
		
		shader = new Shader("/shaders/shader", gsm.getRes());
		darknessFactor = 1.0f;
	}

	@Override
	public void handleInput() 
	{	
		
		for(Player player: players)
		{
			if(player.isActive())
			{
				player.handleInput();
			}
		}
		
		for(Button b : buttons)
		{
			b.handleInput();
		}
	}

	@Override
	public void update() 
	{	
		for(Player player: players)
		{
			player.update();
			
			for(Button button : buttons)
			{
				button.update(player);
			}
		}
		
		for(GameObject go: gameObjects)
		{
			go.update();
		}
	}

	@Override
	public void render() 
	{
		
		applyShading();
		
		RenderUtils.renderTexture(backgroundTexture, 0, 0, 
								  Main.getNormalDimensions()[0], 
								  Main.getNormalDimensions()[1]);
		
		
		for(Button button : buttons)
		{
			button.render();
		}
		
		for(GameObject go: gameObjects)
		{
			go.render();
		}
		
		for(Player player: players)
		{
			player.render();
		}
		
		clearShading();	
	}
	
	private void applyShading()
	{
		shader.useProgram();
		shader.setUniform3f(SHADING_FACTOR_NAME, new float[]{darknessFactor, darknessFactor, darknessFactor});
	}
	
	private void clearShading()
	{
		shader.stopProgram();
	}
}
