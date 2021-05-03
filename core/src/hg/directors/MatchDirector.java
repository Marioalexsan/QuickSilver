package hg.directors;

import com.badlogic.gdx.math.Vector2;
import hg.engine.InputEngine;
import hg.engine.MappedAction;
import hg.entities.Player;
import hg.game.EntityManager;
import hg.game.HgGame;
import hg.libraries.MapLibrary;
import hg.playerlogic.LocalPlayerLogic;
import hg.playerlogic.LuigiAI;
import hg.types.ActorType;
import hg.types.MapType;

import java.awt.*;

public class MatchDirector extends Director {
    private boolean started = false;
    private boolean menuDecidedStop = false;

    // TODO Remove this crap and replace it with proper match starting
    public Player player;
    public Player enemy;
    public Player enemy2;

    private void start() {
        started = true;
        EntityManager manager = HgGame.Entities();

        LevelDirector level = (LevelDirector) manager.addAndGetDirector(DirectorTypes.LevelDirector);
        level.LoadMap(MapLibrary.CreatePrototype(MapType.TestArea01));

        player = (Player) manager.addActor(ActorType.Player, new Vector2(3450, 1500), 0f);
        player.setLogic(new LocalPlayerLogic());

        enemy = (Player) manager.addActor(ActorType.Player, new Vector2(3450, 1800), 0f);
        enemy2 = (Player) manager.addActor(ActorType.Player, new Vector2(3450, 2100), 0f);

        enemy2.setLogic(new LuigiAI());

        manager.setLocalPlayer(player);
        HgGame.Input().addFocusInput(this, InputEngine.FocusPriorities.PlayerInputs);
    }

    private void stop() {
        if (toBeDestroyed) return;

        EntityManager manager = HgGame.Entities();

        LevelDirector level = (LevelDirector) manager.getDirector(DirectorTypes.LevelDirector);
        if (level != null) level.UnloadMap();

        manager.clearActors();
        manager.setLocalPlayer(null);

        HgGame.Entities().addDirector(DirectorTypes.MainMenuDirector);
        toBeDestroyed = true;
    }

    public void receiveStop() {
        menuDecidedStop = true;
    }

    private void pauseMenu() {
        HgGame.Entities().addDirector(DirectorTypes.InGameMenuDirector);
    }

    @Override
    public void clientUpdate() {
        serverUpdate();
    }

    @Override
    public void serverUpdate() {
        if (!started) start();

        boolean hasFocus = HgGame.Input().inputHasFocus(this);

        if (hasFocus && HgGame.Input().isActionTapped(MappedAction.Escape))
            pauseMenu();

        if (menuDecidedStop) stop();
    }

    @Override
    public void destroy() {
        HgGame.Input().removeFocusInput(this);
    }
}
