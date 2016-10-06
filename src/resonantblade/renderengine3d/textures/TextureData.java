package resonantblade.renderengine3d.textures;

import java.nio.ByteBuffer;

public class TextureData
{
	public final int width;
	public final int height;
	public final ByteBuffer buffer;
	
	public TextureData(ByteBuffer buffer, int width, int height)
	{
		this.buffer = buffer;
		this.width = width;
		this.height = height;
	}
}