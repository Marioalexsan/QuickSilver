package hg.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.math.Vector2;
import hg.directors.DirectorTypes;
import hg.drawables.*;
import hg.engine.*;
import hg.entities.Player;
import hg.physics.CollisionEngine;
import hg.physics.RaycastHit;

import java.text.DecimalFormat;
import java.util.ArrayList;
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

	public void setDefaultFOV() {
		factorFOV = 0.75f;
	}

	public void setFOVFactor(float fov) {
		factorFOV = fov;
	}

	BasicSprite lowhpvignette = new BasicSprite();
	BasicSprite targetGUI = new BasicSprite();
	BasicSprite targetWorld = new BasicSprite();

	BasicText debugText0 = new BasicText();
	BasicText debugText1 = new BasicText();
	BasicText debugText2 = new BasicText();
	BasicText debugText3 = new BasicText();

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
		targetWorld.setEnabled(false);

		debugText0.setFont(assetEngine.loadFont("Assets/Fonts/CourierNew36.fnt"));
		debugText0.registerToEngine();
		debugText0.setCameraUse(false);
		debugText0.setLayer(DrawLayer.GUIDefault);
		debugText0.setPosition(new Vector2(-940, -300));

		debugText1.setFont(assetEngine.loadFont("Assets/Fonts/CourierNew36.fnt"));
		debugText1.registerToEngine();
		debugText1.setCameraUse(false);
		debugText1.setLayer(DrawLayer.GUICursor);

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

		entityManager.addDirector(DirectorTypes.InitDirector);
	}

	// Certain things (such as dragging the window) will cause the game to stop updating
	// This means that the game loop is inherently unreliable! Keep this in mind when designing game logic for network play.
	@Override
	public void render () {
		Player player = entityManager.getLocalPlayer();

		inputEngine.update();

		targetGUI.getAngle().add(1.5f);

		entityManager.update();
		collisionEngine.update();

		if (player != null) {
			graphicsEngine.setCameraCenter(player.getPosition());
			audioEngine.setListenerPosition(HgGame.Input().getFOVWorldMouse(factorFOV).sub(player.getPosition()).scl(0.5f).add(player.getPosition()));
			graphicsEngine.setCameraOffset(HgGame.Input().getFOVCameraOffset(factorFOV));
		}

		targetGUI.setPosition(HgGame.Input().getMouse());
		targetWorld.setPosition(HgGame.Input().getFOVWorldMouse(factorFOV));

		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(0);
		format.setGroupingUsed(false);

		DecimalFormat format2 = new DecimalFormat();
		format2.setMaximumFractionDigits(2);
		format2.setGroupingUsed(false);

		ArrayList<RaycastHit> list = new ArrayList<>();
		if (player != null) {
			list = collisionEngine.doRaycast(player.getPosition(), player.getAngle(), 500);
		}

		String str = "Raycast Hits: " + list.size() + " - " + "Point hit: " + "\n";
		if (player != null) str += "Pos: " + format.format(player.getPosition().x) + " " + format.format(player.getPosition().y) + "\n";
		str += list.size() > 0 ? list.get(0).target.toString() + " " + format2.format(list.get(0).distance) : "";
		str += "\n";
		str += list.size() > 1 ? list.get(1).target.toString() + " " + format2.format(list.get(1).distance) : "\n";


		debugText0.setText(str);

		debugText1.setText("(" + format.format(targetWorld.getPosition().x) + ", " + format.format(targetWorld.getPosition().y) + ")\n" +
				format.format(targetGUI.getPosition().x) + ", " + format.format(targetGUI.getPosition().y) + ")");
		debugText1.setPosition(new Vector2(targetGUI.getPosition()).add(100, 20));

		debugText2.setText(Integer.toString(frameCounter));

		if (player != null) debugText3.setText(format.format(player.getStats().health) + ", Kills: " + player.DEBUG_killCount);

		if (inputEngine.isActionTapped(MappedAction.SecondaryFire)) {
			if (player != null) player.setPosition(targetWorld.getPosition()); // Debug teleport
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
