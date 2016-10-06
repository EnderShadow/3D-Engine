package resonantblade.renderengine3d;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import resonantblade.renderengine3d.models.ModelData;
import resonantblade.renderengine3d.models.RawModel;
import resonantblade.renderengine3d.textures.TextureData;

public class Loader
{
	private List<Integer> vaos = new LinkedList<Integer>();
	private List<Integer> vbos = new LinkedList<Integer>();
	private List<Integer> textures = new LinkedList<Integer>();
	
	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices)
	{
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();
		
		return new RawModel(vaoID, indices.length);
	}
	
	public RawModel loadToVAO(ModelData md)
	{
		return loadToVAO(md.positions, md.textureCoords, md.normals, md.indices);
	}
	
	public RawModel loadToVAO(float[] positions, int dimensions)
	{
		int vaoID = createVAO();
		storeDataInAttributeList(0, dimensions, positions);
		unbindVAO();
		
		return new RawModel(vaoID, positions.length / dimensions);
	}
	
	public int loadTexture(String file)
	{
		Texture texture = null;
		try
		{
			String extension = file.substring(file.lastIndexOf(".") + 1);
			String format = "PNG";
			if(extension.equalsIgnoreCase("png"))
				format = "PNG";
			else if(extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg"))
				format = "JPEG";
			else if(extension.equalsIgnoreCase("bmp"))
				format = "BMP";
			texture = TextureLoader.getTexture(format, new FileInputStream("res/" + file));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.5F);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	
	public int loadTexture(BufferedImage image)
	{
		Texture texture = null;
		
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "PNG", baos);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			
			texture = TextureLoader.getTexture("PNG", bais);
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.5F);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	
	public void cleanUp()
	{
		for(int i : vaos)
			GL30.glDeleteVertexArrays(i);
		for(int i : vbos)
			GL15.glDeleteBuffers(i);
		for(int i : textures)
			GL11.glDeleteTextures(i);
	}
	
	public int loadCubeMap(String[] textureFiles)
	{
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		for(int i = 0; i < textureFiles.length; i++)
		{
			TextureData td = decodeTextureFile("res/" + textureFiles[i]);
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, td.width, td.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, td.buffer);
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		textures.add(texID);
		return texID;
	}
	
	private TextureData decodeTextureFile(String filename)
	{
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try
		{
			FileInputStream fis = new FileInputStream(filename);
			PNGDecoder decoder = new PNGDecoder(fis);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			fis.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Tried to load texture " + filename + " but failed");
			System.exit(-1);
		}
		return new TextureData(buffer, width, height);
	}
	
	private int createVAO()
	{
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	private void storeDataInAttributeList(int attributeIndex, int floatsPerStride, float[] data)
	{
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = toFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeIndex, floatsPerStride, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void unbindVAO()
	{
		GL30.glBindVertexArray(0);
	}
	
	private void bindIndicesBuffer(int[] indices)
	{
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = toIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		//GL20.glVertexAttribPointer(attributeIndex, 3, GL11.GL_INT, false, 0, 0);
		//GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	private IntBuffer toIntBuffer(int[] data)
	{
		return (IntBuffer) BufferUtils.createIntBuffer(data.length).put(data).flip();
	}
	
	private FloatBuffer toFloatBuffer(float[] data)
	{
		return (FloatBuffer) BufferUtils.createFloatBuffer(data.length).put(data).flip();
	}
}