package resonantblade.renderengine3d.shaders;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public abstract class ShaderProgram
{
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	protected String[] uniformLocationNames;
	protected TObjectIntHashMap<String> uniformLocations;
	
	public ShaderProgram(String vertexFile, String fragmentFile, String[] uniformLocationNames)
	{
		this.uniformLocationNames = uniformLocationNames;
		uniformLocations = new TObjectIntHashMap<String>();
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}
	
	protected void getAllUniformLocations()
	{
		for(int i = 0; i < uniformLocationNames.length; i++)
			uniformLocations.put(uniformLocationNames[i], getUniformLocation(uniformLocationNames[i]));
	}
	
	protected int getUniformLocation(String uniformName)
	{
		return GL20.glGetUniformLocation(programID, uniformName);
	}
	
	public void start()
	{
		GL20.glUseProgram(programID);
	}
	
	public void stop()
	{
		GL20.glUseProgram(0);
	}
	
	public void cleanUp()
	{
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}
	
	protected abstract void bindAttributes();
	
	protected void bindAttribute(int attribute, String variableName)
	{
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}
	
	protected void loadInt(String location, int value)
	{
		GL20.glUniform1i(uniformLocations.get(location), value);
	}
	
	protected void loadFloat(String location, float value)
	{
		GL20.glUniform1f(uniformLocations.get(location), value);
	}
	
	protected void loadVector3f(String location, Vector3f vector)
	{
		GL20.glUniform3f(uniformLocations.get(location), vector.x, vector.y, vector.z);
	}
	
	protected void loadVector2f(String location, Vector2f vector)
	{
		GL20.glUniform2f(uniformLocations.get(location), vector.x, vector.y);
	}
	
	protected void loadBoolean(String location, boolean value)
	{
		GL20.glUniform1i(uniformLocations.get(location), value ? -1 : 0);
	}
	
	protected void loadMatrix(String location, Matrix4f matrix)
	{
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(uniformLocations.get(location), false, matrixBuffer);
	}
	
	private static int loadShader(String file, int type)
	{
		StringBuilder shaderSource = new StringBuilder();
		
		try(BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			String line;
			while((line = br.readLine()) != null)
				shaderSource.append(line).append('\n');
		}
		catch(Exception e)
		{
			System.err.println("ERROR: could not reader shader file");
			e.printStackTrace();
			System.exit(-1);
		}
		
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
		{
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("ERROR: could not compiler shader");
			System.exit(-1);
		}
		
		return shaderID;
	}
	
	protected static String[] concat(String[]... stras)
	{
		int len = 0;
		for(String[] stra : stras)
			len += stra.length;
		String[] res = new String[len];
		int index = 0;
		for(String[] stra : stras)
			for(String str : stra)
				res[index++] = str;
		return res;
	}
	
	protected static String[] formatStringArray(int lim, String... strs)
	{
		String[] stra = new String[lim * strs.length];
		for(int i = 0; i < lim; i++)
			for(int j = 0; j < strs.length; j++)
				stra[i * strs.length + j] = String.format(strs[j], i);
		return stra;
	}
}