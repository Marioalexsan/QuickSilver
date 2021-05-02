package hg.directors;

import com.badlogic.gdx.math.Vector2;
import hg.engine.MappedAction;
import hg.entities.Player;
import hg.game.EntityManager;
import hg.game.HgGame;
import hg.libraries.MapLibrary;
import hg.playerlogic.LocalPlayerLogic;
import hg.playerlogic.LuigiAI;
import hg.types.ActorType;
import hg.types.MapType;

public class MatchDirector extends Director {
    private boolean started = false;

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

    @Override
    public void clientUpdate() {
        serverUpdate();
    }

    @Override
    public void serverUpdate() {
        if (!started) start();

        if (HgGame.Input().isActionTapped(MappedAction.Escape))
            stop();
    }

    @Override
    public void destroy() {

    }
}
