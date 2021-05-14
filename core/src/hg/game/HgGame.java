package hg.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import hg.types.DirectorType;
import hg.drawables.*;
import hg.engine.*;
import hg.entities.PlayerEntity;
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

	public static GameManager Manager() { return _instance.gameManager; }
	public static NetworkEngine Network() { return _instance.networkEngine; }

	private AudioEngine audioEngine;
	private AssetEngine assetEngine;
	private CollisionEngine collisionEngine;
	private GraphicsEngine graphicsEngine;
	private InputEngine inputEngine;

	private GameManager gameManager;
	private NetworkEngine networkEngine;

	private boolean crashed = false;

	public String localName = "Anonymous" + (Math.abs(new Random().nextInt()) % 9999);

	public static final int WorldWidth = 1920;
	public static final int WorldHeight = 1080;

	// TO DO: rewrite / encapsulate random sources

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
		gameManager = new GameManager();
		networkEngine = new NetworkEngine();

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
		audioEngine.setGlobalMusicVolume(0f);

		graphicsEngine.setVideoMode(1280, 720, false);
		graphicsEngine.setCameraZoom(1.2);

		gameManager.tryAddDirector(DirectorType.GameInit);
	}

	// Certain things (such as dragging the window) will cause the game to stop updating
	// This means that the game loop is inherently unreliable! Keep this in mind when designing game logic for network play.
	@Override
	public void render () {
		try {
			PlayerEntity playerEntity = gameManager.localView != null ? gameManager.localView.playerEntity : null;

			inputEngine.update();
			networkEngine.update();

			gameManager.networkUpdate();

			gameManager.update();
			collisionEngine.update();

			if (playerEntity != null) {
				graphicsEngine.setCameraCenter(playerEntity.getPosition());
				audioEngine.setListenerPosition(HgGame.Input().getFOVWorldMouse(factorFOV).sub(playerEntity.getPosition()).scl(0.5f).add(playerEntity.getPosition()));
				graphicsEngine.setCameraOffset(HgGame.Input().getFOVCameraOffset(factorFOV));
			}

			targetGUI.getAngle().add(1.5f);
			targetGUI.setPosition(HgGame.Input().getMouse());
			targetWorld.setPosition(HgGame.Input().getFOVWorldMouse(factorFOV));

			if (playerEntity != null && inputEngine.isActionTapped(MappedAction.SecondaryFire))
				playerEntity.getPosition().set(targetWorld.getPosition()); // Debug teleport

			graphicsEngine.render();
			audioEngine.update();

			frameCounter++;
		}
		catch (Throwable crash) {
			crash.printStackTrace();
			crashed = true;
		}

		if (crashed || quitCalled) Gdx.app.exit();
	}

	/** This does cleanup on exit */
	@Override
	public void dispose () {
		if (crashed) {
			System.out.println("Game crashed due to an unhandled exception! Sorry about that...");
		}

		gameManager.cleanup();
		graphicsEngine.cleanup();
		audioEngine.cleanup();
		assetEngine.unloadAll();
		networkEngine.cleanup();
	}

	/** This is called by LibGDX whenever the application loses focus. */
	@Override
	public void pause() {
		applicationHasFocus = false;
	}

	/** This called by LibGDX whenever the application gains focus */
	@Override
	public void resume() {
		applicationHasFocus = true;
	}

	/** Returns true if the application has focus. */
	public boolean isFocused() {
		return applicationHasFocus;
	}
}
