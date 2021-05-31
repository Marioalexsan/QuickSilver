package hg.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import hg.game.HgGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.forceExit = false; // Application calls System.exit(-1) on Gdx.app.exit() if this is set to true
		config.pauseWhenBackground = true; // Allows the game to know when the window loses focus
		config.pauseWhenMinimized = false; // Game updates should not be stopped for any reason
		config.backgroundFPS = 60;
		config.foregroundFPS = 60;
		config.samples = 4;
		config.vSyncEnabled = true;

		new LwjglApplication(new HgGame(), config); // Start game
	}
}
