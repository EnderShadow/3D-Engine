package resonantblade.renderengine3d.textures;

public class ModelTexture
{
	public static final ModelTexture NULL_TEXTURE = new ModelTexture(0);
	
	public final int textureID;
	private float shineDamper = 1.0F;
	private float reflectivity = 0.0F;
	
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	
	private int numRows = 1;
	
	public ModelTexture(int textureID)
	{
		this.textureID = textureID;
	}
	
	public void setNumRows(int numRows)
	{
		this.numRows = numRows;
	}
	
	public int getNumRows()
	{
		return numRows;
	}
	
	public void setShineDamper(float value)
	{
		shineDamper = value;
	}
	
	public float getShineDamper()
	{
		return shineDamper;
	}
	
	public void setReflectivity(float value)
	{
		reflectivity = value;
	}
	
	public float getReflectivity()
	{
		return reflectivity;
	}
	
	public void setTransparency(boolean transparent)
	{
		hasTransparency = transparent;
	}
	
	public boolean hasTransparency()
	{
		return hasTransparency;
	}
	
	public void useFakeLighting(boolean fakeLighting)
	{
		useFakeLighting = fakeLighting;
	}
	
	public boolean usesFakeLighting()
	{
		return useFakeLighting;
	}
}