package resonantblade.renderengine3d.entities;

import org.lwjgl.util.vector.Vector3f;

import resonantblade.renderengine3d.terrain.Terrain;

public interface IPlayer
{
	Vector3f getPosition();
	float getRotX();
	float getRotY();
	float getRotZ();
	void move(Terrain terrain);
	public void move(float dx, float dy, float dz);
	public void rotate(float dx, float dy, float dz);
}