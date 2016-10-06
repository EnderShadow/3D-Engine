package resonantblade.renderengine3d;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import resonantblade.renderengine3d.models.RawModel;
import resonantblade.renderengine3d.shaders.TerrainShader;
import resonantblade.renderengine3d.terrain.Terrain;
import resonantblade.renderengine3d.textures.TerrainTexturePack;
import resonantblade.renderengine3d.util.MathUtils;

public class TerrainRenderer
{
	private TerrainShader shader;
	
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix)
	{
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}
	
	public void render(List<Terrain> terrains)
	{
		for(Terrain t : terrains)
		{
			prepareTerrain(t);
			loadModelMatrix(t);
			GL11.glDrawElements(GL11.GL_TRIANGLES, t.getModel().numVertices, GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
	}
	
	private void prepareTerrain(Terrain terrain)
	{
		RawModel rawModel = terrain.getModel();
		GL30.glBindVertexArray(rawModel.vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		bindTextures(terrain);
		shader.loadShineVariables(1.0F, 0.0F);
	}
	
	private void bindTextures(Terrain terrain)
	{
		TerrainTexturePack texturePack = terrain.getTerrainTexturePack();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.backgroundTexture.textureID);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.rTexture.textureID);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.gTexture.textureID);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.bTexture.textureID);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().textureID);
	}
	
	private void unbindTexturedModel()
	{
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	private void loadModelMatrix(Terrain terrain)
	{
		Matrix4f transformationMatrix = MathUtils.createTransformationMatrix(new Vector3f(terrain.getX(), 0.0F, terrain.getZ()), 0.0F, 0.0F, 0.0F, 1.0F);
		shader.loadTransformationMatrix(transformationMatrix);
	}
}