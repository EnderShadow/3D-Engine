package resonantblade.renderengine3d.skybox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import resonantblade.renderengine3d.DisplayManager;
import resonantblade.renderengine3d.entities.Camera;
import resonantblade.renderengine3d.shaders.ShaderProgram;
import resonantblade.renderengine3d.util.MathUtils;

public class SkyboxShader extends ShaderProgram
{
	private static final String VERTEX_FILE = "src/resonantblade/renderengine3d/skybox/skyboxVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/resonantblade/renderengine3d/skybox/skyboxFragmentShader.txt";
	
	private static final float ROTATE_SPEED = 1.0F;
	
	private float rotation = 0.0F;
	
	public SkyboxShader()
	{
		super(VERTEX_FILE, FRAGMENT_FILE, new String[]{
				"projectionMatrix",
				"viewMatrix",
				"fogColor",
				"cubeMap",
				"cubeMap2",
				"blendFactor"
		});
	}
	
	public void connectTextureUnits()
	{
		loadInt("cubeMap", 0);
		loadInt("cubeMap2", 1);
	}
	
	public void loadBlendFactor(float blendFactor)
	{
		loadFloat("blendFactor", blendFactor);
	}
	
	public void loadFogColor(float r, float g, float b)
	{
		loadVector3f("fogColor", new Vector3f(r, g, b));
	}
	
	public void loadProjectionMatrix(Matrix4f matrix)
	{
		loadMatrix("projectionMatrix", matrix);
	}
	
	public void loadViewMatrix(Camera camera)
	{
		Matrix4f matrix = MathUtils.createViewMatrix(camera);
		matrix.m30 = 0.0F;
		matrix.m31 = 0.0F;
		matrix.m32 = 0.0F;
		rotation = (rotation + ROTATE_SPEED * DisplayManager.getFrameTimeSeconds()) % 360.0F;
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), matrix, matrix);
		loadMatrix("viewMatrix", matrix);
	}
	
	@Override
	protected void bindAttributes()
	{
		super.bindAttribute(0, "position");
	}
}