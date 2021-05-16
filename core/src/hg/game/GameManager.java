package hg.game;


import com.badlogic.gdx.math.Vector2;
import hg.directors.*;
import hg.drawables.BasicText;
import hg.drawables.DrawLayer;
import hg.engine.NetworkEngine;
import hg.entities.Entity;
import hg.entities.PlayerEntity;
import hg.enums.HPos;
import hg.enums.VPos;
import hg.gamelogic.BaseStats;
import hg.gamelogic.gamemodes.Gamemode;
import hg.gamelogic.states.State;
import hg.libraries.ActorLibrary;
import hg.libraries.BuilderLibrary;
import hg.libraries.EnvironmentLibrary;
import hg.networking.NetworkRole;
import hg.networking.NetworkStatus;
import hg.networking.PlayerView;
import hg.networking.packets.*;
import hg.types.DirectorType;
import hg.types.TargetType;
import hg.utils.*;

import java.text.DecimalFormat;
import java.util.*;

/** Holds various stuff relevant to the game, such as entities */
public class GameManager {
    private static final int StartingID = 1;
    private int nextEntityID = StartingID;
    private int nextStaticEnvID = StartingID; // Is this needed?

    private final HashMap<Integer, Entity> actors = new HashMap<>();
    private final HashMap<Integer, Entity> environments = new HashMap<>();
    private final HashMap<Integer, Director> directors = new HashMap<>();

    public PlayerView localView;
    private final ArrayList<PlayerView> playerViews = new ArrayList<>();

    private final ChatSystem chatSystem;
    private final BasicText debug_mouseWorldPosition;

    private final BasicText notice;
    private int noticeTimeLeft;

    // Network Update stuff

    private int actorHeavyUpdateInterval = 12;
    private int actorNextHeavyUpdate = 0;

    private int gamemodeHeavyUpdateInterval = 30;
    private int gamemodeNextHeavyUpdate = 0;

