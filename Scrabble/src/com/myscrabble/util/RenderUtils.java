package com.myscrabble.util;

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
	
	public static void renderRectangle(final float x, final float y, final float width, 
									   final float height, final float[] color, 
									   final boolean centerRendering)
	{
		float[][] vertices = new float[4][2];
		
		if(centerRendering)
		{
			vertices = new float[][]{{x - width / 2f, y - height / 2f}, 									  
								  	 {x + width / 2f, y - height / 2f},
								     {x + width / 2f, y + height / 2f},
								     {x - width / 2f, y + height / 2f}};
		}
		else
		{
			vertices = new float[][]{{x        , y         }, 									  
								  	 {x + width, y         },
								  	 {x + width, y + height},
								  	 {x        , y + height}};
		}
		
		glPushMatrix();
		{
			//glPushAttrib(GL_CURRENT_BIT);
			
			glColor3f(color[0], color[1], color[2]);
			
			glBegin(GL_QUADS);
			glVertex2f(vertices[0][0], vertices[0][1]);
			glVertex2f(vertices[1][0], vertices[1][1]);
			glVertex2f(vertices[2][0], vertices[2][1]);
			glVertex2f(vertices[3][0], vertices[3][1]);
			glEnd();
			
			//glPopAttrib();
		}
		glPopMatrix();
	}
	
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
	 * @param tex
	 * @param x
	 * @param y
	 * @param center: Whether or not to center
	 * the texture on the coordinates given
	 */
	public static void renderTexture(final Texture tex, final float x, final float y)
	{
		/** Creates the vertices depending on center request or not */
		float[][] quadVertices;

		
		quadVertices = new float[][]{{x                        , y                         }, 									  
							  		 {x + tex.getTextureWidth(), y                         },
							  		 {x + tex.getTextureWidth(), y + tex.getTextureHeight()},
							  		 {x                        , y + tex.getTextureHeight()}};
	
		
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
}
