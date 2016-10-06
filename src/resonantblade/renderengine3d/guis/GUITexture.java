package resonantblade.renderengine3d.guis;

import org.lwjgl.util.vector.Vector2f;

public class GUITexture
{
	public final int textureID;
	public final Vector2f position;
	public final Vector2f scale;
	
	public GUITexture(int textureID, Vector2f position, Vector2f scale)
	{
		this.textureID = textureID;
		this.position = position;
		this.scale = scale;
	}
}