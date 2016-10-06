package resonantblade.testing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import javafx.util.Pair;
import resonantblade.renderengine3d.DisplayManager;
import resonantblade.renderengine3d.Loader;
import resonantblade.renderengine3d.MasterRenderer;
import resonantblade.renderengine3d.OBJLoader;
import resonantblade.renderengine3d.PMXLoader;
import resonantblade.renderengine3d.entities.Camera;
import resonantblade.renderengine3d.entities.Entity;
import resonantblade.renderengine3d.entities.Light;
import resonantblade.renderengine3d.entities.Player;
import resonantblade.renderengine3d.guis.GUIRenderer;
import resonantblade.renderengine3d.guis.GUITexture;
import resonantblade.renderengine3d.models.ModelData;
import resonantblade.renderengine3d.models.TexturedModel;
import resonantblade.renderengine3d.pmx.PMXData;
import resonantblade.renderengine3d.terrain.Terrain;
import resonantblade.renderengine3d.textures.ModelTexture;
import resonantblade.renderengine3d.textures.TerrainTexture;
import resonantblade.renderengine3d.textures.TerrainTexturePack;
import resonantblade.renderengine3d.util.MathUtils;
import resonantblade.renderengine3d.util.MousePicker;

public class Tester
{
	public static void main(String[] args)
	{
		DisplayManager.createDisplay("3D Render Engine Test");
		
		Loader loader = new Loader();
		
		// load textures
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy.png"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt.png"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers.png"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path.png"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap.png"));
		
		// done loading textures
		
		ModelData mTree = OBJLoader.loadObj("tree");
		TexturedModel tree = new TexturedModel(loader.loadToVAO(mTree), mTree, new ModelTexture(loader.loadTexture("tree.png")));
		ModelData mGrass = OBJLoader.loadObj("grassModel");
		TexturedModel grass = new TexturedModel(loader.loadToVAO(mGrass), mGrass, new ModelTexture(loader.loadTexture("grassTexture.png")));
		grass.textures[0].setTransparency(true);
		grass.textures[0].useFakeLighting(true);
		ModelData mFern = OBJLoader.loadObj("fern");
		TexturedModel fern = new TexturedModel(loader.loadToVAO(mFern), mFern, new ModelTexture(loader.loadTexture("fern.png")));
		fern.textures[0].setTransparency(true);
		fern.textures[0].setNumRows(2);
		
		Pair<ModelData, PMXData> yukiData = PMXLoader.loadPMXOrPMD("yuki/Yuki Nagato.pmx");//, (float) Math.PI);
		ModelData yuki = yukiData.getKey();
		PMXData yukiPmxData = yukiData.getValue();
		ModelTexture[] mts = new ModelTexture[yukiPmxData.textures.length];
		for(int i = 0; i < mts.length; i++)
		{
			mts[i] = new ModelTexture(loader.loadTexture("yuki/" + yukiPmxData.textures[i]));
		}
		{
			ModelTexture[] temp = new ModelTexture[yukiPmxData.materials.length];
			for(int i = 0; i < temp.length; i++)
			{
				int index = yukiPmxData.materials[i].textureIndex;
				temp[i] = index > 0 ? mts[index] : ModelTexture.NULL_TEXTURE;
				temp[i].setTransparency(temp[i].hasTransparency() || yukiPmxData.materials[i].noCull());
			}
			mts = temp;
		}
		TexturedModel texturedYuki = new TexturedModel(loader.loadToVAO(yuki), yuki, mts);
		
		ModelData mPerson = OBJLoader.loadObj("person");
		//Player player = new Player(new TexturedModel(loader.loadToVAO(mPerson), mPerson, new ModelTexture(loader.loadTexture("playerTexture.png"))), new Vector3f(200, 20, -300), 0, 0, 0, 0.3F);
		Player player = new Player(texturedYuki, new Vector3f(200, 20, -300), 0, 0, 0, 0.3F);
		
		List<Light> lights = new ArrayList<Light>();
		Light light = new Light(new Vector3f(0, 1000, -7000), new Vector3f(0.4F, 0.4F, 0.4F));
		Light light2 = new Light(new Vector3f(185, 10, -293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01F, 0.002F));
		Light light3 = new Light(new Vector3f(370, 17, -300), new Vector3f(0, 2, 2), new Vector3f(1, 0.01F, 0.002F));
		Light light4 = new Light(new Vector3f(293, 7, -305), new Vector3f(2, 2, 0), new Vector3f(1, 0.01F, 0.002F));
		lights.add(light);
		lights.add(light2);
		lights.add(light3);
		lights.add(light4);
		
		int numTerrains = 6;
		numTerrains = Math.max(numTerrains / 2 * 2, 1);
		Terrain[][] terrains = new Terrain[numTerrains][numTerrains];
		for(int i = 0; i < numTerrains; i++)
			for(int j = 0; j < numTerrains; j++)
				terrains[i][j] = new Terrain(j - numTerrains / 2, i - numTerrains / 2, loader, texturePack, blendMap, "heightMap.png");
		
		List<Entity> entities = new ArrayList<Entity>(2000);
		Random rand = new Random();
		for(int i = 0; i < 500 * numTerrains; i++)
		{
			float x = randWorldCoord(rand, numTerrains);
			float z = randWorldCoord(rand, numTerrains);
			int terX = (int) Math.floor(x / Terrain.SIZE) + numTerrains / 2;
			int terZ = (int) Math.floor(z / Terrain.SIZE) + numTerrains / 2;
			float height = terrains[terZ][terX].getHeight(x, z);
			entities.add(new Entity(tree, new Vector3f(x, height, z), 0, 0, 0, 3.0F));
			
			x = randWorldCoord(rand, numTerrains);
			z = randWorldCoord(rand, numTerrains);
			terX = (int) Math.floor(x / Terrain.SIZE) + numTerrains / 2;
			terZ = (int) Math.floor(z / Terrain.SIZE) + numTerrains / 2;
			height = terrains[terZ][terX].getHeight(x, z);
			entities.add(new Entity(grass, new Vector3f(x, height, z), 0, 0, 0, 1.0F));
			
			x = randWorldCoord(rand, numTerrains);
			z = randWorldCoord(rand, numTerrains);
			terX = (int) Math.floor(x / Terrain.SIZE) + numTerrains / 2;
			terZ = (int) Math.floor(z / Terrain.SIZE) + numTerrains / 2;
			height = terrains[terZ][terX].getHeight(x, z);
			entities.add(new Entity(fern, rand.nextInt(fern.textures[0].getNumRows() * fern.textures[0].getNumRows()), new Vector3f(x, height, z), 0, 0, 0, 0.6F));
		}
		
		Camera camera = new Camera(player);
		
		List<GUITexture> guis = new ArrayList<GUITexture>();
		//GUITexture gui = new GUITexture(loader.loadTexture("socuwan.png"), new Vector2f(0.5F, 0.5F), new Vector2f(0.25F, 0.25F));
		//guis.add(gui);
		//GUITexture gui2 = new GUITexture(loader.loadTexture("thinmatrix.png"), new Vector2f(0.3F, 0.74F), new Vector2f(0.4F, 0.4F));
		//guis.add(gui2);
		
		GUIRenderer guiRenderer = new GUIRenderer(loader);
		
		MasterRenderer renderer = new MasterRenderer(loader);
		
		MousePicker mousePicker = new MousePicker(camera, renderer.getProjectionMatrix());
		
		while(!Display.isCloseRequested())
		{
			int terX = (int) Math.floor(player.getPosition().x / Terrain.SIZE) + numTerrains / 2;
			int terZ = (int) Math.floor(player.getPosition().z / Terrain.SIZE) + numTerrains / 2;
			camera.move(terrains[terZ][terX]);
			terX = (int) Math.floor(player.getPosition().x / Terrain.SIZE) + numTerrains / 2;
			terZ = (int) Math.floor(player.getPosition().z / Terrain.SIZE) + numTerrains / 2;
			
			mousePicker.update();
			System.out.println(mousePicker.getCurrentRay());
			
			// game logic
			renderer.processTerrain(terrains[terZ][terX]);
			float terXOffset = (player.getPosition().x % Terrain.SIZE + Terrain.SIZE) % Terrain.SIZE;
			float terZOffset = (player.getPosition().z % Terrain.SIZE + Terrain.SIZE) % Terrain.SIZE;
			if(terXOffset < Terrain.SIZE / 2)
			{
				if(terX - 1 >= 0 && terX - 1 < terrains.length)
				{
					renderer.processTerrain(terrains[terZ][terX - 1]);
					if(terZOffset < Terrain.SIZE / 2)
					{
						if(terZ - 1 >= 0 && terZ - 1 < terrains.length)
						{
							renderer.processTerrain(terrains[terZ - 1][terX]);
							renderer.processTerrain(terrains[terZ - 1][terX - 1]);
						}
					}
					else
					{
						if(terZ + 1 >= 0 && terZ + 1 < terrains.length)
						{
							renderer.processTerrain(terrains[terZ + 1][terX]);
							renderer.processTerrain(terrains[terZ + 1][terX - 1]);
						}
					}
				}
			}
			else
			{
				if(terX + 1 >= 0 && terX + 1 < terrains.length)
				{
					renderer.processTerrain(terrains[terZ][terX + 1]);
					if(terZOffset < Terrain.SIZE / 2)
					{
						if(terZ - 1 >= 0 && terZ - 1 < terrains.length)
						{
							renderer.processTerrain(terrains[terZ - 1][terX]);
							renderer.processTerrain(terrains[terZ - 1][terX + 1]);
						}
					}
					else
					{
						if(terZ + 1 >= 0 && terZ + 1 < terrains.length)
						{
							renderer.processTerrain(terrains[terZ + 1][terX]);
							renderer.processTerrain(terrains[terZ + 1][terX + 1]);
						}
					}
				}
			}
			renderer.processEntity(player);
			entities.stream().filter(e -> MathUtils.distance(e.getPosition(), player.getPosition()) < Terrain.SIZE / 2).forEach(renderer::processEntity);
			renderer.render(lights, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
			//System.out.println(DisplayManager.getFrameTimeSeconds());
		}
		
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
	
	private static float randWorldCoord(Random rand, int numTerrains)
	{
		return (rand.nextFloat() * numTerrains - numTerrains / 2) * Terrain.SIZE;
	}
}