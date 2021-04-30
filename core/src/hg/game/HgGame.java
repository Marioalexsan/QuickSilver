package hg.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.math.Vector2;
import hg.directors.DirectorTypes;
import hg.directors.LevelDirector;
import hg.drawables.*;
import hg.engine.*;
import hg.interfaces.callbacks.IButtonCallback;
import hg.libraries.ActorLibrary;
import hg.entities.Player;
import hg.maps.MapRWMethods;
import hg.physics.BoxCollider;
import hg.physics.CollisionAlgorithms;
import hg.physics.CollisionEngine;
import hg.physics.RTriangleCollider;
import hg.playerlogic.LuigiAI;
import hg.playerlogic.LocalPlayerLogic;
import hg.ui.ClickButton;
import hg.ui.UIElement;

import java.text.DecimalFormat;
import java.util.Random;


/** For TUIASI:
 * Project made by Miron Alexandru, 1208B
 */

public class HgGame extends ApplicationAdapter {

	private static HgGame _instance;
	public static HgGame Game() { return _instance; }

	public static AudioEngine Audio() { return _instance.audioEngine; }
	public static AssetEngine Assets() { return _instance.assetEngine; }
	public static CollisionEngine Physics() { return _instance.collisionEngine; }
	public static GraphicsEngine Graphics() { return _instance.graphicsEngine; }
	public static InputEngine Input() { return _instance.inputEngine; }

	public static EntityManager Entities() { return _instance.entityManager; }

	private AudioEngine audioEngine;
	private AssetEngine assetEngine;
	private CollisionEngine collisionEngine;
	private GraphicsEngine graphicsEngine;
	private InputEngine inputEngine;

	private EntityManager entityManager;

	public static final int WorldWidth = 1920;
	public static final int WorldHeight = 1080;

	// TO DO: rewrite / encapsulate random sources

	private static final Random randomVisual = new Random();
	private static final Random randomLogic = new Random();

	public static double getVisualRandom() { return randomVisual.nextDouble(); }
	public static double getLogicRandom() { return randomLogic.nextDouble(); }

	private int frameCounter = 0;
	private boolean applicationHasFocus = true;
	private float factorFOV = 0.75f;

	public float getFOVFactor() {
		return factorFOV;
	}

	BasicSprite lowhpvignette = new BasicSprite();
	BasicSprite targetGUI = new BasicSprite();
	BasicSprite targetWorld = new BasicSprite();

	BasicText debugText0 = new BasicText();
	BasicText debugText1 = new BasicText();
	BasicText debugText2 = new BasicText();
	BasicText debugText3 = new BasicText();
	BasicText debugText4 = new BasicText();
	BasicText debugText5 = new BasicText();

	public Player player;
	public Player enemy;
	public Player enemy2;


	RTriangleCollider pointColliderTest = new RTriangleCollider(1000, 350);

	UIElement button;


	@Override
	public void create () {
		_instance = this;

		graphicsEngine = new GraphicsEngine();
		assetEngine = new AssetEngine();
		audioEngine = new AudioEngine();
		inputEngine = new InputEngine();
		collisionEngine = new CollisionEngine();
		entityManager = new EntityManager();

		collisionEngine.setDebugDraw(true); // This will make colliders visible!

		lowhpvignette.setTexture(assetEngine.loadTexture("Assets/GUI/LowHP.png"));
		lowhpvignette.registerToEngine();
		lowhpvignette.centerToRegion();
		lowhpvignette.setLayer(DrawLayer.GUIDefault);
		lowhpvignette.setCameraUse(false);
		targetGUI.setTexture(assetEngine.loadTexture("Assets/GUI/Target.png"));
		targetGUI.setCameraUse(false);
		targetGUI.setLayer(DrawLayer.GUICursor);
		targetGUI.centerToRegion();
		targetGUI.registerToEngine();

		targetWorld.setTexture(assetEngine.loadTexture("Assets/GUI/Target.png"));
		targetWorld.setLayer(DrawLayer.Overlay);
		targetWorld.centerToRegion();
		targetWorld.registerToEngine();

		debugText0.setFont(assetEngine.loadFont("Assets/Fonts/CourierNew36.fnt"));
		debugText0.registerToEngine();
		debugText0.setCameraUse(false);
		debugText0.setLayer(DrawLayer.GUIDefault);
		debugText0.setPosition(new Vector2(-940, -300));

		debugText1.setFont(assetEngine.loadFont("Assets/Fonts/CourierNew36.fnt"));
		debugText1.registerToEngine();
		debugText1.setCameraUse(false);
		debugText1.setLayer(DrawLayer.GUIDefault);

		debugText2.setFont(assetEngine.loadFont("Assets/Fonts/CourierNew36.fnt"));
		debugText2.setCameraUse(false);
		debugText2.registerToEngine();
		debugText2.setLayer(DrawLayer.GUIDefault);
		debugText2.setPosition(new Vector2(-940, -160));

		audioEngine.playMusic("Assets/Audio/advancing_chaos.ogg", 1f);
		audioEngine.setGlobalSoundVolume(0.5f);
		audioEngine.setGlobalMusicVolume(0.5f);

		graphicsEngine.setVideoMode(1600, 900, false);
		graphicsEngine.setCameraZoom(1.2);

		LevelDirector level = (LevelDirector) entityManager.getDirector(DirectorTypes.LEVEL_DIRECTOR);

		//var proto = MapLibrary.CreatePrototype(MapLibrary.StaticMaps.TestArea01);
		level.LoadMap(MapRWMethods.LoadMapFromFile("Assets/Maps/grinder.hgm"));
		//MapReaderWriter.WriteMapToFile(proto, "Assets/Maps/grinder.hgm");

		player = (Player) entityManager.addActor(ActorLibrary.Types.Player, new Vector2(3450, 1500), 0f);
		player.setLogic(new LocalPlayerLogic());

		enemy = (Player) entityManager.addActor(ActorLibrary.Types.Player, new Vector2(3450, 1800), 0f);
		enemy2 = (Player) entityManager.addActor(ActorLibrary.Types.Player, new Vector2(3450, 2100), 0f);

		for (int i = 0; i < 100; i++) {
			//Player enemy3 = (Player) entityManager.addActor(ActorLibrary.Types.Player, new Vector2(3450 + 5 * i, 2100 + 4 * i), 0f);
			//enemy3.setLogic(new LuigiAI());
		}

		enemy2.setLogic(new LuigiAI());

		debugText3.setFont(assetEngine.loadFont("Assets/Fonts/CourierNew36.fnt"));
		debugText3.registerToEngine();
		debugText3.setLayer(DrawLayer.GUIDefault);
		debugText3.setPosition(player.getPosition());
		debugText3.setPositionOffset(new Vector2(100, 0));

		debugText4.setFont(assetEngine.loadFont("Assets/Fonts/CourierNew36.fnt"));
		debugText4.registerToEngine();
		debugText4.setLayer(DrawLayer.GUIDefault);
		debugText4.setPosition(enemy.getPosition());
		debugText4.setPositionOffset(new Vector2(100, 0));

		debugText5.setFont(assetEngine.loadFont("Assets/Fonts/CourierNew36.fnt"));
		debugText5.registerToEngine();
		debugText5.setLayer(DrawLayer.GUIDefault);
		debugText5.setPosition(enemy2.getPosition());
		debugText5.setPositionOffset(new Vector2(100, 0));


		pointColliderTest.getAngle().add(30);
		pointColliderTest.getPosition().add(1000, 1000);
		pointColliderTest.registerToEngine();

		button = new ClickButton(assetEngine.loadTexture("Assets/GUI/Button.png"), 460, 150);
		button.setPosition(-0, -0);
		((ClickButton) button).setCallback(() -> audioEngine.playSound(assetEngine.loadSound("Assets/Audio/gunclick.ogg"), 1f));
	}

