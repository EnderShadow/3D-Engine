package resonantblade.renderengine3d;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import resonantblade.renderengine3d.models.ModelData;
import resonantblade.renderengine3d.pmx.Face;
import resonantblade.renderengine3d.pmx.Material;
import resonantblade.renderengine3d.pmx.PMXData;
import javafx.util.Pair;

public class PMXLoader
{
	private static final String RES_LOC = "res/";
	
	public static Pair<ModelData, PMXData> loadPMXOrPMD(String pmxOrPmdURL)
	{
		return loadPMXOrPMD(pmxOrPmdURL, 0.0F);
	}
	
	public static Pair<ModelData, PMXData> loadPMXOrPMD(String pmxOrPmdURL, float yawRot)
	{
		PMXData data = null;
		try
		{
			data = PMXData.readFromFile(new File(RES_LOC + pmxOrPmdURL));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
		
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		
		int index = 0;
		for(resonantblade.renderengine3d.pmx.Vertex v : data.vertices)
		{
			Vertex vert = new Vertex(index, v.position);
			vert.setTextureIndex(index++);
			vertices.add(vert);
			textures.add(v.uv); // texture coords I think
			normals.add(v.normal);
		}
		for(Face f : data.faces)
		{
			processVertex(f.vertexIndex1, vertices, indices);
			processVertex(f.vertexIndex2, vertices, indices);
			processVertex(f.vertexIndex3, vertices, indices);
		}
		
		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray);
		int[] indicesArray = convertIndicesListToArray(indices);
		
		for(int i = 0; i < verticesArray.length; i += 3)
		{
			verticesArray[i] = verticesArray[i] * (float) Math.cos(yawRot);
			verticesArray[i + 2] = verticesArray[i + 2] * (float) Math.cos(yawRot);
		}
		for(int i = 0; i < normalsArray.length; i += 3)
		{
			normalsArray[i] = normalsArray[i] * (float) Math.cos(yawRot);
			normalsArray[i + 2] = normalsArray[i + 2] * (float) Math.cos(yawRot);
		}
		int[] textureFaceBounds = Arrays.stream(data.materials).mapToInt(m -> m.faceCount).toArray();
		
		return new Pair<>(new ModelData(verticesArray, texturesArray, normalsArray, indicesArray, textureFaceBounds, furthest), data);
	}
	
	private static void processVertex(int index, List<Vertex> vertices, List<Integer> indices)
	{
		Vertex v = vertices.get(index);
		int textureIndex = v.getTextureIndex();
		if(!v.isSet())
		{
			v.setNormalIndex(index);
			indices.add(index);
		}
		else
		{
			dealWithAlreadyProcessedVertex(v, textureIndex, index, indices, vertices);
		}
	}
	
	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normals, float[] verticesArray, float[] texturesArray, float[] normalsArray) {
		float furthestPoint = 0;
		for(int i = 0; i < vertices.size(); i++)
		{
			Vertex currentVertex = vertices.get(i);
			if(currentVertex.getLength() > furthestPoint)
			{
				furthestPoint = currentVertex.getLength();
			}
			Vector3f position = currentVertex.getPosition();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
		}
		return furthestPoint;
	}

	private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
			int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
						indices, vertices);
			} else {
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
			}

		}
	}
	
	private static void removeUnusedVertices(List<Vertex> vertices){
		for(Vertex vertex:vertices){
			if(!vertex.isSet()){
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}

}