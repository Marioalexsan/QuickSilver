package hg.directors;

import com.badlogic.gdx.math.Vector2;
import hg.engine.InputEngine;
import hg.engine.NetworkEngine;
import hg.entities.PlayerEntity;
import hg.game.ChatSystem;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.gamemodes.Deathmatch;
import hg.gamemodes.Gamemode;
import hg.libraries.MapLibrary;
import hg.networking.PlayerView;
import hg.playerlogic.LocalPlayerLogic;
import hg.playerlogic.LuigiAI;
import hg.playerlogic.NetworkPlayerLogic;
import hg.types.ActorType;
import hg.types.MapType;
import hg.utils.BadCoderException;

import java.io.IOException;

public class MatchDirector extends Director {
    private boolean started = false;
    private boolean stopDecided = false;

    private Gamemode gamemode;

    // TODO Remove this crap and replace it with proper match starting
    public PlayerEntity playerEntity;
    public PlayerEntity enemy;
    public PlayerEntity enemy2;

    public MatchDirector() {

    }

    public void startAsServer() {
        if (started) throw new BadCoderException("Tried to start a started MatchDirector!");

        GameManager manager = HgGame.Manager();
        NetworkEngine network = HgGame.Network();

        try {
            network.startServer();
        }
        catch(IOException ignored) {
            throw new BadCoderException("Server failed!");
        }

        // Probably not needed, but...
        manager.playerViews.forEach(PlayerView::onRemove);
        manager.playerViews.clear();

        manager.localView = manager.createPlayerView(PlayerView.Type.Host);

        HgGame.Manager().addDirector(DirectorTypes.LobbyDirector);

        started = true;
    }

    public void startAsClient() {
        if (started) throw new BadCoderException("Tried to start a started MatchDirector!");

        HgGame.Manager().addDirector(DirectorTypes.LobbyDirector);

        started = true;
    }

    public void startMatch() {
        if (!started) throw new BadCoderException("Tried to start match without being started!");

        GameManager manager = HgGame.Manager();
        NetworkEngine network = HgGame.Network();

        manager.enableChatSystem();

        if (!network.isLocalOrServer()) throw new BadCoderException("Tried to start match as non-host!");

        started = true;
        gamemode = new Deathmatch();

        for (var view : manager.playerViews) {
            if (view.playerEntity != null)
                throw new BadCoderException("Player Entity already exists lool");

            PlayerEntity entity = (PlayerEntity) manager.addActor(ActorType.PlayerEntity, new Vector2(3450, 1500), 0f);

            if (view == manager.localView) entity.setLogic(new LocalPlayerLogic());
            else {
                if (network.isLocalOrServer()) {
                    if (view.viewType == PlayerView.Type.HostAI) entity.setLogic(new LuigiAI());
                    else entity.setLogic(new NetworkPlayerLogic());
                }
                else entity.setLogic(new NetworkPlayerLogic());
            }

            view.playerEntity = entity;
        }

        LevelDirector level = (LevelDirector) manager.addAndGetDirector(DirectorTypes.LevelDirector);
        level.LoadMap(MapLibrary.CreatePrototype(MapType.TestArea01));

        enemy = (PlayerEntity) manager.addActor(ActorType.PlayerEntity, new Vector2(3450, 1800), 0f);
        enemy2 = (PlayerEntity) manager.addActor(ActorType.PlayerEntity, new Vector2(3450, 2100), 0f);

        enemy2.setLogic(new LuigiAI());

        HgGame.Input().addFocusInput(this, InputEngine.FocusPriorities.PlayerInputs);

        HgGame.Manager().addDirector(DirectorTypes.InGameMenu);
    }

    private void stop() {
        if (toBeDestroyed) return;

        GameManager manager = HgGame.Manager();
        NetworkEngine network = HgGame.Network();

        manager.disableChatSystem();

        LevelDirector level = (LevelDirector) manager.getDirector(DirectorTypes.LevelDirector);
        if (level != null) level.UnloadMap();

        manager.clearActors();
        if (manager.localView != null)
            manager.localView.playerEntity = null;
        manager.playerViews.clear();

        network.stopNetwork();

        HgGame.Manager().addDirector(DirectorTypes.MainMenu);
        toBeDestroyed = true;
    }

    public void receiveStop() {
        stopDecided = true;
    }

    @Override
    public void update() {
        boolean hasFocus = HgGame.Input().inputHasFocus(this);

        if (gamemode != null) gamemode.update();

        if (stopDecided) stop();
    }

    @Override
    public void destroy() {
        HgGame.Input().removeFocusInput(this);
    }
}
