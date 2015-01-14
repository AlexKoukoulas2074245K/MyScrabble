package com.myscrabble.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import com.myscrabble.util.Animation;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A class that handles File I/O
 * and resource management
 */

public class ResourceManager 
{
	public static final String STD_TEX_EXT = ".png";
	private static final String STD_TEX_EXT_UPPER = "PNG";
	
	private static final String DEFAULT_DIR = "C:/Users/alex/Pictures/scrabble/res";
	private static final String TEX_DIR = DEFAULT_DIR + "/tex";
	
	
	private HashMap<String, Texture> loadedTextures;
	
	public ResourceManager()
	{
		this(DEFAULT_DIR);
	}
	
	public ResourceManager(final String rootDir)
	{		
		loadedTextures = new HashMap<>();
	}
	
	/**
	 * 
	 * @param dirPath The path to the directory containing the keyframes
	 * for the animation
	 * @param aniDelay The delay supplied for the animation
	 * @return A new animation containing the keyframes requested
	 * and the delay
	 */
	public Animation loadAnimation(final String dirPath, final int aniDelay)
	{
		File dir = new File(TEX_DIR + dirPath);
		
		ArrayList<Texture> keyFrames = new ArrayList<>();
		
		for(File f : dir.listFiles())
		{
			try
			{
				Texture tex = TextureLoader.getTexture(STD_TEX_EXT_UPPER, 
					          ResourceLoader.getResourceAsStream(f.getAbsolutePath()));
				
				keyFrames.add(tex);
			}
			catch (IOException e)
			{
				System.err.println("Animation directory not found: " + dir.getAbsolutePath());
				e.printStackTrace();
				System.exit(1);
			}
							
		}
		
		return new Animation(keyFrames, aniDelay, true);
	}
	
	/**
	 * 
	 * @param fileName. The file name expected here
	 * is the file name relative to the the 
	 * current "tex" directory.  
	 * @return loaded Texture
	 * 
	 * Checks for already loaded asset first
	 * and the proceeds to load the texture
	 */
	public Texture loadTexture(final String fileName)
	{
		if(loadedTextures.containsKey(fileName))
		{
			return loadedTextures.get(fileName);
		}
		
		Texture tex = null;
		try 
		{
			tex = TextureLoader.getTexture(STD_TEX_EXT_UPPER, 
			      ResourceLoader.getResourceAsStream(TEX_DIR + fileName));
			
			loadedTextures.put(fileName, tex);
		}
		catch (IOException e) 
		{
			System.err.println("Missing asset: " + fileName);
			e.printStackTrace();
			System.exit(1);
		}
		
		return tex;
	}
}
