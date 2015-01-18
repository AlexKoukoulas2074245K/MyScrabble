package com.myscrabble.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
	
	private static final String RES_DIR = "C:/Users/alex/Pictures/scrabble/res";
	private static final String TEX_DIR = RES_DIR + "/tex";
	
	private HashMap<String, Texture> loadedTextures;
	
	public ResourceManager()
	{
		this(RES_DIR);
	}
	
	public ResourceManager(final String rootDir)
	{		
		loadedTextures = new HashMap<>();
	}
	
	/**
	 * 
	 * @param filePath of the file to open
	 * @return the file contents as a Strings
	 */
	public String loadFileAsString(final String filePath)
	{
		String fileDir = RES_DIR + filePath;
		StringBuilder result  = new StringBuilder();
		
		File file = new File(fileDir);
		
		try(BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			String line;
			
			while((line = br.readLine()) != null)
			{
				result.append(line);
				result.append(System.lineSeparator());
			}
			
			result.deleteCharAt(result.length() - 1);
			result.deleteCharAt(result.length() - 1);
			
			return result.toString();
		}
		catch (FileNotFoundException e)
		{
			System.err.println("File not found: " + fileDir);
			e.printStackTrace();
			System.exit(1);
		}
		catch (IOException e)
		{
			System.err.println("Error while reading from: " + fileDir);
			e.printStackTrace();
			System.exit(1);
		}
		
		return new String();
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
	 * @param relativePath of the textures to be loaded
	 * @return All the textures in a given directory path
	 * contained in an ArrayList.
	 */
	public ArrayList<Texture> getAllTextures(String relativePath)
	{
		ArrayList<Texture> result = new ArrayList<>();
		
		File finalDir = new File(TEX_DIR + relativePath);
		
		if(!finalDir.isDirectory())
		{
			System.err.println("False directory to extract textures: " + finalDir.getAbsolutePath());
			System.exit(1);
			return null;
		}
		
		for(File f : finalDir.listFiles())
		{
			try
			{
				result.add(TextureLoader.getTexture(STD_TEX_EXT_UPPER,
						   ResourceLoader.getResourceAsStream(f.getAbsolutePath())));
			}
			catch (IOException e)
			{
				System.err.println("Unnable to load texture: " + f.getAbsolutePath());
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		return result;
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
