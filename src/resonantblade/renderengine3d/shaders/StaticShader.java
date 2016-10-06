package resonantblade.renderengine3d.shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import resonantblade.renderengine3d.entities.Camera;
import resonantblade.renderengine3d.entities.Light;
import resonantblade.renderengine3d.util.MathUtils;

public class StaticShader extends ShaderProgram
{
	private static final int MAX_LIGHTS = 16;
	
	private static final String VERTEX_FILE = "src/resonantblade/renderengine3d/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "src/resonantblade/renderengine3d/shaders/fragmentShader.txt";
	
	public StaticShader()
	{
		super(VERTEX_FILE, FRAGMENT_FILE, concat(new String[]{
				"transformationMatrix",
				"projectionMatrix",
				"viewMatrix",
				"shineDamper",
				"reflectivity",
				"useFakeLighting",
				"skyColor",
				"numRows",
				"offset",
				"numLights"
		}, formatStringArray(MAX_LIGHTS, "lightPosition[%d]", "lightColor[%d]", "attenuation[%d]")));
	}
	
	@Override
	protected void bindAttributes()
	{
		bindAttribute(0, "position");
		bindAttribute(1, "textureCoords");
		bindAttribute(2, "normal");
	}
	
	public void loadNumRows(float numRows)
	{
		loadFloat("numRows", numRows);
	}
	
	public void loadOffset(float x, float y)
	{
		loadVector2f("offset", new Vector2f(x, y));
	}
	
	public void loadSkyColorVariable(float r, float g, float b)
	{
		loadVector3f("skyColor", new Vector3f(r, g, b));
	}
	
	public void loadFakeLightingVariable(boolean useFakeLighting)
	{
		loadBoolean("useFakeLighting", useFakeLighting);
	}
	
	public void loadShineVariables(float damper, float reflectivity)
	{
		loadFloat("shineDamper", damper);
		loadFloat("reflectivity", reflectivity);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix)
	{
		loadMatrix("transformationMatrix", matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix)
	{
		loadMatrix("projectionMatrix", matrix);
	}
	
	public void loadViewMatrix(Camera camera)
	{
		loadMatrix("viewMatrix", MathUtils.createViewMatrix(camera));
	}
	
	public void loadLights(List<Light> lights)
	{
		int numLights = Math.min(lights.size(), MAX_LIGHTS);
		for(int i = 0; i < numLights; i++)
		{
			loadVector3f("lightPosition[" + i + "]", lights.get(i).getPosition());
			loadVector3f("lightColor[" + i + "]", lights.get(i).getColor());
			loadVector3f("attenuation[" + i + "]", lights.get(i).getAttenuation());
		}
		loadInt("numLights", numLights);
	}
}