package resonantblade.renderengine3d.util;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import resonantblade.renderengine3d.entities.Camera;

public class MousePicker
{
	private Vector3f currentRay;
	private Matrix4f projectionMatrix, viewMatrix;
	private Camera camera;
	
	public MousePicker(Camera camera, Matrix4f projectionMatrix)
	{
		this.camera = camera;
		this.projectionMatrix = projectionMatrix;
		viewMatrix = MathUtils.createViewMatrix(camera);
	}
	
	public Vector3f getCurrentRay()
	{
		return currentRay;
	}
	
	public void update()
	{
		viewMatrix = MathUtils.createViewMatrix(camera);
		currentRay = calculateCurrentRay();
	}
	
	private Vector3f calculateCurrentRay()
	{
		float mouseX = Mouse.getX();
		float mouseY = Mouse.getY();
		Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0F, 1.0F);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}
	
	private Vector3f toWorldCoords(Vector4f eyeCoords)
	{
		Matrix4f invertedViewMatrix = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedViewMatrix, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();
		return mouseRay;
	}
	
	private Vector4f toEyeCoords(Vector4f clipCoords)
	{
		Matrix4f invertedProjectionMatrix = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjectionMatrix, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1.0F, 0.0F);
	}
	
	private Vector2f getNormalizedDeviceCoords(float x, float y)
	{
		float width = Display.getWidth();
		float height = Display.getHeight();
		return new Vector2f(2.0F * x / width - 1.0F, 2.0F * y / height - 1.0F);
	}
}