package com.myscrabble.uicomponents;

import java.awt.Rectangle;

import org.newdawn.slick.opengl.Texture;

import com.myscrabble.main.Main;
import com.myscrabble.managers.GameStateManager;
import com.myscrabble.managers.MouseManager;
import com.myscrabble.managers.ResourceManager;
import com.myscrabble.rendering.Shader;
import com.myscrabble.rendering.Shader.ShaderType;
import com.myscrabble.util.RenderUtils;

public class MainOption
{
	public enum OptionName
	{
		PLAY("/menu/play.png", new float[]{Main.getCenterDimensions()[0], 300.0f}),
		CUSTOMIZE("/menu/customize.png", new float[]{Main.getCenterDimensions()[0], 380.0f}),
		EXIT("/menu/exit.png", new float[]{Main.getCenterDimensions()[0], 460.0f});
		
		public String imagePath;
		public float[] pos;
		
		private OptionName(String imagePath, float[] pos)
		{
			this.imagePath = imagePath;
			this.pos = pos;
		}
	}
	
	private OptionName optionName;
	private Texture texture;
	private Shader shader;
	private float[] pos;
	private boolean highlighted;
	private boolean selected;
	
	public MainOption(GameStateManager gsm, OptionName optionName)
	{
		this.optionName = optionName;
		loadTexture(gsm.getRes());
		pos = optionName.pos;
		highlighted = false;
		shader = new Shader(ShaderType.HIGHLIGHTING);
	}
	
	private void loadTexture(ResourceManager res)
	{
		texture = res.loadTexture(optionName.imagePath);
	}
	
	public void handleInput()
	{
		highlighted = isHighlighted();
	
		selected = highlighted && MouseManager.isButtonPressed(MouseManager.LEFT_BUTTON);
	}
	
	public void render()
	{
		if(highlighted)
		{
			shader.useProgram();
			shader.setUniformb("fullWhite", Shader.TRUE);
			shader.setUniformb("highlighted", Shader.TRUE);
			shader.setUniform3f("darknessFactor", new float[]{1.0f, 1.0f, 1.0f});
		}
		RenderUtils.renderTexture(texture, pos[0], pos[1], 
				texture.getTextureWidth(), texture.getTextureHeight(), true);
		
		shader.stopProgram();
	}
	
	public boolean isSelected()
	{
		return selected;
	}
	
	public boolean isHighlighted()
	{
		Rectangle optionRect = new Rectangle((int)pos[0] - texture.getTextureWidth() / 2, 
										     (int)pos[1] - texture.getTextureHeight() / 2,
										          texture.getTextureWidth(),
										          texture.getTextureHeight());

		return optionRect.contains(MouseManager.getX(), MouseManager.getY());
	}
}
