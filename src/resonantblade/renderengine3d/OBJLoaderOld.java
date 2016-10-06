package resonantblade.renderengine3d;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import resonantblade.renderengine3d.models.RawModel;

public class OBJLoaderOld
{
	public static RawModel loadObjModel(String filename, Loader loader)
	{
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		float[] verticesArray = null;
		float[] normalsArray = null;
		float[] textureArray = null;
		int[] indicesArray = null;
		
		try(BufferedReader br = new BufferedReader(new FileReader(new File("res/" + filename + ".obj"))))
		{
			String line;
			
			while((line = br.readLine()) != null)
			{
				String[] curLine = line.split(" ");
				if(line.startsWith("v "))
				{
					Vector3f vertex = new Vector3f(Float.parseFloat(curLine[1]), Float.parseFloat(curLine[2]), Float.parseFloat(curLine[3]));
					vertices.add(vertex);
				}
				else if(line.startsWith("vt "))
				{
					Vector2f texture = new Vector2f(Float.parseFloat(curLine[1]), Float.parseFloat(curLine[2]));
					textures.add(texture);
				}
				else if(line.startsWith("vn "))
				{
					Vector3f normal = new Vector3f(Float.parseFloat(curLine[1]), Float.parseFloat(curLine[2]), Float.parseFloat(curLine[3]));
					normals.add(normal);
				}
				else if(line.startsWith("f "))
				{
					textureArray = new float[vertices.size() * 2];
					normalsArray = new float[vertices.size() * 3];
					break;
				}
			}
			
			while(line != null)
			{
				if(!line.startsWith("f "))
				{
					line = br.readLine();
					continue;
				}
				String[] curLine = line.split(" ");
				String[] vertex1 = curLine[1].split("/");
				String[] vertex2 = curLine[2].split("/");
				String[] vertex3 = curLine[3].split("/");
				
				processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
				processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
				processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);
				
				line = br.readLine();
			}
		}
		catch(Exception e)
		{
			System.err.println("ERROR: Failed to load OBJ file");
			e.printStackTrace();
		}
		
		verticesArray = new float[vertices.size() * 3];
		indicesArray = new int[indices.size()];
		
		for(int i = 0; i < vertices.size(); i++)
		{
			Vector3f vertex = vertices.get(i);
			verticesArray[i * 3] = vertex.x;
			verticesArray[i * 3 + 1] = vertex.y;
			verticesArray[i * 3 + 2] = vertex.z;
		}
		
		for(int i = 0; i < indicesArray.length; i++)
			indicesArray[i] = indices.get(i);
		
		return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);
	}
	
	private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures, List<Vector3f> normals, float[] textureArray, float[] normalsArray)
	{
		int curVertexPointer = Integer.parseInt(vertexData[0]) - 1;
		indices.add(curVertexPointer);
		
		Vector2f curTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
		textureArray[curVertexPointer * 2] = curTex.x;
		textureArray[curVertexPointer * 2 + 1] = 1.0F - curTex.y;
		
		Vector3f curNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
		normalsArray[curVertexPointer * 3] = curNorm.x;
		normalsArray[curVertexPointer * 3 + 1] = curNorm.y;
		normalsArray[curVertexPointer * 3 + 2] = curNorm.z;
	}
}