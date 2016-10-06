package resonantblade.renderengine3d.models;

import javafx.geometry.Point3D;

public class ModelData
{
	public final float[] positions, textureCoords, normals;
	public final int[] indices, textureFaceBounds;
	public final float furthest;
	private double surfaceArea;
	private double volume;
	
	public ModelData(float[] positions, float[] textureCoords, float[] normals, int[] indices, int[] textureFaceBounds, float furthest)
	{
		this.positions = positions;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.indices = indices;
		this.textureFaceBounds = textureFaceBounds;
		this.furthest = furthest;
		surfaceArea = -1.0D;
		volume = -1.0D;
	}
	
	private void computeComplex()
	{
		double totalSA = 0.0D;
		double totalVol = 0.0D;
		for(int i = 0; i < indices.length; i += 3)
		{
			Point3D pa = new Point3D(positions[indices[i] * 3], positions[indices[i] * 3 + 1], positions[indices[i] * 3 + 2]);
			Point3D pb = new Point3D(positions[indices[i + 1] * 3], positions[indices[i + 1] * 3 + 1], positions[indices[i + 1] * 3 + 2]);
			Point3D pc = new Point3D(positions[indices[i + 2] * 3], positions[indices[i + 2] * 3 + 1], positions[indices[i + 2] * 3 + 2]);
			double a = pa.distance(pb);
			double b = pb.distance(pc);
			double c = pc.distance(pa);
			double s = (a + b + c) / 2.0D;
			totalSA += Math.sqrt(s * (s - a) * (s - b) * (s - c));
			totalVol += pa.dotProduct(pb.crossProduct(pc)) / 6.0D;
		}
		surfaceArea = totalSA;
		volume = Math.abs(totalVol);
	}
	
	public double getSurfaceArea()
	{
		if(surfaceArea < 0.0D)
			computeComplex();
		return surfaceArea;
	}
	
	public double getVolume()
	{
		if(volume < 0.0D)
			computeComplex();
		return volume;
	}
}