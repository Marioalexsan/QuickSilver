package hg.directors;

import com.badlogic.gdx.Gdx;
import hg.game.HgGame;
import hg.libraries.EnvironmentLibrary;

public class QuitDirector extends Director {
    @Override
    public void destroy() {

    }

    @Override
    public void clientUpdate() {
        serverUpdate();
    }

    @Override
    public void serverUpdate() {
        HgGame.Game().quitGame();
        toBeDestroyed = true;
    }
}