package resonantblade.renderengine3d.guis;

import org.lwjgl.util.vector.Matrix4f;

import resonantblade.renderengine3d.shaders.ShaderProgram;

public class GUIShader extends ShaderProgram
{
	private static final String VERTEX_FILE = "src/resonantblade/renderengine3d/guis/guiVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/resonantblade/renderengine3d/guis/guiFragmentShader.txt";
	
	public GUIShader()
	{
		super(VERTEX_FILE, FRAGMENT_FILE, new String[]{
				"transformationMatrix"	
		});
	}
	
	public void loadTransformation(Matrix4f matrix)
	{
		loadMatrix("transformationMatrix", matrix);
	}
	
	@Override
	protected void bindAttributes()
	{
		super.bindAttribute(0, "position");
	}
}