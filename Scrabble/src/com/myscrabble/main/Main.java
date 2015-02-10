package com.myscrabble.main;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glViewport;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.myscrabble.managers.GameStateManager;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * The Entry point of the program.
 * Initializes the core functionality needed
 * to launch and play the game.
 */

public class Main 
{
	private static boolean finished;
	private static WindowConfiguration wconfig;
	private static GameStateManager gsm;
	
	public static void main(String[] args)
	{
		initWindow();
		initGL();
		initCoreEntities();
		gameLoop();
		cleanUp();
	}
	
	/**
	 * Game Termination Request
	 */
	public static void endGame()
	{
		Main.finished = true;
	}
	
	public static float[] getNormalDimensions()
	{
		return new float[]{ Display.getWidth(), Display.getHeight() };
	}
	
	public static float[] getCenterDimensions()
	{
		return new float[]{ Display.getWidth() / 2f, Display.getHeight() / 2f };
	}
	
	/**
	 * Initializes an Lwjgl window and OpenGL context for use.
	 * Uses a default window configuration available
	 * @invariable default window configuration
	 */
	private static void initWindow()
	{
		wconfig = new WindowConfiguration();
		
		try
		{
			Display.setTitle(wconfig.getTitle());
			Display.setResizable(wconfig.getResizable());
			Display.setDisplayMode(new DisplayMode(wconfig.getWidth(), wconfig.getHeight()));
			Display.create();
			
			System.out.println(glGetString(GL_VERSION));
		}
		catch(LWJGLException e)
		{
			System.err.println("Failed to initialize OpenGL window");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void initGL()
	{
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glViewport(0, 0, wconfig.getWidth(), wconfig.getHeight());
		
		glMatrixMode(GL_PROJECTION);
		{
			glLoadIdentity();
			glOrtho(0, wconfig.getWidth(), wconfig.getHeight(), 0, -1, 1);
		}
		glMatrixMode(GL_MODELVIEW);
	}
	
	private static void initCoreEntities()
	{
		gsm = new GameStateManager();
	}
	
	private static void gameLoop()
	{
		finished = false;
	
		while(!Display.isCloseRequested() && !finished)
		{
			/* Game Input Handling */
			gsm.handleInput();
			
			/* Game Update Func */
			gsm.update();
			
			/* Game Rendering */
			glClear(GL_COLOR_BUFFER_BIT);
			
			gsm.render();
			
			Display.update();
			
			/* Frame Cap */
			Display.sync(wconfig.getFps());
			
			/* VSync */
			//Display.setVSyncEnabled(wconfig.getVsync());
		}
	}
	
	private static void cleanUp()
	{
	    gsm.getCurrentState().getCurrentUser().save();
		Display.destroy();
		AL.destroy();
	}
	
}
