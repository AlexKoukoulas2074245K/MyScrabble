package com.myscrabble.managers;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.openal.Audio;

import com.myscrabble.managers.SoundManager.SoundType;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A class handling sound management
 * and play back.
 */
public class SoundManager
{
	public enum SoundType
	{
		SOUND_EFFECT,
		MUSIC;
	}
	
	/* Play back constants */
	private static final float STD_GAIN = 1.0f;
	private static final float STD_PITCH = 1.0f;
	private static final float EFFECT_GAIN = STD_GAIN / 10.0f;
	private static final float EFFECT_PITCH = STD_PITCH;
	
	/* A reference to the ResourceManager for IO */
	private ResourceManager rm;
	
	/* Loaded clips that can be used for play back */
	private Map<String, Clip> loadedClips;
	
	/* Whether or not sounds enabled */
	private boolean active;
	
	public SoundManager(ResourceManager rm)
	{
		this.rm = rm;
		loadedClips = new HashMap<>();
		enable();
	}
	
	/**
	 * 
	 * @param clipName to be loaded
	 * @param type the enumerator type of the clip
	 * Overloading loadClip. Loop is by default false
	 */
	public void loadClip(String clipName, SoundType type)
	{
		loadClip(clipName, type, false);
	}
	
	/**
	 * 
	 * @param clipName to be loaded
	 * @param type the enumerator type of the clip 
	 * @param loop whether the clip is played continuously
	 */
	public void loadClip(String clipName, SoundType type, boolean loop)
	{
		if (loadedClips.containsKey(clipName))
		{
			return;
		}
		
		Audio loadedAudio = rm.loadAudio(clipName);
		
		Clip newClip = new Clip(loadedAudio, type, loop);
		
		loadedClips.put(clipName, newClip);
	}
	
	public void playClip(String clipName)
	{
		if (!loadedClips.containsKey(clipName) || !active)
		{
			return;
		}
		
		Clip targetClip = loadedClips.get(clipName);
		
		if (targetClip.type == SoundType.MUSIC)
		{
			targetClip.audioContent.playAsMusic(STD_PITCH, STD_GAIN, targetClip.loop);
		}
		else
		{
			targetClip.audioContent.playAsSoundEffect(EFFECT_PITCH, EFFECT_GAIN, targetClip.loop);	
		}
	}
	
	public void disable()
	{
		active = false;
	}
	
	public void enable()
	{
		active = true;
	}
}

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A class representing a
 * stand-alone sound or music clip
 */
class Clip
{
	public Audio audioContent;
	public SoundType type;
	public boolean loop;
	
	public Clip(Audio audioContent, SoundType type, boolean loop)
	{
		this.audioContent = audioContent;
		this.type = type;
		this.loop = loop;
	}
}
