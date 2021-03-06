package com.myscrabble.managers;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
	/* Public constants */
	public static final String DOT_REGEX         = "\\.";
	public static final String STD_TEX_EXT       = ".png";
	public static final String SAV_DIR           = "save";
	
	/* Private Extension constants */
	private static final String STD_TEX_EXT_CAPS = "PNG";
	private static final String STD_SFX_EXT      = ".wav";
	private static final String STD_SFX_EXT_CAPS = "WAV";
	private static final String INVALID_EXT      = ".db";
	private static final String FONT_EXT         = ".ttf";
	
	/* Private Directory constants */

	private static final String RES_DIR  = "res";
	private static final String TEX_DIR  = RES_DIR + "/tex";
	private static final String FONT_DIR = "/fonts/";
	private static final String SFX_DIR  = RES_DIR + "/sfx/";
	
	private HashMap<String, Texture> loadedTextures;
	private HashMap<String, Font> loadedFonts;
	private static final int ENCRYPT_KEY = 24;
	
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
	 * @param f to write on
	 * @param content to write on file
	 * (NOTE) static to be accessible to non game objects
	 * such as UserProfile objects.
	 */
	public static void writeToFile(File f, String content)
	{
	    try (BufferedWriter bw = new BufferedWriter(new FileWriter(f)))
	    {	        
	        bw.write(encrypt(content));
	    }
	    catch (IOException e) 
	    {
	        System.err.println("Failed to write on file: " + f.getAbsolutePath());
            e.printStackTrace();
        }
	}
	
	private static String encrypt(String content)
	{
	    StringBuilder result = new StringBuilder();
	    
	    for(int i = 0; i < content.length(); i++)
	    {
	        result.append((char)((int)content.charAt(i) + ENCRYPT_KEY));
	    }
	    
	    return result.toString();
	}
	
	private static String decrypt(String content)
	{
	    StringBuilder result = new StringBuilder();
	    
	    for(int i = 0; i < content.length(); i++)
	    {
	        result.append((char)((int)content.charAt(i) - ENCRYPT_KEY));
	    }
	    
	    return result.toString();
	}
	
	/**
	 * 
	 * @param dirPath to extract the file names from
	 * @return all the filenames from a specified
	 * directory
	 */
	public static String[] getFileNames(String dirPath)
	{
		File dir = new File(dirPath);
		
		if (!dir.isDirectory())
		{
			System.out.println("Directory not found: " + dirPath);
			return null;
		}
		
		File[] listFiles = dir.listFiles();
		String[] fileNames = new String[listFiles.length];
		
		for (int i = 0;
			 i < fileNames.length;
			 i++)
		{
			fileNames[i] = listFiles[i].getName();
		}
		
		return fileNames;
	}
	
	public static String loadFileAsString(final String filePath)
    {
        return loadFileAsString(filePath, false, false);
    }
	
	/**
	 * 
	 * @param filePath of the file to open
	 * @return the file contents as a Strings
	 * (NOTE) static to be accessible to non game objects 
	 * such as shader objects, user profiles and ScrabbleDictionary objects.
	 */
	public static String loadFileAsString(final String filePath, boolean absolutePath, boolean decryptionNeeded)
	{
	    String fileDir = absolutePath ? filePath : RES_DIR + filePath;
	    
		StringBuilder result  = new StringBuilder();
		
		File file = new File(fileDir);
		
		try(BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			String line;
			
			while((line = br.readLine()) != null)
			{
			    if(decryptionNeeded)
			    {
			        result.append(decrypt(line));
			    }
			    else
			    {
			        result.append(line);
			    }
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
    		Font cacheFont = loadedFonts.get(fontName);
    		cacheFont = cacheFont.deriveFont(fontSize);
    		return new TrueTypeFont(cacheFont, antiAlias);
    	}
    	
        TrueTypeFont result = null;
        Font awtFont = null;
        
        try(InputStream in = getClass().getResourceAsStream(FONT_DIR + fontName + FONT_EXT))
        {
            long startTime = System.nanoTime();
            awtFont = Font.createFont(Font.TRUETYPE_FONT, in); //<-- This operation takes about 5 seconds
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
        
        loadedFonts.put(fontName, awtFont);
        
        
        return result;
    }
}
