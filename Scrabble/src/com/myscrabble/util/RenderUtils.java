package com.myscrabble.util;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import static org.lwjgl.opengl.GL11.*;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * A class containing helpful rendering methods
 */

public class RenderUtils 
{
	public static final float MAX_RGB_VAL = 255f;
	
	/**
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return The normalized openGL accepted
	 * color representation given the 3 r, g, b
	 * parameters of the original color.
	 */
	public static float[] getGLColor(final int r, final int g, final int b)
	{
		return new float[]{ r / MAX_RGB_VAL , g / MAX_RGB_VAL, b / MAX_RGB_VAL };
	}
    
	/**
	 * 
	 * @param x offset to start rendering
	 * @param y offset to start rendering
	 * @param width of rectangle
	 * @param height of rectangle
	 * <br>Renders a rectangle(colour-less) on the specified coordinates and
	 * with the desired width and height 
	 */
	public static void renderRectangle(final float x, final float y, final float width, final float height)
	{
	    glBegin(GL_QUADS);
	    glVertex2f(x        , y         );
	    glVertex2f(x + width, y         );
	    glVertex2f(x + width, y + height);
	    glVertex2f(x        , y + width );
	    glEnd();
	}
	
	/**
	 * 
	 * @param tex Texture to be rendered
	 * @param x x-offset to start rendering
	 * @param y y-offset to start rendering
	 * <br>Does not specify width and height so the rendering width and height
	 * will default to the original texture's dimensions.
	 */
	public static void renderTexture(final Texture tex, final float x, final float y)
	{
		renderTexture(tex, x, y, tex.getTextureWidth(), tex.getTextureHeight());
	}
	
	/**
	 * 
	 * @param tex Texture to be rendered
	 * @param x x-offset to start rendering
	 * @param y y-offset to start rendering
	 * @param width the final width of the texture(defaults to the texture's own width if left blank)
	 * @param height the final height of the texture(default to the texture's own height if left blank)
	 * Does not specify center rendering.
	 * false will be passed to the core method for the centerRendering parameter. 
	 */
	public static void renderTexture(final Texture tex, final float x, final float y, final float width, final float height)
	{
		renderTexture(tex, x, y, width, height, false);
	}
	
	/**
	 * 
	 * @param tex Texture to be rendered
	 * @param x x-offset to start rendering
	 * @param y y-offset to start rendering
	 * @param width the final width of the texture(defaults to the texture's own width if left blank)
	 * @param height the final height of the texture(default to the texture's own height if left blank)
	 * @param centerRendering whether or not the texture will be rendered with its center as the origin
	 * or the top left corner as the origin
	 */
	public static void renderTexture(final Texture tex, final float x, final float y, final float width, final float height, boolean centerRendering)
	{
		/** Creates the vertices depending on center request or not */
		float[][] quadVertices;

		if(centerRendering)
		{
			quadVertices = new float[][]{{x - width / 2, y - height / 2}, 									  
								  		 {x + width / 2, y - height / 2},
								  		 {x + width / 2, y + height / 2},
								  		 {x - width / 2, y + height / 2}};
		}
		else
		{
			quadVertices = new float[][]{{x        , y         }, 									  
								  		 {x + width, y         },
								  		 {x + width, y + height},
								  		 {x        , y + height}};
		}
		
		/** Renders the texture on top of the rectangle */
		glPushMatrix();
		{
			tex.bind();
			
			glBegin(GL_QUADS);
			{
				glTexCoord2f(0.0f, 0.0f);
				glVertex2f(quadVertices[0][0], quadVertices[0][1]);
				
				glTexCoord2f(1.0f, 0.0f);
				glVertex2f(quadVertices[1][0], quadVertices[1][1]);
				
				glTexCoord2f(1.0f, 1.0f);
				glVertex2f(quadVertices[2][0], quadVertices[2][1]);
				
				glTexCoord2f(0.0f, 1.0f);
				glVertex2f(quadVertices[3][0], quadVertices[3][1]);
			}
			glEnd();
		}
		glPopMatrix();
	}
	
	/**
	 * 
	 * @param color the input color
	 * @param factor the factor that the result will
	 * be multiplied with
	 * @return the given color multiplied by the
	 * given factor
	 */
	public static Color blend(Color color, float factor)
	{
	    return new Color(color.r * factor, color.g * factor, color.b * factor);
	}
	
	/**
	 * 
	 * @param deg To rotate the current matrix
	 * @param x percentage of rotation on x axis
	 * @param y percentage of rotation on y axis
	 * @param z percentage of rotation on z axis
	 */
	public static void rotatef(float deg, float x, float y, float z)
	{
	    glRotatef(deg, x, y, z);
	}
	
	/**
	 * OpenGL wrapper method for pushMatrix()
	 */
	public static void pushMatrix()
	{
	    glPushMatrix();
	}
	
	/**
	 * OpenGL wrapper method for popMatrix()
	 */
	public static void popMatrix()
	{
	    glPopMatrix();
	}
}
