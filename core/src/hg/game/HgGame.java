package hg.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import hg.enums.types.DirectorType;
import hg.drawables.*;
import hg.engine.*;
import hg.entities.PlayerEntity;
import hg.utils.BadCoderException;
import hg.utils.MathTools;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


// Project made by Miron Alexandru, 1208B

/** HgGame centralizes the game subsystems and updates each of them 60 times per second. */
public class HgGame extends ApplicationAdapter {

	// A single access point for the game engine is provided here.
	// This may be taboo, but it is infinitely better than the alternatives.

	private static HgGame _instance;
	public static HgGame Game() { return _instance; }

	public static AudioEngine Audio() { return _instance.audioEngine; }
	public static AssetEngine Assets() { return _instance.assetEngine; }
	public static CollisionEngine Physics() { return _instance.collisionEngine; }
	public static GraphicsEngine Graphics() { return _instance.graphicsEngine; }
	public static InputEngine Input() { return _instance.inputEngine; }
	public static NetworkEngine Network() { return _instance.networkEngine; }

	public static GameManager Manager() { return _instance.gameManager; }
	public static HUDManager GUI() { return _instance.hudManager; }
	public static DataManager Data() { return _instance.dataManager; }
	public static ChatSystem Chat() { return _instance.hudManager.getChatSystem(); }
	public static void SetNotice(String notice, int timeout) { _instance.hudManager.setNotice(notice, timeout); }

	private AudioEngine audioEngine;
	private AssetEngine assetEngine;
	private CollisionEngine collisionEngine;
	private GraphicsEngine graphicsEngine;
	private InputEngine inputEngine;
	private NetworkEngine networkEngine;

	private GameManager gameManager;
	private HUDManager hudManager;
	private DataManager dataManager;

	private boolean crashed = false;
	private Throwable crash = null;

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
		factorFOV = MathTools.Clamp(fov, 0f, 0.8f);
	}

	public void quitGame() {
		quitCalled = true;
	}

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
		networkEngine = new NetworkEngine();

		gameManager = new GameManager();
		hudManager = new HUDManager();
		dataManager = new DataManager();

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

		audioEngine.setGlobalSoundVolume(0.5f);
		audioEngine.setGlobalMusicVolume(0.5f);

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

			hudManager.update();
			graphicsEngine.render();
			audioEngine.update();

			networkEngine.update();

			frameCounter++;
		}
		catch (Throwable crash) {
			crash.printStackTrace();
			this.crash = crash;
			crashed = true;
		}

		if (crashed || quitCalled) Gdx.app.exit();
	}

	/** This does cleanup on exit */
	@Override
	public void dispose () {
		if (crashed) {
			var time = LocalDateTime.now();
			System.out.println("Game crashed due to an unhandled exception! Sorry about that...");
			try {
				try {
					Files.createDirectory(Path.of("CrashLogs"));
				} catch (Exception ignored) {}

				var compatTime = time.truncatedTo(ChronoUnit.SECONDS).toString().replace(" ", "-").replace(":", "-").replace("T", " at ");
				var logPath = Path.of("CrashLogs", "QSCrashLog " + compatTime + ".txt");

				try {
					Files.delete(logPath);
				}
				catch (Throwable ignored) {}
				Files.createFile(logPath);
				try (BufferedWriter log = new BufferedWriter(new FileWriter(logPath.toString()))) {
					log.write("A crash occured at the following time: " + compatTime + "!\n\n");
					log.write("Exception information:\n");
					crash.printStackTrace(new PrintWriter(log));
					log.write("\nGame Settings used:\n");
					for (var setting: GameVars.GetAllDefaultSettings().keySet()) {
						log.write("  " + setting + " - " + dataManager.getSetting(setting) + "\n");
					}
				}
			}
			catch (Throwable ignored) {}
		}

		hudManager.cleanup();
		gameManager.cleanup();
		graphicsEngine.cleanup();
		audioEngine.cleanup();
		assetEngine.unloadAll();
		networkEngine.cleanup();
		dataManager.cleanup();
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
