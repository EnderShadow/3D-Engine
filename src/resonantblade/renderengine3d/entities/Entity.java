package resonantblade.renderengine3d.entities;

import java.util.Arrays;

import org.lwjgl.util.vector.Vector3f;

import resonantblade.renderengine3d.models.TexturedModel;
import resonantblade.renderengine3d.textures.ModelTexture;

public class Entity
{
	private TexturedModel model;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	private double surfaceArea;
	private double volume;
	
	private int textureIndex;
	
	public Entity(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale)
	{
		this.model = model;
		this.textureIndex = textureIndex;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		surfaceArea = model.data.getSurfaceArea() * scale * scale;
		if(Arrays.stream(model.textures).anyMatch(ModelTexture::hasTransparency))
			surfaceArea *= 2.0D;
		volume = model.data.getVolume() * scale * scale * scale;
	}
	
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale)
	{
		this(model, 0, position, rotX, rotY, rotZ, scale);
	}
	
	public double getSurfaceArea()
	{
		return surfaceArea;
	}
	
	public double getVolume()
	{
		return volume;
	}
	
	public float getTextureXOffset(int texture)
	{
		return textureIndex % model.textures[texture].getNumRows() / (float) model.textures[texture].getNumRows();
	}
	
	public float getTextureYOffset(int texture)
	{
		return textureIndex / model.textures[texture].getNumRows() / (float) model.textures[texture].getNumRows();
	}
	
	public void move(float dx, float dy, float dz)
	{
		position.x += dx;
		position.y += dy;
		position.z += dz;
	}
	
	public void rotate(float dx, float dy, float dz)
	{
		rotX += dx;
		rotY += dy;
		rotZ += dz;
	}
	
	public TexturedModel getModel()
	{
		return model;
	}
	
	public void setModel(TexturedModel model)
	{
		this.model = model;
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	public void setPosition(Vector3f position)
	{
		this.position = position;
	}
	
	public float getRotX()
	{
		return rotX;
	}
	
	public void setRotX(float rotX)
	{
		this.rotX = rotX;
	}
	
	public float getRotY()
	{
		return rotY;
	}
	
	public void setRotY(float rotY)
	{
		this.rotY = rotY;
	}
	
	public float getRotZ()
	{
		return rotZ;
	}
	
	public void setRotZ(float rotZ)
	{
		this.rotZ = rotZ;
	}
	
	public float getScale()
	{
		return scale;
	}
	
	public void setScale(float scale)
	{
		this.scale = scale;
	}
}