package hg.directors;

import com.badlogic.gdx.math.Vector2;
import hg.engine.InputEngine;
import hg.engine.NetworkEngine;
import hg.entities.PlayerEntity;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.gamelogic.gamemodes.Deathmatch;
import hg.gamelogic.gamemodes.Gamemode;
import hg.libraries.MapLibrary;
import hg.networking.PlayerView;
import hg.networking.packets.GameSessionStart;
import hg.networking.packets.PlayerViewUpdate;
import hg.gamelogic.playerlogic.LocalPlayerLogic;
import hg.gamelogic.playerlogic.LuigiAI;
import hg.gamelogic.playerlogic.NetworkPlayerLogic;
import hg.types.ActorType;
import hg.types.MapType;
import hg.utils.BadCoderException;

import java.io.IOException;

public class MatchDirector extends Director {
    private boolean started = false;
    private boolean stopDecided = false;

    private Gamemode gamemode;

    public MatchDirector() {
        HgGame.Manager().enableChatSystem();
    }

    public boolean startAsServer() {
        if (started) throw new BadCoderException("Tried to start a started MatchDirector!");

        GameManager manager = HgGame.Manager();
        NetworkEngine network = HgGame.Network();

        try {
            network.startServer();
        }
        catch(IOException ignored) {
            manager.setNotice("Couldn't open server!\nAre ports " + network.getTCPPort() + " TCP and " + network.getUDPPort() + " UDP open?", 180);
            toBeDestroyed = true;
            return false;
        }

        manager.removeAllPlayerViews(); // Probably not needed, but done just in case

        manager.localView = manager.createPlayerView(PlayerView.Type.Host);
        manager.localView.name = HgGame.Game().localName;

        HgGame.Manager().addDirector(DirectorTypes.LobbyDirector);

        started = true;
        return true;
    }

    public boolean startAsClient() {
        if (started) throw new BadCoderException("Tried to start a started MatchDirector!");

        HgGame.Manager().addDirector(DirectorTypes.LobbyDirector);

        started = true;

        return true;
    }

    public void startMatch() {
        if (!started) throw new BadCoderException("Tried to start match without Match Director being started!");

        GameManager manager = HgGame.Manager();
        NetworkEngine network = HgGame.Network();
        boolean isServer = network.isLocalOrServer();

        if (isServer) {
            gamemode = new Deathmatch();

            for (var view : manager.getPlayerViews()) {
                if (view.playerEntity != null) throw new BadCoderException("Player Entity already exists lool");

                PlayerEntity entity = (PlayerEntity) manager.addActor(ActorType.PlayerEntity, new Vector2(3450, 1500), 0f);

                if (view == manager.localView) entity.setLogic(new LocalPlayerLogic());
                else if (network.isLocalOrServer()) {
                    if (view.viewType == PlayerView.Type.HostAI) entity.setLogic(new LuigiAI());
                    else entity.setLogic(new NetworkPlayerLogic());
                }
                else entity.setLogic(new NetworkPlayerLogic());

                view.playerEntity = entity;
            }
        }

        LevelDirector level = (LevelDirector) manager.addAndGetDirector(DirectorTypes.LevelDirector);
        level.LoadMap(MapLibrary.CreatePrototype(MapType.TestArea01));

        HgGame.Input().addFocusInput(this, InputEngine.FocusPriorities.PlayerInputs);

        HgGame.Manager().addDirector(DirectorTypes.InGameMenu);

        if (isServer) {
            // Jojo, it's time to gogo

            GameSessionStart msg = new GameSessionStart();
            network.sendToAllClients(msg, true);

            for (var view: manager.getPlayerViews()) {
                PlayerViewUpdate viewMsg = new PlayerViewUpdate(view.uniqueID, view.playerEntity.getID());
                network.sendToAllClients(viewMsg, true);
            }
        }
    }

    private void stop() {
        if (toBeDestroyed) return;

        GameManager manager = HgGame.Manager();
        NetworkEngine network = HgGame.Network();

        LevelDirector level = (LevelDirector) manager.getDirector(DirectorTypes.LevelDirector);
        if (level != null) level.UnloadMap();

        manager.clearActors();
        if (manager.localView != null)
            manager.localView.playerEntity = null;

        manager.removeAllPlayerViews();

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
        GameManager manager = HgGame.Manager();

        HgGame.Input().removeFocusInput(this);
        manager.disableChatSystem();

        LobbyDirector lobby = (LobbyDirector) manager.getDirector(DirectorTypes.LobbyDirector);
        if (lobby != null) lobby.signalDestroy();
    }
}
