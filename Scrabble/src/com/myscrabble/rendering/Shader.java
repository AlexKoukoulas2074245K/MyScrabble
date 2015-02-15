package com.myscrabble.rendering;

import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;

import org.lwjgl.opengl.GL20;

import com.myscrabble.managers.ResourceManager;

/**
 * 
 * @author Alex Koukoulas
 * Class Description:
 * An encapsulation of an opengl shader.
 */
public class Shader
{
    public enum ShaderType
    {
        SHADING("/shaders/shading"),
        COLORING("/shaders/coloring"),
        HIGHLIGHTING("/shaders/highlight"),
        AUTO_BRIGHTNESS("/shaders/autoBrightness"),
        TRANSITION("/shaders/transition");
        
        private String fileName;
        
        private ShaderType(String name)
        {
            this.fileName = name;
        }
        
        public String getFileName()
        {
            return fileName;
        }
    }
    
    public static final int FALSE = 0;
    public static final int TRUE = 1;
    
	private static final String VERTEX_EXT = ".vs";
	private static final String FRAGMENT_EXT = ".fs";
	private static final int MAX_DEBUG_STREAM_LENGTH = 1024;
	private HashMap<String, Integer> uniformLocations;
	
	private int programHandle;
	private int vertexHandle;
	private int fragmentHandle;
	
	public Shader(ShaderType type)
	{
		createProgram();
		loadContent(type.getFileName());
		compileProgram();
		
		uniformLocations = new HashMap<>();
	}
	
	private void createProgram()
	{
		programHandle  = glCreateProgram();
		vertexHandle   = glCreateShader(GL_VERTEX_SHADER);
		fragmentHandle = glCreateShader(GL_FRAGMENT_SHADER);
	}
	
	private void loadContent(String fileName)
	{
		String vertexSource = ResourceManager.loadFileAsString(fileName + VERTEX_EXT);
		String fragmentSource = ResourceManager.loadFileAsString(fileName + FRAGMENT_EXT);
		glShaderSource(vertexHandle, vertexSource);
		glShaderSource(fragmentHandle, fragmentSource);
	}
	
	private void compileProgram()
	{
		glCompileShader(vertexHandle);
		glCompileShader(fragmentHandle);
		
		if(glGetShaderi(vertexHandle, GL_COMPILE_STATUS) == 0)
		{
			System.err.println(glGetShaderInfoLog(vertexHandle, MAX_DEBUG_STREAM_LENGTH));
		}
		
		if(glGetShaderi(fragmentHandle, GL_COMPILE_STATUS) == 0)
		{
			System.err.println(glGetShaderInfoLog(fragmentHandle, MAX_DEBUG_STREAM_LENGTH));
		}
		
		glAttachShader(programHandle, vertexHandle);
		glAttachShader(programHandle, fragmentHandle);
		
		glLinkProgram(programHandle);
		glValidateProgram(programHandle);
	}
	
	public void useProgram()
	{
		glUseProgram(programHandle);
	}
	
	public void stopProgram()
	{
		glUseProgram(0);
	}
	
	public void stopProgram(int next)
	{
	    glUseProgram(next);
	}
	
	public void setUniform3f(String uniformName, float[] values)
	{
		if(!uniformLocations.containsKey(uniformName))
		{
			uniformLocations.put(uniformName, glGetUniformLocation(programHandle, uniformName));
		}
		
		glUniform3f(uniformLocations.get(uniformName), values[0], values[1], values[2]);
	}
	
	public void setUniform3f(String uniformName, float x, float y, float z)
	{
	    setUniform3f(uniformName, new float[]{x, y, z});
	}
	
	public void setUniformf(String uniformName, float value)
	{
		if (!uniformLocations.containsKey(uniformName))
		{
			uniformLocations.put(uniformName, glGetUniformLocation(programHandle, uniformName));
		}
		
		glUniform1f(uniformLocations.get(uniformName), value);
	}
	
	public void setUniformb(String uniformName, int booleanValue)
	{
	    if(!uniformLocations.containsKey(uniformName))
	    {
	        uniformLocations.put(uniformName, glGetUniformLocation(programHandle, uniformName));
	    }
	    
	    GL20.glUniform1i(uniformLocations.get(uniformName), booleanValue);
	}
	
	public int getProgramHandle()
	{
		return programHandle;
	}
	
	public int getVertexHandle()
	{
		return vertexHandle;
	}
	
	public int getFragmentHandle()
	{
		return fragmentHandle;
	}
}
