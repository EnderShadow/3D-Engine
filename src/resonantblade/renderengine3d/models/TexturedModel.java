package resonantblade.renderengine3d.models;

import resonantblade.renderengine3d.textures.ModelTexture;

public class TexturedModel
{
	public final RawModel model;
	public final ModelData data;
	public final ModelTexture[] textures;
	
	public TexturedModel(RawModel model, ModelData data, ModelTexture... textures)
	{
		this.model = model;
		this.data = data;
		this.textures = textures;
	}
}