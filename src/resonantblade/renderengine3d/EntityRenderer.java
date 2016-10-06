package resonantblade.renderengine3d;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import resonantblade.renderengine3d.entities.Entity;
import resonantblade.renderengine3d.models.RawModel;
import resonantblade.renderengine3d.models.TexturedModel;
import resonantblade.renderengine3d.shaders.StaticShader;
import resonantblade.renderengine3d.textures.ModelTexture;
import resonantblade.renderengine3d.util.MathUtils;

public class EntityRenderer
{
	private StaticShader shader;
	
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix)
	{
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Map<TexturedModel, List<Entity>> entities)
	{
		for(TexturedModel tm : entities.keySet())
		{
			List<Entity> batch = entities.get(tm);
			int faceOffset = 0;
			for(int i = 0; i < tm.textures.length; i++)
			{
				prepareTexturedModel(tm, i);
				for(Entity ent : batch)
				{
					prepareEntity(ent, i);
					GL11.glDrawElements(GL11.GL_TRIANGLES, tm.data.textureFaceBounds[i] * 3, GL11.GL_UNSIGNED_INT, faceOffset);
					faceOffset += tm.data.textureFaceBounds[i] * 3 * 4;
				}
				unbindTexturedModel();
			}
		}
	}
	
	private void prepareTexturedModel(TexturedModel model, int textureIndex)
	{
		RawModel rawModel = model.model;
		GL30.glBindVertexArray(rawModel.vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.textures[textureIndex];
		shader.loadNumRows(texture.getNumRows());
		if(texture.hasTransparency())
			MasterRenderer.disableCulling();
		shader.loadFakeLightingVariable(texture.usesFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureID);
	}
	
	private void unbindTexturedModel()
	{
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareEntity(Entity entity, int textureIndex)
	{
		Matrix4f transformationMatrix = MathUtils.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(entity.getTextureXOffset(textureIndex), entity.getTextureYOffset(textureIndex));
	}
}