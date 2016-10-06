package resonantblade.renderengine3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import resonantblade.renderengine3d.entities.Camera;
import resonantblade.renderengine3d.entities.Entity;
import resonantblade.renderengine3d.entities.Light;
import resonantblade.renderengine3d.models.TexturedModel;
import resonantblade.renderengine3d.shaders.StaticShader;
import resonantblade.renderengine3d.shaders.TerrainShader;
import resonantblade.renderengine3d.skybox.SkyboxRenderer;
import resonantblade.renderengine3d.terrain.Terrain;

public class MasterRenderer
{
	private static final float FOV = 70.0F;
	private static final float NEAR_PLANE = 0.1F;
	private static final float FAR_PLANE = 1000.0F;
	
	private static final float SKY_COLOR_RED = 135.0F / 255.0F;
	private static final float SKY_COLOR_GREEN = 206.0F / 255.0F;
	private static final float SKY_COLOR_BLUE = 235.0F / 255.0F;
	
	private Matrix4f projectionMatrix;
	private StaticShader shader = new StaticShader();
	private EntityRenderer entityRenderer;
	
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	private SkyboxRenderer skyboxRenderer;
	
	public MasterRenderer(Loader loader)
	{
		enableCulling();
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
	}
	
	public static void enableCulling()
	{
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling()
	{
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}
	
	public void render(List<Light> lights, Camera camera)
	{
		prepare();
		shader.start();
		shader.loadSkyColorVariable(SKY_COLOR_RED, SKY_COLOR_GREEN, SKY_COLOR_BLUE);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		entityRenderer.render(entities);
		shader.stop();
		terrainShader.start();
		terrainShader.loadSkyColorVariable(SKY_COLOR_RED, SKY_COLOR_GREEN, SKY_COLOR_BLUE);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		skyboxRenderer.render(camera, SKY_COLOR_RED, SKY_COLOR_GREEN, SKY_COLOR_BLUE);
		terrains.clear();
		entities.clear();
	}
	
	public void processEntity(Entity entity)
	{
		TexturedModel tm = entity.getModel();
		List<Entity> list = entities.getOrDefault(tm, new ArrayList<Entity>());
		if(!list.contains(entity))
			list.add(entity);
		entities.putIfAbsent(tm, list);
	}
	
	public void processTerrain(Terrain terrain)
	{
		terrains.add(terrain);
	}
	
	public void prepare()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(SKY_COLOR_RED, SKY_COLOR_GREEN, SKY_COLOR_BLUE, 1.0F);
	}
	
	private void createProjectionMatrix()
	{
		float aspectRatio = (float) Display.getWidth() / Display.getHeight();
		float xScale = (float) (1.0D / Math.tan(Math.toRadians(FOV / 2.0D)));
		float yScale = xScale * aspectRatio;
		float frustomLength = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = xScale;
		projectionMatrix.m11 = yScale;
		projectionMatrix.m22 = -(FAR_PLANE + NEAR_PLANE) / frustomLength;
		projectionMatrix.m23 = -1.0F;
		projectionMatrix.m32 = -2.0F * NEAR_PLANE * FAR_PLANE / frustomLength;
		projectionMatrix.m33 = 0.0F;
	}
	
	public void cleanUp()
	{
		shader.cleanUp();
		terrainShader.cleanUp();
	}
}