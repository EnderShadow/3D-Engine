package resonantblade.renderengine3d.pmx;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class ByteBufferUtils
{
	public static String getString(ByteBuffer data, Charset encoding)
	{
		byte[] buffer = new byte[data.getInt()];
		data.get(buffer);
		return new String(buffer, encoding);
	}
	
	public static Vector2f getVec2(ByteBuffer data)
	{
		return new Vector2f(data.getFloat(), data.getFloat());
	}
	
	public static Vector3f getVec3(ByteBuffer data)
	{
		return new Vector3f(data.getFloat(), data.getFloat(), data.getFloat());
	}
	
	public static Vector4f getVec4(ByteBuffer data)
	{
		return new Vector4f(data.getFloat(), data.getFloat(), data.getFloat(), data.getFloat());
	}
}