    public GameManager() {
        chatSystem = new ChatSystem();
        chatSystem.setEnabled(false);
        chatSystem.getPosition().set(-650, -500);

        debug_mouseWorldPosition = BuilderLibrary.BasicTextBuilders("label").textPos(HPos.Left, VPos.Top).makeGUI().build();
        debug_mouseWorldPosition.setPositionOffset(new Vector2(80, -80));
        debug_mouseWorldPosition.setEnabled(false);
        debug_mouseWorldPosition.registerToEngine();
        // Notice - used for error messages

        notice = new BasicText(HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), "");
        notice.setConstraints(HPos.Center, VPos.Center, 0f);
        notice.setAlpha(0f);
        notice.setCameraUse(false);
        notice.setLayer(DrawLayer.GUIDefault);
        notice.registerToEngine();
        noticeTimeLeft = 0;
    }

    public void enableChatSystem() {
        chatSystem.setEnabled(true);
    }

    public void disableChatSystem() {
        chatSystem.setEnabled(false);
        chatSystem.clear();
    }

    public void setNotice(String text, int noticeTime) {
        notice.setText(text);
        noticeTimeLeft = noticeTime;
    }

    public ChatSystem getChatSystem() {
        return chatSystem;
    }

    // ---
    // Player Views
    // ---

    /** Creates a PlayerView and adds it to the local PlayerView list */
    public PlayerView createPlayerView(PlayerView.Type type) {
        if (!HgGame.Network().isLocalOrServer()) throw new BadCoderException("Non-server tried to create player views lol");

        PlayerView view = new PlayerView();
        view.uniqueID = generatePlayerViewID();
        view.viewType = type;

        playerViews.add(view);
        return view;
    }

    /** Adds an existing PlayerView to the local PlayerView list */
    public void addPlayerView(PlayerView newView) {
        for (var view : playerViews)
            if (view.uniqueID == newView.uniqueID) throw new BadCoderException("ID conflict when adding player view!");
        playerViews.add(newView);
    }

    /** Removes and returns the PlayerView with that ID from the local PlayerView list. */
    public PlayerView removePlayerView(int uniqueID) {
        PlayerView target = null;
        for (var view : playerViews) {
            if (view.uniqueID == uniqueID) {
                target = view;
                break;
            }
        }

        if (target == null) return null;
        if (target == localView)
            chatSystem.addDebugMessage("Someone tried to remove localview via uniqueID!", DebugLevels.Error);

        playerViews.remove(target);
        return target;
    }

    /** The nuclear option. */
    public void removeAllPlayerViews() {
        localView = null;
        playerViews.clear();
    }

    /** Returns an ID that is not in use by any other PlayerView right now. Should be used by Server only. */
    private int generatePlayerViewID() {
        Random rand = new Random();
        boolean isUnique = false;
        int ID = 0;
        while (!isUnique) {
            isUnique = true;
            ID = rand.nextInt();

            for (var view : playerViews) {
                if (view.uniqueID == ID) {
                    isUnique = false;
                    break;
                }
            }
        }
        return ID;
    }

    public PlayerView getPlayerViewByUniqueID(int uniqueID) {
        for (var view: playerViews) {
            if (view.uniqueID == uniqueID) return view;
        }
        return null;
    }

    public PlayerView getPlayerViewByConnectionID(int connectionID) {
        for (var view: playerViews) {
            if (view.connectionID != -1 && view.connectionID == connectionID) return view;
        }
        return null;
    }

    public PlayerView getPlayerViewByActorID(int actorID) {
        for (var view: playerViews) {
            if (view.playerEntity != null && view.playerEntity.getID() == actorID) return view;
        }
        return null;
    }

    public PlayerView[] getPlayerViews() {
        return playerViews.toArray(new PlayerView[0]);
    }

    public ArrayList<Entity> getAllActors() {
        return new ArrayList<>(actors.values());
    }

    public Entity[] getDeadActors() {
        ArrayList<Entity> deadEntities = new ArrayList<>();
        for (var actor: actors.values()) {
            BaseStats stats = actor.getStats();
            if (stats != null && stats.isDead)
                deadEntities.add(actor);
        }
        return deadEntities.toArray(new Entity[0]);
    }

    public PlayerEntity[] getDeadPlayerEntities() {
        Entity[] deadEntities = getDeadActors();
        ArrayList<PlayerEntity> deadPlayers = new ArrayList<>();
        for (var actor: deadEntities) {
            if (actor instanceof PlayerEntity)
                deadPlayers.add((PlayerEntity) actor);
        }
        return deadPlayers.toArray(new PlayerEntity[0]);
    }

    public Entity addActor(int entityType, Vector2 position, float direction) {
        return addActor(nextEntityID++, entityType, position, direction);
    }

    public Entity addActor(int ID, int entityType, Vector2 position, float direction) {
        Entity newEntity = ActorLibrary.CreateActor(entityType);
        if (newEntity != null) {
            newEntity.getPosition().set(position);
            newEntity.getAngle().set(direction);

            if (actors.put(ID, newEntity) != null) {
                chatSystem.addDebugMessage("Trying to add an entity with an existing ID!", DebugLevels.Warn);
            }
            newEntity.setID(ID);
        }

        NetworkEngine network = HgGame.Network();
        if (newEntity != null && network.isLocalOrServer()) {
            EntityAdded msg = new EntityAdded(TargetType.Actors, entityType, ID, position.x, position.y, direction);
            network.sendToAllClients(msg, true);
        }

        return newEntity;
    }

    public Entity getActor(int ID) {
        return actors.get(ID);
    }

    public Entity addEnvironment(int envType, Vector2 position, float direction) {
        return addEnvironment(nextStaticEnvID++, envType, position, direction);
    }

    public Entity addEnvironment(int ID, int envType, Vector2 position, float direction) {
        Entity newEntity = EnvironmentLibrary.CreateEnvironment(envType);

        newEntity.getPosition().set(position);
        newEntity.getAngle().set(direction);

        if (environments.put(ID, newEntity) != null) {
            chatSystem.addDebugMessage("Trying to add an environment with an existing ID!", DebugLevels.Warn);
        }
        newEntity.setID(ID);

        return newEntity;
    }

    /** Tries to add a director of the given type, or returns the existing one if applicable. */
    public Director tryAddDirector(int type) {
        Director which = directors.get(type);
        if (which != null) return which;

        switch (type) { // TODO Replace switch with dictionary
            case DirectorType.GameInit -> which = new GameInit();
            case DirectorType.GameQuit -> which = new GameQuit();
            case DirectorType.MainMenu -> which = new MainMenu();
            case DirectorType.GameSession -> which = new GameSession();
            case DirectorType.Level -> which = new Level();
            case DirectorType.PauseMenu -> which = new PauseMenu();
            case DirectorType.LobbyMenu -> which = new LobbyMenu();
            default -> throw new RuntimeException("Coudln't retrieve director of type " + type);
        }
        directors.put(type, which);
        return which;
    }

    /** Returns an existing director of the given type, if applicable. */
    public Director getDirector(int type) {
        return directors.get(type);
    }

    /** Marks an existing director of the given type for destruction, if applicable. */
    public void tryRetireDirector(int type) {
        Director existing = directors.remove(type);
        if (existing != null) existing.destroy();
    }

    /** Clears all entities. This also resets the nextEntityID */
    public void clearEntities() {
        clearActors();
        clearStaticEnvironments();
        nextEntityID = StartingID;
        nextStaticEnvID = StartingID;
    }

    public void clearActors() {
        for (var actor: actors.entrySet()) {
            actor.getValue().signalDestroy();
            actor.getValue().destroy();
        }
        actors.clear();
    }

    public void clearStaticEnvironments() {
        for (var environment: environments.entrySet()) {
            environment.getValue().signalDestroy();
            environment.getValue().destroy();
        }
        environments.clear();
    }

    public void clearDirectors() {
        for (Map.Entry<Integer, Director> director: directors.entrySet()) {
            director.getValue().signalDestroy();
            director.getValue().destroy();
        }
        directors.clear();
    }

    public void update() {
        // Update all current entities based on network status
        NetworkEngine network = HgGame.Network();

        boolean isServer = network.isLocalOrServer();

        chatSystem.onUpdate();
        if (noticeTimeLeft > -60) {
            notice.setAlpha(Math.min(noticeTimeLeft + 60, 60) / 60f);
            noticeTimeLeft--;
        }

        DecimalFormat noDigits = new DecimalFormat();
        noDigits.setMaximumFractionDigits(0);

        debug_mouseWorldPosition.setPosition(HgGame.Input().getMouse());
        var pos = HgGame.Input().getFOVWorldMouse(HgGame.Game().getFOVFactor());
        debug_mouseWorldPosition.setText(noDigits.format(pos.x) + " " + noDigits.format(pos.y));

        for (var director : new ArrayList<>(directors.values()))
            director.update();

        for (var actor : new ArrayList<>(actors.values()))
            actor.update();

        // Directors

        LinkedList<Integer> directorsToRemove = new LinkedList<>();
        for (Map.Entry<Integer, Director> director : directors.entrySet())
            if (director.getValue().isDestroySignalled())
                directorsToRemove.add(director.getKey());

        for (Integer key : directorsToRemove) directors.remove(key).destroy();

        // Actors

        LinkedList<Integer> actorsToRemove = new LinkedList<>();
        for (var actor : actors.entrySet())
            if (actor.getValue().isDestroySignalled())
                actorsToRemove.add(actor.getKey());

        for (var key : actorsToRemove) actors.remove(key).destroy();
        if (isServer) {
            for (var key : actorsToRemove) {
                network.sendToAllClients(new EntityDestroyed(TargetType.Actors, key), true);
            }
        }

        // Environments

        LinkedList<Integer> environmentsToRemove = new LinkedList<>();
        for (var environment : environments.entrySet())
            if (environment.getValue().isDestroySignalled())
                environmentsToRemove.add(environment.getKey());

        for (var key : environmentsToRemove) environments.remove(key).destroy();


        // Send network updates if Server

        if (isServer) {
            if (actorNextHeavyUpdate-- <= 0) {
                actorNextHeavyUpdate = actorHeavyUpdateInterval;
                for (var actor: actors.entrySet()) {
                    State stuff = actor.getValue().tryGenerateState();
                    if (stuff != null)
                        network.sendToAllClients(new StateUpdate(TargetType.Actors, actor.getKey(), stuff), false); // UDP Packet
                }
            }
            else {
                if (actorNextHeavyUpdate % 3 == 0) { // A packet every three frames
                    for (var actor: actors.entrySet()) {
                        Vector2 position = actor.getValue().getPosition();
                        Angle angle = actor.getValue().getAngle();
                        network.sendToAllClients(new PositionUpdate(actor.getKey(), position.x, position.y, angle.getDeg()), false); // UDP Packet
                    }
                }
            }

            if (gamemodeNextHeavyUpdate-- <= 0) {
                gamemodeNextHeavyUpdate = gamemodeHeavyUpdateInterval;
                GameSession match = (GameSession) getDirector(DirectorType.GameSession);
                Gamemode mode = null;
                State stuff = null;
                if (match != null) mode = match.getGamemode();
                if (mode != null) stuff = mode.tryGenerateState();
                if (stuff != null)
                    network.sendToAllClients(new StateUpdate(TargetType.Gamemodes, -1337, stuff), false); // UDP Packet
            }

        }
    }

    public void cleanup() {
        clearActors();
        clearStaticEnvironments();
        clearDirectors();
        notice.unregisterFromEngine();
        debug_mouseWorldPosition.unregisterFromEngine();
    }

    public void onChatMessageEntered(String message) {
        if (message.length() > 0 && message.charAt(0) == '/') {
            String[] parts = message.substring(1).split(" ");
            for (int i = 0; i < parts.length; i++)
                parts[i] = parts[i].trim();
            parseCommand(parts[0], parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0]);
        }
        else {
            ChatMessage broadcast = new ChatMessage(message, localView.uniqueID);
            // Send in network
            if (HgGame.Network().isLocalOrServer()) {
                for (var view : playerViews) {
                    if (view != localView) HgGame.Network().sendToClient(broadcast, true, view.connectionID);
                }
            }
            else {
                HgGame.Network().sendToServer(broadcast, true);
            }
        }
    }

    public void parseCommand(String command, String[] args) {
        if (command.length() == 0) return;

        NetworkEngine network = HgGame.Network();
        boolean isServer = network.isLocalOrServer();

        String casualPrefix = "[" + command + "] ";
        switch (command) {
            case "debugColliders" -> {
                boolean debugDraw = HgGame.Physics().getDebugDraw();
                HgGame.Physics().setDebugDraw(!debugDraw);
                chatSystem.addMessage(casualPrefix + "Debug draw is now " + (debugDraw ? "off" : "on"));
            }
            case "listPlayers" -> {
                for (var view: playerViews) {
                    chatSystem.addMessage(view.name + "( uID: " + view.uniqueID + " | cID: " + view.connectionID + " )");
                }
            }
            case "kickPlayer" -> {
                if (!isServer) {
                    chatSystem.addMessage("Can't kick players as Client");
                    return;
                }
                try {
                    int uniqueID = Integer.parseInt(args[0]);
                    PlayerView toKick = getPlayerViewByUniqueID(uniqueID);
                    if (toKick == localView) {
                        chatSystem.addMessage("You can't kick yourself!");
                        return;
                    }
                    if (toKick == null) {
                        chatSystem.addMessage("The uniqueID is invalid");
                        return;
                    }
                    chatSystem.addMessage("Kicking " + toKick.name);
                    network.disconnectClient(toKick.connectionID);
                }
                catch (Exception ignored) {
                    chatSystem.addMessage("Usage: /kickPlayer [uniqueID]");
                }
            }
            case "mousePos" -> {
                debug_mouseWorldPosition.setEnabled(!debug_mouseWorldPosition.isActive());
                chatSystem.addMessage("Toggled mouse position view");
            }
            default -> chatSystem.addMessage("[System] Unknown command: " + command);
        }
    }

    public void networkUpdate() {
        NetworkEngine network = HgGame.Network();
        var newMessages = network.dumpMessages();

        // Parse connections

        if (network.getNetRole() == NetworkRole.Local && network.getNetStatus() == NetworkStatus.GotDisconnectedAsClient) {
            network.clearStatus();
            onDisconnect();
        }

        if (network.isLocalOrServer()) {
            var newConnections = network.dumpConnectedClients();
            var newDisconnects = network.dumpDisconnectedClients();

            // Process disconnects, then connects
            for (var connection: newDisconnects) {
                onClientDisconnected(connection);
            }

            for (var connection: newConnections) {
                onClientConnected(connection);
            }

            for (var msg: newMessages) msg.packet.parseOnServer(msg.connectionID);
        }
        else {
            for (var msg: newMessages) msg.packet.parseOnClient();
        }
    }


    /** Called if this machine sent PlayerViews to a client */
    public void onClientInitialized(int uniqueID) {
        PlayerView newView = getPlayerViewByUniqueID(uniqueID);
        if (newView == null) {
            chatSystem.addDebugMessage("Null PlayerView after addition!", DebugLevels.Error);
            return;
        }
        getChatSystem().addMessage(newView.name + " connected.");
    }

    /** Called if a client connected to this machine */
    public void onClientConnected(int connectionID) {
    }

    /** Called if a client disconnected from this machine */
    public void onClientDisconnected(int connectionID) {
        // Check if this client was initialized
        PlayerView deadView = getPlayerViewByConnectionID(connectionID);

        if (deadView != null) {
            removePlayerView(deadView.uniqueID);

            if (deadView.playerEntity != null)
                deadView.playerEntity.signalDestroy();

            PlayerViewDisconnected msg = new PlayerViewDisconnected(deadView.uniqueID);

            for (var view: playerViews)
                if (view != localView) HgGame.Network().sendToClient(msg, true, view.connectionID);

            HgGame.Manager().getChatSystem().addMessage(deadView.name + " disconnected.");
        }
    }

    /** Called if this machine obtained PlayerViews from the server */
    public void onInitializedByServer() {

    }

    /** Called if this machine connected to a server */
    public void onConnect() {

    }

    /** Called if this machine disconnected from a server */
    public void onDisconnect() {
        GameSession match = (GameSession) getDirector(DirectorType.GameSession);
        if (match != null) match.signalStop();

        MainMenu main = (MainMenu) getDirector(DirectorType.MainMenu);
        if (main != null) main.onDisconnectFromServer();

        setNotice("Got disconnected from server!", 120);
    }
}
