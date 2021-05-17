package hg.directors;

import com.badlogic.gdx.math.Vector2;
import hg.engine.InputEngine;
import hg.engine.NetworkEngine;
import hg.entities.PlayerEntity;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.gamelogic.gamemodes.Deathmatch;
import hg.gamelogic.gamemodes.Gamemode;
import hg.interfaces.ICopy;
import hg.libraries.MapLibrary;
import hg.networking.PlayerView;
import hg.networking.packets.GameSessionStart;
import hg.networking.packets.PlayerViewUpdate;
import hg.gamelogic.playerlogic.LocalPlayerLogic;
import hg.gamelogic.playerlogic.LuigiAI;
import hg.gamelogic.playerlogic.NetworkPlayerLogic;
import hg.networking.packets.SessionSettingsUpdate;
import hg.enums.types.ActorType;
import hg.enums.types.DirectorType;
import hg.enums.types.MapType;
import hg.utils.BadCoderException;
import hg.utils.DebugLevels;

/** GameSession is the director that encapsulates the "core" game.
 * GameSession is started once this machine becomes a Server / initialized Client */
public class GameSession extends Director {
    public static class SessionOptions implements ICopy {
        public boolean hardcore = false;
        public int map = MapType.Grinder;

        public SessionOptions() {}

        public SessionOptions(SessionOptions toCopy) {
            hardcore = toCopy.hardcore;
            map = toCopy.map;
        }

        @Override
        public SessionOptions copy() {
            return new SessionOptions(this);
        }
    }

    private SessionOptions settings = new SessionOptions();

    private int matchState = 0;
    private boolean stopDecided = false;

    private Gamemode gamemode;

    public GameSession() {
        HgGame.Manager().enableChatSystem();
    }

    public Gamemode getGamemode() {
        return gamemode;
    }

    public boolean updateSettings(SessionOptions newSettings) {
        if (matchState != 1) {
            HgGame.Manager().setNotice("Can't change game settings outside of lobby!", 60);
            return false;
        }
        settings = new SessionOptions(newSettings);

        if (HgGame.Network().isLocalOrServer()) {
            SessionSettingsUpdate msg = new SessionSettingsUpdate(settings);
            HgGame.Network().sendToAllClients(msg, true);
        }
        return true;
    }

    public SessionOptions getSettings() {
        return new SessionOptions(settings);
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
        level.loadMap(MapLibrary.CreatePrototype(settings.map));

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
            SessionSettingsUpdate msg2 = new SessionSettingsUpdate(settings);
            HgGame.Network().sendToAllClients(msg2, true);

            GameSessionStart msg = new GameSessionStart();
            network.sendToAllClients(msg, true);

            for (var view: manager.getPlayerViews()) {
                PlayerViewUpdate viewMsg = new PlayerViewUpdate(view.uniqueID, view.playerEntity.getID());
                network.sendToAllClients(viewMsg, true);
            }
        }

        gamemode.restart();
        matchState = 2;
        HgGame.Audio().playMusic("Assets/Audio/QuickSilver - Combat Grind.ogg", 1f);
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
        HgGame.Audio().stopMusic();
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