	// Certain things (such as dragging the window) will cause the game to stop updating
	// This means that the game loop is inherently unreliable! Keep this in mind when designing game logic for network play.
	@Override
	public void render () {
		inputEngine.update();

		targetGUI.getAngle().add(1.5f);

		entityManager.update();
		collisionEngine.update();

		graphicsEngine.setCameraCenter(player.getPosition());
		audioEngine.setListenerPosition(HgGame.Input().getFOVWorldMouse(factorFOV).sub(player.getPosition()).scl(0.5f).add(player.getPosition()));
		graphicsEngine.setCameraOffset(HgGame.Input().getFOVCameraOffset(factorFOV));

		targetGUI.setPosition(HgGame.Input().getMouse());
		targetWorld.setPosition(HgGame.Input().getFOVWorldMouse(factorFOV));

		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(0);
		format.setGroupingUsed(false);

		DecimalFormat format2 = new DecimalFormat();
		format2.setMaximumFractionDigits(2);
		format2.setGroupingUsed(false);

		var list = collisionEngine.doRaycast(player.getPosition(), player.getAngle(), 500);

		String str = "Raycast Hits: " + list.size() + " - " + "Point hit: " + CollisionAlgorithms.PointHit(targetWorld.getPosition(), pointColliderTest) + "\n";
		str += "Pos: " + format.format(player.getPosition().x) + " " + format.format(player.getPosition().y) + "\n";
		str += list.size() > 0 ? list.get(0).target.toString() + " " + format2.format(list.get(0).distance) : "";
		str += "\n";
		str += list.size() > 1 ? list.get(1).target.toString() + " " + format2.format(list.get(1).distance) : "\n";


		debugText0.setText(str);

		debugText1.setText("(" + format.format(targetWorld.getPosition().x) + ", " + format.format(targetWorld.getPosition().y) + ")");
		debugText1.setPosition(new Vector2(targetGUI.getPosition()).add(100, 20));

		debugText2.setText(Integer.toString(frameCounter));

		debugText3.setText(format.format(player.getStats().health) + ", Kills: " + player.DEBUG_killCount);
		debugText4.setText(format.format(enemy.getStats().health) + ", Kills: " + enemy.DEBUG_killCount);
		debugText5.setText(format.format(enemy2.getStats().health) + ", Kills: " + enemy2.DEBUG_killCount);

		if (inputEngine.isActionTapped(MappedAction.SecondaryFire)) {
			//player.setPosition(targetWorld.getPosition()); // Debug teleport
			button.onLMBDown(targetGUI.getPosition().x, targetGUI.getPosition().y);
		}

		graphicsEngine.render();
		audioEngine.update();

		frameCounter++;
	}

	@Override
	public void dispose () {
		entityManager.cleanup();

		graphicsEngine.cleanup();
		audioEngine.cleanup();

		assetEngine.unloadAll();
	}

	/**
	 * This function is called by LibGDX whenever the application loses focus.
	 */
	@Override
	public void pause() {
		applicationHasFocus = false;
	}

	/**
	 * This function is called by LibGDX whenever the application gains focus
	 */
	@Override
	public void resume() {
		applicationHasFocus = true;
	}

	/**
	 * Returns true if the application has focus.
	 */
	public boolean isFocused() {
		return applicationHasFocus;
	}
}
