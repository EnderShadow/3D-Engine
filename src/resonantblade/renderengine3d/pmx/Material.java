package resonantblade.renderengine3d.pmx;

import java.nio.ByteBuffer;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import static resonantblade.renderengine3d.pmx.ByteBufferUtils.*;

public class Material
{
	private static final int NO_CULL = 1;
	private static final int GROUND_SHADOW = 1 << 1;
	private static final int DRAW_SHADOW = 1 << 2;
	private static final int RECIEVE_SHADOW = 1 << 3;
	private static final int HAS_EDGE = 1 << 4;
	private static final int VERTEX_COLOR = 1 << 5;
	private static final int POINT_DRAWING = 1 << 6;
	private static final int LINE_DRAWING = 1 << 7;
	
	public final String nameLocal, nameUniversal, metaData;
	public final Vector4f diffuseColor, edgeColor;
	public final Vector3f specularColor, ambientColor;
	public final float specularStrength, edgeScale;
	public final byte drawingFlags, environmentBlendMode, toonReference;
	public final int textureIndex, environmentIndex, toonValue, faceCount;
	
	public Material(ByteBuffer data, Header header)
	{
		nameLocal = getString(data, header.textEncoding);
		nameUniversal = getString(data, header.textEncoding);
		diffuseColor = getVec4(data);
		specularColor = getVec3(data);
		specularStrength = data.getFloat();
		ambientColor = getVec3(data);
		drawingFlags = data.get();
		edgeColor = getVec4(data);
		edgeScale = data.getFloat();
		switch(header.textureIndexSize)
		{
		case 1:
			textureIndex = data.get();
			environmentIndex = data.get();
			break;
		case 2:
			textureIndex = data.getShort();
			environmentIndex = data.getShort();
			break;
		case 4:
			textureIndex = data.getInt();
			environmentIndex = data.getInt();
			break;
		default:
			throw new IllegalStateException("Invalid texture index size: " + header.textureIndexSize);
		}
		environmentBlendMode = data.get();
		toonReference = data.get();
		if(toonReference == 0)
		{
			switch(header.textureIndexSize)
			{
			case 1:
				toonValue = data.get();
				break;
			case 2:
				toonValue = data.getShort();
				break;
			case 4:
				toonValue = data.getInt();
				break;
			default:
				throw new IllegalStateException("Invalid texture index size: " + header.textureIndexSize);
			}
		}
		else if(toonReference == 1)
		{
			toonValue = data.get();
		}
		else
		{
			throw new IllegalStateException("Unknown toon reference value: " + toonReference);
		}
		metaData = getString(data, header.textEncoding);
		faceCount = data.getInt() / 3;
	}
	
	public boolean noCull()
	{
		return (drawingFlags & NO_CULL) == 1;
	}
	
	public boolean groundShadow()
	{
		return (drawingFlags & GROUND_SHADOW) == 1;
	}
	
	public boolean drawShadow()
	{
		return (drawingFlags & DRAW_SHADOW) == 1;
	}
	
	public boolean recieveShadow()
	{
		return (drawingFlags & RECIEVE_SHADOW) == 1;
	}
	
	public boolean hasEdge()
	{
		return (drawingFlags & HAS_EDGE) == 1;
	}
	
	public boolean vertexColor()
	{
		return (drawingFlags & VERTEX_COLOR) == 1;
	}
	
	public boolean pointDrawing()
	{
		return (drawingFlags & POINT_DRAWING) == 1;
	}
	
	public boolean lineDrawing()
	{
		return (drawingFlags & LINE_DRAWING) == 1;
	}
}