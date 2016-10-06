package resonantblade.renderengine3d.pmx;

import java.nio.ByteBuffer;

public class Face
{
	public final int vertexIndex1, vertexIndex2, vertexIndex3;
	
	public Face(ByteBuffer data, Header header)
	{
		switch(header.vertexIndexSize)
		{
		case 1:
			vertexIndex1 = data.get() & 0xFF;
			vertexIndex2 = data.get() & 0xFF;
			vertexIndex3 = data.get() & 0xFF;
			break;
		case 2:
			vertexIndex1 = data.getShort() & 0xFFFF;
			vertexIndex2 = data.getShort() & 0xFFFF;
			vertexIndex3 = data.getShort() & 0xFFFF;
			break;
		case 4:
			vertexIndex1 = data.getInt();
			vertexIndex2 = data.getInt();
			vertexIndex3 = data.getInt();
			break;
		default:
			throw new IllegalStateException("Unknown vertex index size: " + header.vertexIndexSize);
		}
	}
}