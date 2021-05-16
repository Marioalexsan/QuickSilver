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
import hg.types.DirectorType;
import hg.types.MapType;
import hg.utils.BadCoderException;
import hg.utils.DebugLevels;

/** GameSession is the director that encapsulates the "core" game.
 * GameSession is started once this machine becomes a Server / initialized Client */
public class GameSession extends Director {
    private int matchState = 0;
    private boolean stopDecided = false;

    private Gamemode gamemode;

    public GameSession() {
        HgGame.Manager().enableChatSystem();
    }

    public Gamemode getGamemode() {
        return gamemode;
    }

    public void startLobby() {
        GameManager manager = HgGame.Manager();
        NetworkEngine network = HgGame.Network();

        if (matchState == 1) {
            manager.getChatSystem().addDebugMessage("Tried to enter lobby while already in lobby", DebugLevels.Error);
            return;
        }

        boolean isServer = network.isLocalOrServer();

        if (isServer) {
            manager.localView = manager.createPlayerView(PlayerView.Type.Host);
            manager.localView.name = HgGame.Game().localName;
        }

        manager.tryAddDirector(DirectorType.LobbyMenu);

        matchState = 1;
    }

    public void startMatch() {
        GameManager manager = HgGame.Manager();
        NetworkEngine network = HgGame.Network();
        InputEngine input = HgGame.Input();

        if (matchState == 2) {
            manager.getChatSystem().addDebugMessage("Tried to start a started match", DebugLevels.Error);
            return;
        }

        manager.tryRetireDirector(DirectorType.LobbyMenu);

        Level level = (Level) manager.tryAddDirector(DirectorType.Level);
        level.loadMap(MapLibrary.CreatePrototype(MapType.TestArea01));

        boolean isServer = network.isLocalOrServer();

        gamemode = new Deathmatch();
        if (isServer) {
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

        input.addFocusInput(this, InputEngine.FocusPriorities.PlayerInputs);
        manager.tryAddDirector(DirectorType.PauseMenu);

        if (isServer) {
            // Jojo, it's time to gogo
            GameSessionStart msg = new GameSessionStart();
            network.sendToAllClients(msg, true);

            for (var view: manager.getPlayerViews()) {
                PlayerViewUpdate viewMsg = new PlayerViewUpdate(view.uniqueID, view.playerEntity.getID());
                network.sendToAllClients(viewMsg, true);
            }
        }

        gamemode.restart();
        matchState = 2;
    }

    private void stop() {
        if (toBeDestroyed) return;

        GameManager manager = HgGame.Manager();
        NetworkEngine network = HgGame.Network();

        Level level = (Level) manager.getDirector(DirectorType.Level);
        if (level != null) level.unloadMap();

        manager.clearActors();
        if (manager.localView != null)
            manager.localView.playerEntity = null;

        manager.removeAllPlayerViews();

        network.stopNetwork();

        HgGame.Manager().tryAddDirector(DirectorType.MainMenu);

        matchState = -1;

        toBeDestroyed = true;
    }

    public void signalStop() {
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

        LobbyMenu lobby = (LobbyMenu) manager.getDirector(DirectorType.LobbyMenu);
        if (lobby != null) lobby.signalDestroy();
    }
}
