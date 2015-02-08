package com.myscrabble.managers;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
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
	/* Extension constants */
	public static final String STD_TEX_EXT = ".png";
	private static final String STD_TEX_EXT_CAPS = "PNG";
	private static final String STD_SFX_EXT = ".wav";
	private static final String STD_SFX_EXT_CAPS = "WAV";
	private static final String INVALID_EXT = ".db";
	private static final String FONT_EXT = ".ttf";
	
	/* Directory constants */
	private static final String RES_DIR  = "res";
	private static final String TEX_DIR  = RES_DIR + "/tex";
	private static final String FONT_DIR = "/fonts/";
	private static final String SFX_DIR  = RES_DIR + "/sfx/";
	
	private HashMap<String, Texture> loadedTextures;
	private HashMap<String, TrueTypeFont> loadedFonts;
	
	public ResourceManager()
	{
		this(RES_DIR);
	}
	
	public ResourceManager(final String rootDir)
	{	    
		loadedTextures = new HashMap<>();
		loadedFonts = new HashMap<>();
	}
	
	/**
	 * 
	 * @param filePath of the file to open
	 * @return the file contents as a Strings
	 * (NOTE) static to be accessible to non game objects 
	 * such as shader objects and ScrabbleDictionary objects.
	 */
	public static String loadFileAsString(final String filePath)
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
		    if(f.getName().endsWith(INVALID_EXT))
		    {
		        continue;
		    }
		    
			try
			{
				Texture tex = TextureLoader.getTexture(STD_TEX_EXT_CAPS, 
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
		    if(f.getName().endsWith(INVALID_EXT))
		    {
		        continue;
		    }
		    
			try
			{
				result.add(TextureLoader.getTexture(STD_TEX_EXT_CAPS,
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
	
	public Audio loadAudio(final String fileName)
	{
		try
		{
			return AudioLoader.getAudio(STD_SFX_EXT_CAPS, 
				   ResourceLoader.getResourceAsStream(SFX_DIR + fileName + STD_SFX_EXT));
		} 
		catch (IOException e)
		{
			System.err.println("Sound file not found: " + SFX_DIR + fileName + STD_SFX_EXT);
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
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
			if(fileName.endsWith(STD_TEX_EXT))
			{
				tex = TextureLoader.getTexture(STD_TEX_EXT_CAPS, 
					  ResourceLoader.getResourceAsStream(TEX_DIR + fileName));
			}
			else
			{
				tex = TextureLoader.getTexture(STD_TEX_EXT_CAPS,
					  ResourceLoader.getResourceAsStream(TEX_DIR + fileName + STD_TEX_EXT));
			}
			
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
	
    /**
     * 
     * @param fontName The fontName to be searched in the fonts directory
     * @param fontSize The desired font size
     * @param antiAlias Whether anti-aliasing will be used or not
     * @return The corresponding openGL-context-friendly TrueType font that
     * will be used for String rendering
     */
    public TrueTypeFont loadFont(String fontName, float fontSize, boolean antiAlias)
    {
        
    	if(loadedFonts.containsKey(fontName))
    	{
    		return loadedFonts.get(fontName);
    	}
    	
        TrueTypeFont result = null;
        
        try(InputStream in = getClass().getResourceAsStream(FONT_DIR + fontName + FONT_EXT))
        {
            long startTime = System.nanoTime();
            Font awtFont = Font.createFont(Font.TRUETYPE_FONT, in); //<-- This operation takes about 5 seconds
            System.out.println("Operation took: " + (System.nanoTime() - startTime) / 1000000);
            
            awtFont = awtFont.deriveFont(fontSize);
            result = new TrueTypeFont(awtFont, antiAlias);
            
        }
        catch (IOException e) 
        {
            System.err.println("Font not found: \"" + fontName + "\"");
            e.printStackTrace();
            System.exit(1);
        }
        catch (FontFormatException e) 
        {
            System.err.println("Error while formatting font: \"" + fontName + "\"");
            e.printStackTrace();
        }
        
        loadedFonts.put(fontName, result);
        
        
        return result;
    }
}
