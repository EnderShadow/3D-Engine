package resonantblade.renderengine3d.entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import resonantblade.renderengine3d.terrain.Terrain;

public class Camera
{
	private float distFromPlayer = 10.0F;
	private float angleAroundPlayer = 0.0F;
	
	
	private Vector3f position = new Vector3f(0.0F, 0.0F, 0.0F);
	private float pitch = 11.0F;
	private float yaw;
	private float roll;
	
	private IPlayer player;
	
	public Camera(IPlayer player)
	{
		this.player = player;
	}
	
	public void move(Terrain terrain)
	{
		player.move(terrain);
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		player.rotate(0.0F, angleAroundPlayer, 0.0F);
		float distX = distFromPlayer * (float) Math.cos(Math.toRadians(pitch));
		float distY = distFromPlayer * (float) Math.sin(Math.toRadians(pitch));
		
		float xOffset = distX * (float) Math.sin(Math.toRadians(angleAroundPlayer + player.getRotY()));
		float zOffset = distX * (float) Math.cos(Math.toRadians(angleAroundPlayer + player.getRotY()));

		position.x = player.getPosition().x - xOffset;
		position.y = player.getPosition().y + distY + 3;
		position.z = player.getPosition().z - zOffset;
		
		yaw = (180.0F - player.getRotY() - angleAroundPlayer) % 360.0F;
		if(yaw < 0.0F)
			yaw += 360.0F;
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	public float getPitch()
	{
		return pitch;
	}
	
	public float getYaw()
	{
		return yaw;
	}
	
	public float getRoll()
	{
		return roll;
	}
	
	public float getDistanceFromPlayer()
	{
		return distFromPlayer;
	}
	
	private void calculateZoom()
	{
		distFromPlayer -= Mouse.getDWheel() * 0.01F;
		if(distFromPlayer < 0.0F)
			distFromPlayer = 0.0F;
	}
	
	private void calculatePitch()
	{
		if(Mouse.isButtonDown(1))
		{
			pitch -= Mouse.getDY() * 0.2F;;
			if(pitch < -90.0F)
				pitch = -90.0F;
			if(pitch > 90.0F)
				pitch = 90.0F;
		}
	}
	
	private void calculateAngleAroundPlayer()
	{
		if(!Mouse.isInsideWindow())
			return;
		
		int centerX = Display.getWidth() / 2;
		int relativeX = Mouse.getX() - centerX;
		angleAroundPlayer = -relativeX * 0.2F;
		Mouse.setCursorPosition(centerX, Mouse.getY());
//		if(Mouse.isButtonDown(1))
//		{
//			angleAroundPlayer = -Mouse.getDX() * 0.2F;
//		}
//		else
//		{
//			angleAroundPlayer = 0.0F;
//		}
	}
}