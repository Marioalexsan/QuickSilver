package hg.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import hg.directors.DirectorTypes;
import hg.drawables.*;
import hg.engine.*;
import hg.entities.Player;
import hg.engine.CollisionEngine;

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
	private float factorFOV = 0.75f;

	private boolean applicationHasFocus = true;
	private boolean quitCalled = false;

	public float getFOVFactor() {
		return factorFOV;
	}

	public void setDefaultFOV() {
		factorFOV = 0.75f;
	}

	public void setFOVFactor(float fov) {
		factorFOV = fov;
	}

	public void quitGame() {
		quitCalled = true;
	}

	BasicSprite lowhpvignette = new BasicSprite();
	BasicSprite targetGUI = new BasicSprite();
	BasicSprite targetWorld = new BasicSprite();

	@Override
	public void create () {
		_instance = this;

		graphicsEngine = new GraphicsEngine();
		assetEngine = new AssetEngine();
		audioEngine = new AudioEngine();
		inputEngine = new InputEngine();
		collisionEngine = new CollisionEngine();
		entityManager = new EntityManager();

		collisionEngine.setDebugDraw(false); // This will make colliders visible!

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

		entityManager.update();
		collisionEngine.update();

		if (player != null) {
			graphicsEngine.setCameraCenter(player.getPosition());
			audioEngine.setListenerPosition(HgGame.Input().getFOVWorldMouse(factorFOV).sub(player.getPosition()).scl(0.5f).add(player.getPosition()));
			graphicsEngine.setCameraOffset(HgGame.Input().getFOVCameraOffset(factorFOV));
		}

		targetGUI.getAngle().add(1.5f);
		targetGUI.setPosition(HgGame.Input().getMouse());
		targetWorld.setPosition(HgGame.Input().getFOVWorldMouse(factorFOV));

		if (player != null && inputEngine.isActionTapped(MappedAction.SecondaryFire))
			player.setPosition(targetWorld.getPosition()); // Debug teleport

		graphicsEngine.render();
		audioEngine.update();

		frameCounter++;

		if (quitCalled) Gdx.app.exit();
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
