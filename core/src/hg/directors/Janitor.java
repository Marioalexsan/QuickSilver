package hg.directors;

import hg.game.HgGame;

/** GameQuit is rudimentary right now, but it could be used to save settings and stuff later on */
public class Janitor extends Director {
    @Override
    public void destroy() { }

    @Override
    public void update() {
        HgGame.Game().quitGame();
        toBeDestroyed = true;
    }
}