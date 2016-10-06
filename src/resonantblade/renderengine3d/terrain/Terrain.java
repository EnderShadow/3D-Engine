package resonantblade.renderengine3d.terrain;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import resonantblade.renderengine3d.Loader;
import resonantblade.renderengine3d.models.RawModel;
import resonantblade.renderengine3d.textures.TerrainTexture;
import resonantblade.renderengine3d.textures.TerrainTexturePack;
import resonantblade.renderengine3d.util.MathUtils;

public class Terrain
{
	public static final float SIZE = 800.0F;
	private static final float MAX_HEIGHT = 40.0F;
	private static final float MAX_PIXEL_COLOR = (float) (256 << 16);
	
	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	private float[][] heights;
	
	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMap)
	{
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		x = gridX * SIZE;
		z = gridZ * SIZE;
		model = generateTerrain(loader, heightMap);
	}
	
	public float getHeight(float worldX, float worldZ)
	{
		float terrainX = worldX - x;
		float terrainZ = worldZ - z;
		float gridSquareSize = SIZE / (heights.length - 1.0F);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		if(gridX < 0 || gridX >= heights.length - 1 || gridZ < 0 || gridZ >= heights.length - 1)
			return 0.0F;
		
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		if(xCoord <= 1 - zCoord)
			return MathUtils.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		return MathUtils.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
	}
	
	private RawModel generateTerrain(Loader loader, String heightMap)
	{
		BufferedImage image = null;
		try
		{
			image = ImageIO.read(new File("res/" + heightMap));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		int VERTEX_COUNT = image.getHeight();
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		for(int i = 0; i < VERTEX_COUNT; i++)
			for(int j = 0; j < VERTEX_COUNT; j++)
				heights[j][i] = getHeight(j, i, image);
		
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
		int vertexPointer = 0;
		for(int i = 0; i < VERTEX_COUNT; i++)
		{
			for(int j = 0; j < VERTEX_COUNT; j++)
			{
				vertices[vertexPointer * 3] = (float) j / (VERTEX_COUNT - 1) * SIZE;
				vertices[vertexPointer * 3 + 1] = heights[j][i];
				vertices[vertexPointer * 3 + 2] = (float) i / (VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = calculateNormal(j, i, VERTEX_COUNT);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoords[vertexPointer * 2] = (float) j / (VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / (VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz = 0; gz < VERTEX_COUNT - 1; gz++)
		{
			for(int gx = 0; gx < VERTEX_COUNT - 1; gx++)
			{
				int topLeft = gz * VERTEX_COUNT + gx;
				int topRight = topLeft + 1;
				int bottomLeft = (gz + 1) * VERTEX_COUNT + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	private Vector3f calculateNormal(int x, int z, int maxIndex)
	{
		float heightL = getHeight2(x - 1, z, maxIndex);
		float heightR = getHeight2(x + 1, z, maxIndex);
		float heightD = getHeight2(x, z - 1, maxIndex);
		float heightU = getHeight2(x, z + 1, maxIndex);
		return (Vector3f) new Vector3f(heightL - heightR, 2.0F, heightD - heightU).normalise();
	}
	
	private float getHeight(int x, int z, BufferedImage image)
	{
		if(image == null || x < 0 || x >= image.getWidth() || z < 0 || z >= image.getHeight())
			return 0.0F;
		return (image.getRGB(x, z) + MAX_PIXEL_COLOR / 2.0F) * 2.0F / MAX_PIXEL_COLOR * MAX_HEIGHT;
	}
	
	private float getHeight2(int x, int z, int maxIndex)
	{
		if(x < 0 || x >= maxIndex || z < 0 || z >= maxIndex)
			return 0.0F;
		return heights[z][x];
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getZ()
	{
		return z;
	}
	
	public RawModel getModel()
	{
		return model;
	}
	
	public TerrainTexturePack getTerrainTexturePack()
	{
		return texturePack;
	}
	
	public TerrainTexture getBlendMap()
	{
		return blendMap;
	}
}