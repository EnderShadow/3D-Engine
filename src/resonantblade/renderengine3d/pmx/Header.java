package resonantblade.renderengine3d.pmx;

import static resonantblade.renderengine3d.pmx.ByteBufferUtils.getString;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Header
{
	public static final byte[] EXPECTED_SIGNATURE = {0x50, 0x4D, 0x58, 0x20};
	public final byte[] signature;
	public final float version;
	public final byte[] globals;
	
	public final byte textEncodingRaw;
	public final Charset textEncoding;
	public final byte additionalVec4Count;
	public final byte vertexIndexSize;
	public final byte textureIndexSize;
	public final byte materialIndexSize;
	public final byte boneIndexSize;
	public final byte morphIndexSize;
	public final byte rigidBodyIndexSize;
	
	public final String modelNameLocal;
	public final String modelNameUniversal;
	public final String commentsLocal;
	public final String commentsUniversal;
	
	public Header(ByteBuffer data)
	{
		signature = new byte[4];
		data.get(signature);
		if(!Arrays.equals(signature, EXPECTED_SIGNATURE)) // "PMX "
			throw new IllegalArgumentException("Invalid signature: '" + new String(signature) + "', " + Arrays.toString(signature));
		version = data.getFloat();
		if(version < 2.0F)
			throw new IllegalArgumentException("Invalid version: " + version);
		globals = new byte[data.get()];
		data.get(globals);
		textEncodingRaw = globals[0];
		switch(textEncodingRaw)
		{
		case 0:
			textEncoding = Charset.forName("UTF-16LE");
			break;
		case 1:
			textEncoding = Charset.forName("UTF-8");
			break;
		default:
			throw new IllegalArgumentException("Invalid text encoding: " + textEncodingRaw);
		}
		additionalVec4Count = globals[1];
		if(additionalVec4Count < 0 || additionalVec4Count > 4)
			throw new IllegalArgumentException("Invalid vec4 count");
		// TODO change the 8 to globalsCount depending on extended globals
		for(int i = 2; i < 8; i++)
			if(globals[i] != 1 && globals[i] != 2 && globals[i] != 4)
				throw new IllegalArgumentException("Invalid index size " + globals[i] + " at index " + i);
		vertexIndexSize = globals[2];
		textureIndexSize = globals[3];
		materialIndexSize = globals[4];
		boneIndexSize = globals[5];
		morphIndexSize = globals[6];
		rigidBodyIndexSize = globals[7];
		
		modelNameLocal = getString(data, textEncoding);
		modelNameUniversal = getString(data, textEncoding);
		commentsLocal = getString(data, textEncoding);
		commentsUniversal = getString(data, textEncoding);
	}
}