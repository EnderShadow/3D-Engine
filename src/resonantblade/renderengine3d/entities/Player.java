package resonantblade.renderengine3d.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import resonantblade.renderengine3d.DisplayManager;
import resonantblade.renderengine3d.models.TexturedModel;
import resonantblade.renderengine3d.terrain.Terrain;

public class Player extends Entity implements IPlayer
{
	private static final float RUN_SPEED = 20.0F;
	private static final float STRAFE_SPEED = 15.0F;
	private static final float TURN_SPEED = 160.0F;
	private static final float GRAVITY = -50.0F;
	private static final float JUMP_POWER = 15.0F;
	
	private float curSpeedFB;
	private float curSpeedLR;
	private float curTurnSpeed;
	private float upSpeed;
	private boolean isInAir = true;
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale)
	{
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	public void move(Terrain terrain)
	{
		checkInputs();
		rotate(0.0F, curTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0.0F);
		float distance = curSpeedFB * DisplayManager.getFrameTimeSeconds();
		float distance2 = curSpeedLR * DisplayManager.getFrameTimeSeconds();
		upSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		move((float) Math.sin(Math.toRadians(getRotY())) * distance, upSpeed * DisplayManager.getFrameTimeSeconds(), (float) Math.cos(Math.toRadians(getRotY())) * distance);
		move((float) Math.cos(Math.toRadians(getRotY())) * distance2, 0.0F, (float) -Math.sin(Math.toRadians(getRotY())) * distance2);
		float terrainHeight = terrain.getHeight(getPosition().x, getPosition().z);
		if(getPosition().y < terrainHeight)
		{
			upSpeed = 0.0F;
			getPosition().y = terrainHeight;
			isInAir = false;
		}
	}
	
	private void checkInputs()
	{
		if(isInAir)
			return;
		
		curSpeedFB = 0.0F;
		curSpeedLR = 0.0F;
		curTurnSpeed = 0.0F;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
			curSpeedFB += RUN_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_S))
			curSpeedFB -= RUN_SPEED;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
			//curTurnSpeed += TURN_SPEED;
			curSpeedLR += STRAFE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
			//curTurnSpeed -= TURN_SPEED;
			curSpeedLR -= STRAFE_SPEED;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			jump();
	}
	
	private void jump()
	{
		upSpeed = JUMP_POWER;
		isInAir = true;
	}
}