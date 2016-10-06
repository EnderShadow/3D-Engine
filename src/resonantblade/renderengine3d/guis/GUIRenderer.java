package resonantblade.renderengine3d.guis;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import resonantblade.renderengine3d.Loader;
import resonantblade.renderengine3d.models.RawModel;
import resonantblade.renderengine3d.util.MathUtils;

public class GUIRenderer
{
	private final RawModel quad;
	private GUIShader shader;
	
	public GUIRenderer(Loader loader)
	{
		float[] positions = {-1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, -1.0F};
		quad = loader.loadToVAO(positions, 2);
		shader = new GUIShader();
	}
	
	public void render(List<GUITexture> guis)
	{
		shader.start();
		GL30.glBindVertexArray(quad.vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for(GUITexture gui : guis)
		{
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.textureID);
			Matrix4f matrix = MathUtils.createTransformationMatrix(gui.position, gui.scale);
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.numVertices);
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	public void cleanUp()
	{
		shader.cleanUp();
	}
}