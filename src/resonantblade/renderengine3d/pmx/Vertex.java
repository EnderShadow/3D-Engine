package resonantblade.renderengine3d.pmx;

import java.nio.ByteBuffer;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import static resonantblade.renderengine3d.pmx.ByteBufferUtils.*;

public class Vertex
{
	public final Vector3f position, normal;
	public final Vector2f uv;
	public final Vector4f[] vec4s;
	public final byte weightDeformType;
	public final WeightDeform weightDeform;
	public final float edgeScale;
	
	public Vertex(ByteBuffer data, Header header)
	{
		position = getVec3(data);
		normal = getVec3(data);
		uv = getVec2(data);
		vec4s = new Vector4f[header.additionalVec4Count];
		for(int i = 0; i < vec4s.length; i++)
			vec4s[i] = getVec4(data);
		weightDeformType = data.get();
		switch(weightDeformType)
		{
		case 0:
			weightDeform = new BDEF1(data, header);
			break;
		case 1:
			weightDeform = new BDEF2(data, header);
			break;
		case 2:
			weightDeform = new BDEF4(data, header);
			break;
		case 3:
			weightDeform = new SDEF(data, header);
			break;
		case 4:
			weightDeform = new QDEF(data, header);
			break;
		default:
			throw new IllegalStateException("Unknown weight deform type: " + weightDeformType);
		}
		edgeScale = data.getFloat();
	}
	
	public interface WeightDeform {}
	
	public class BDEF1 implements WeightDeform
	{
		public final int boneIndex;
		public final float boneWeight = 1.0F;
		
		public BDEF1(ByteBuffer data, Header header)
		{
			switch(header.boneIndexSize)
			{
			case 1:
				boneIndex = data.get();
				break;
			case 2:
				boneIndex = data.getShort();
				break;
			case 4:
				boneIndex = data.getInt();
				break;
			default:
				throw new IllegalStateException("Unknown bone index size");
			}
		}
	}
	
	public class BDEF2 implements WeightDeform
	{
		public final int boneIndex1, boneIndex2;
		public final float boneWeight1, boneWeight2;
		
		public BDEF2(ByteBuffer data, Header header)
		{
			switch(header.boneIndexSize)
			{
			case 1:
				boneIndex1 = data.get();
				boneIndex2 = data.get();
				break;
			case 2:
				boneIndex1 = data.getShort();
				boneIndex2 = data.getShort();
				break;
			case 4:
				boneIndex1 = data.getInt();
				boneIndex2 = data.getInt();
				break;
			default:
				throw new IllegalStateException("Unknown bone index size");
			}
			boneWeight1 = data.getFloat();
			boneWeight2 = 1.0F - boneWeight1;
		}
	}
	
	public class BDEF4 implements WeightDeform
	{
		public final int boneIndex1, boneIndex2, boneIndex3, boneIndex4;
		public final float boneWeight1, boneWeight2, boneWeight3, boneWeight4;
		
		public BDEF4(ByteBuffer data, Header header)
		{
			switch(header.boneIndexSize)
			{
			case 1:
				boneIndex1 = data.get();
				boneIndex2 = data.get();
				boneIndex3 = data.get();
				boneIndex4 = data.get();
				break;
			case 2:
				boneIndex1 = data.getShort();
				boneIndex2 = data.getShort();
				boneIndex3 = data.getShort();
				boneIndex4 = data.getShort();
				break;
			case 4:
				boneIndex1 = data.getInt();
				boneIndex2 = data.getInt();
				boneIndex3 = data.getInt();
				boneIndex4 = data.getInt();
				break;
			default:
				throw new IllegalStateException("Unknown bone index size");
			}
			boneWeight1 = data.getFloat();
			boneWeight2 = data.getFloat();
			boneWeight3 = data.getFloat();
			boneWeight4 = data.getFloat();
		}
	}
	
	public class SDEF implements WeightDeform
	{
		public final int boneIndex1, boneIndex2;
		public final float boneWeight1, boneWeight2;
		public final Vector3f C, R0, R1;
		
		public SDEF(ByteBuffer data, Header header)
		{
			switch(header.boneIndexSize)
			{
			case 1:
				boneIndex1 = data.get();
				boneIndex2 = data.get();
				break;
			case 2:
				boneIndex1 = data.getShort();
				boneIndex2 = data.getShort();
				break;
			case 4:
				boneIndex1 = data.getInt();
				boneIndex2 = data.getInt();
				break;
			default:
				throw new IllegalStateException("Unknown bone index size");
			}
			boneWeight1 = data.getFloat();
			boneWeight2 = 1.0F - boneWeight1;
			C = getVec3(data);
			R0 = getVec3(data);
			R1 = getVec3(data);
		}
	}
	
	public class QDEF implements WeightDeform
	{
		public final int boneIndex1, boneIndex2, boneIndex3, boneIndex4;
		public final float boneWeight1, boneWeight2, boneWeight3, boneWeight4;
		
		public QDEF(ByteBuffer data, Header header)
		{
			switch(header.boneIndexSize)
			{
			case 1:
				boneIndex1 = data.get();
				boneIndex2 = data.get();
				boneIndex3 = data.get();
				boneIndex4 = data.get();
				break;
			case 2:
				boneIndex1 = data.getShort();
				boneIndex2 = data.getShort();
				boneIndex3 = data.getShort();
				boneIndex4 = data.getShort();
				break;
			case 4:
				boneIndex1 = data.getInt();
				boneIndex2 = data.getInt();
				boneIndex3 = data.getInt();
				boneIndex4 = data.getInt();
				break;
			default:
				throw new IllegalStateException("Unknown bone index size");
			}
			boneWeight1 = data.getFloat();
			boneWeight2 = data.getFloat();
			boneWeight3 = data.getFloat();
			boneWeight4 = data.getFloat();
		}
	}
}