package com.myscrabble.fx;

import com.myscrabble.entities.LetterTile.Direction;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A simple struct holding information about
 * a Letter block. (Used in PassAnimation class)
 */
public class LetterBlock
{
	public Direction direction;
	public char character;
	public float[] pos;
	public float[] vel;
	public boolean jumped;
	
	public LetterBlock(char character, float[] pos)
	{
		this.character = character;
		this.pos = pos;
		vel = new float[2];
		direction = Direction.DOWN;
		jumped = false;
	}
}
