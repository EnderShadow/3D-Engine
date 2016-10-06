package resonantblade.renderengine3d.pmx;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;

import static resonantblade.renderengine3d.pmx.ByteBufferUtils.*;

public class PMXData
{
	public static PMXData readFromFile(File file) throws IOException
	{
		return new PMXData(Files.readAllBytes(file.toPath()));
	}
	
	public Header header;
	public Vertex[] vertices;
	public Face[] faces;
	public String[] textures;
	public Material[] materials;
	
	private PMXData(byte[] dataArray)
	{
		ByteBuffer dataBuffer = ByteBuffer.wrap(dataArray).order(ByteOrder.LITTLE_ENDIAN);
		header = new Header(dataBuffer);
		readVertices(dataBuffer);
		readFaces(dataBuffer);
		readTextures(dataBuffer);
		readMaterials(dataBuffer);
		// TODO read the rest of the PMX data
		// https://gist.github.com/felixjones/f8a06bd48f9da9a4539f
	}
	
	private void readVertices(ByteBuffer data)
	{
		vertices = new Vertex[data.getInt()];
		for(int i = 0; i < vertices.length; i++)
			vertices[i] = new Vertex(data, header);
	}
	
	private void readFaces(ByteBuffer data)
	{
		faces = new Face[data.getInt() / 3];
		for(int i = 0; i < faces.length; i++)
			faces[i] = new Face(data, header);
	}
	
	public void readTextures(ByteBuffer data)
	{
		textures = new String[data.getInt()];
		for(int i = 0; i < textures.length; i++)
			textures[i] = getString(data, header.textEncoding);
	}
	
	public void readMaterials(ByteBuffer data)
	{
		materials = new Material[data.getInt()];
		for(int i = 0; i < materials.length; i++)
			materials[i] = new Material(data, header);
	}
}