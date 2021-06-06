package hg.game;


import com.badlogic.gdx.math.Vector2;
import hg.directors.*;
import hg.drawables.Animation;
import hg.drawables.gfxeffects.GFXEffect;
import hg.engine.NetworkEngine;
import hg.entities.Entity;
import hg.entities.PlayerEntity;
import hg.gamelogic.BaseStats;
import hg.gamelogic.gamemodes.Gamemode;
import hg.gamelogic.ObjectState;
import hg.libraries.ActorLibrary;
import hg.libraries.EnvironmentLibrary;
import hg.networking.NetworkRole;
import hg.networking.NetworkStatus;
import hg.networking.PlayerView;
import hg.networking.packets.*;
import hg.enums.DirectorType;
import hg.enums.TargetType;
import hg.utils.*;

import java.util.*;

/** Holds various stuff relevant to the game, such as entities, directors, player views, etc.
 * Also does a crapton of network processing.
 * Could be considered the heart of the game. */
public class GameManager {
    private static final int StartingID = 1;
    private int nextEntityID = StartingID;
    private int nextStaticEnvID = StartingID; // Is this needed?

    private final HashMap<Integer, Entity> actors = new HashMap<>();
    private final HashMap<Integer, Entity> environments = new HashMap<>();
    private final HashMap<Integer, Director> directors = new HashMap<>();

    private final HashSet<GFXEffect> updateableGFXEffects = new HashSet<>();

    public PlayerView localView;
    private final ArrayList<PlayerView> playerViews = new ArrayList<>();

    // Network Update stuff

    private int actorHeavyUpdateInterval = 12;
    private int actorNextHeavyUpdate = 0;

    private int gamemodeHeavyUpdateInterval = 30;
    private int gamemodeNextHeavyUpdate = 0;

    public GameManager() { }

    // --- GFX Effects --- //

    // These are technically not really vital

    /** Adds the GFXEffect to the list of effects managed by GameManager.
     * GameManager updates effects each frame, until they expire. */
    public void addGFXEffectToManage(GFXEffect effect) {
        if (effect == null) return;
        updateableGFXEffects.add(effect);
        effect.registerToEngine();
    }

    public void dropGFXEffect(GFXEffect effect) {
        if (effect == null) return;
        updateableGFXEffects.remove(effect);
        effect.unregisterFromEngine();
    }

    // --- Player View related Methods --- //

    /** Creates a PlayerView and adds it to the local list. Used by Servers. */
    public PlayerView createPlayerView(PlayerView.Type type) {
        if (!HgGame.Network().isLocalOrServer()) throw new BadCoderException("Non-server tried to create player views lol");

        PlayerView view = new PlayerView();
        view.uniqueID = generatePlayerViewID();
        view.viewType = type;

        playerViews.add(view);

        GameSession match = (GameSession) getDirector(DirectorType.GameSession);
        Gamemode mode = match != null ? match.getGamemode() : null;
        if (mode != null) mode.onPlayerViewAdded(view);

        return view;
    }

    /** Adds an existing PlayerView to the local list. Used by Clients. */
    public void addPlayerView(PlayerView newView) {
        PlayerView existingView = getPlayerViewByUniqueID(newView.uniqueID);
        if (existingView != null)
            throw new BadCoderException("ID conflict when adding player view!");
        playerViews.add(newView);
    }

    /** Removes the PlayerView with the given unique ID.
     * @return The removed PlayerView, if any. */
    public PlayerView removePlayerView(int uniqueID) {
        PlayerView target = getPlayerViewByUniqueID(uniqueID);

        if (target == null) return null;
        if (target == localView) {
            HgGame.Chat().addDebugMessage("Someone tried to remove localview via uniqueID!", DebugLevels.Error);
            return null;
        }

        GameSession match = (GameSession) getDirector(DirectorType.GameSession);
        Gamemode mode = match != null ? match.getGamemode() : null;
        if (mode != null) mode.onPlayerViewRemoved(target);

        playerViews.remove(target);
        return target;
    }

    /** The nuclear option. */
    public void removeAllPlayerViews() {
        GameSession match = (GameSession) getDirector(DirectorType.GameSession);
        Gamemode mode = match != null ? match.getGamemode() : null;
        if (mode != null) {
            for (var view: playerViews)
                mode.onPlayerViewRemoved(view);
        }
        localView = null;
        playerViews.clear();
    }

    /** Returns an ID that is not in use by existing PlayerViews. */
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
        for (var view: playerViews)
            if (view.uniqueID == uniqueID) return view;
        return null;
    }

    public PlayerView getPlayerViewByConnectionID(int connectionID) {
        for (var view: playerViews)
            if (view.connectionID != -1 && view.connectionID == connectionID) return view;
        return null;
    }

    public PlayerView getPlayerViewByActorID(int actorID) {
        for (var view: playerViews)
            if (view.playerEntity != null && view.playerEntity.getID() == actorID) return view;
        return null;
    }

    public PlayerView[] getPlayerViews() {
        return playerViews.toArray(new PlayerView[0]);
    }

    // --- Actor related methods --- //

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
                HgGame.Chat().addDebugMessage("Trying to add an entity with an existing ID!", DebugLevels.Warn);
                HgGame.Chat().addDebugMessage("Entity ID: " + ID + ", Type: " + entityType, DebugLevels.Warn);
            }
            newEntity.setID(ID);
        }

        GameSession match = (GameSession) getDirector(DirectorType.GameSession);
        Gamemode mode = match != null ? match.getGamemode() : null;
        if (mode != null) mode.onEntityAdded(newEntity);

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

    // --- Environment related methods --- //

    public Entity addEnvironment(int envType, Vector2 position, float direction) {
        return addEnvironment(nextStaticEnvID++, envType, position, direction);
    }

    public Entity addEnvironment(int ID, int envType, Vector2 position, float direction) {
        Entity newEntity = EnvironmentLibrary.CreateEnvironment(envType);

        newEntity.getPosition().set(position);
        newEntity.getAngle().set(direction);

        if (environments.put(ID, newEntity) != null) {
            HgGame.Chat().addDebugMessage("Trying to add an environment with an existing ID!", DebugLevels.Warn);
        }
        newEntity.setID(ID);

        return newEntity;
    }

    // --- Generic Director related methods --- //

    /** Tries to add a director of the given type, or returns the existing one if applicable. */
    public Director tryAddDirector(int type) {
        Director which = directors.get(type);
        if (which != null) return which;

        switch (type) { // TODO Replace switch with dictionary
            case DirectorType.Starter -> which = new Starter();
            case DirectorType.Janitor -> which = new Janitor();
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

    // --- Clear methods --- //

    /** Clears all entities. This also resets the nextEntityID */
    public void clearEntities() {
        clearActors();
        clearStaticEnvironments();
        nextEntityID = StartingID;
        nextStaticEnvID = StartingID;
    }

    public void clearActors() {
        GameSession match = (GameSession) getDirector(DirectorType.GameSession);
        Gamemode mode = match != null ? match.getGamemode() : null;

        for (var actor: actors.entrySet()) {
            if (mode != null) mode.onEntityRemoved(actor.getValue());
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

    // --- Other --- //

    public void update() {
        // Update all current entities based on network status
        NetworkEngine network = HgGame.Network();
        GameSession match = (GameSession) getDirector(DirectorType.GameSession);
        Gamemode mode = match != null ? match.getGamemode() : null;

        boolean isServer = network.isLocalOrServer();
        
        for (var director : new ArrayList<>(directors.values()))
            director.update();

        for (var actor : new ArrayList<>(actors.values()))
            actor.update();

        for (var effect: updateableGFXEffects)
            effect.update();

        // GFXEffects

        LinkedList<GFXEffect> effectsToRemove = new LinkedList<>();
        for (GFXEffect effect : updateableGFXEffects)
            if (effect.isExpired())
                effectsToRemove.add(effect);

        for (GFXEffect effect : effectsToRemove) {
            updateableGFXEffects.remove(effect);
            effect.unregisterFromEngine();
        }

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

        for (var key : actorsToRemove) {
            var actor = actors.remove(key);
            if (mode != null) mode.onEntityRemoved(actor);
            actor.destroy();
        }
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
                    ObjectState stuff = actor.getValue().tryGenerateState();
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
                match = (GameSession) getDirector(DirectorType.GameSession);
                mode = match != null ? match.getGamemode() : null;
                ObjectState stuff = null;
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
                HgGame.Chat().addMessage(casualPrefix + "Debug draw is now " + (debugDraw ? "off" : "on"));
            }
            case "listPlayers" -> {
                for (var view: playerViews) {
                    HgGame.Chat().addMessage(view.name + "( uID: " + view.uniqueID + " | cID: " + view.connectionID + " )");
                }
            }
            case "kickPlayer" -> {
                if (!isServer) {
                    HgGame.Chat().addMessage("Can't kick players as Client");
                    return;
                }
                try {
                    int uniqueID = Integer.parseInt(args[0]);
                    PlayerView toKick = getPlayerViewByUniqueID(uniqueID);
                    if (toKick == localView) {
                        HgGame.Chat().addMessage("You can't kick yourself!");
                        return;
                    }
                    if (toKick == null) {
                        HgGame.Chat().addMessage("The uniqueID is invalid");
                        return;
                    }
                    HgGame.Chat().addMessage("Kicking " + toKick.name);
                    network.disconnectClient(toKick.connectionID);
                }
                catch (Exception ignored) {
                    HgGame.Chat().addMessage("Usage: /kickPlayer [uniqueID]");
                }
            }
            case "getLatestGames" -> {
                try {

                    int count = args.length == 0 ? 5 : Integer.parseInt(args[0]);
                    if (count <= 0) {
                        HgGame.Chat().addMessage("Count should be higher than 0!");
                        return;
                    }
                    var results = HgGame.Data().getDeathmatchResults().keySet().toArray(new Long[0]);
                    HgGame.Chat().addMessage("IDs: ");
                    for (int i = 0; i < count && i < results.length; i++)
                        HgGame.Chat().addMessage(results[i].toString());
                }
                catch (Exception ignored) {
                    HgGame.Chat().addMessage("Usage: /getLatestGames [count = 5]");
                }
            }
            case "getGame" -> {
                try {
                    long id = Long.parseLong(args[0]);
                    var result = HgGame.Data().getDeathmatchResults().get(id);
                    if (result == null) {
                        HgGame.Chat().addMessage("No such game found!");
                        return;
                    }
                    HgGame.Chat().addMessage("Game " + id + " results: ");
                    HgGame.Chat().addMessage(" Name | Score |  Winner ");
                    for (var player: result) {
                        HgGame.Chat().addMessage(player.playerName + "    " + player.score + "    " + (player.winner ? "Yes" : "No"));
                    }
                }
                catch (Exception ignored) {
                    HgGame.Chat().addMessage("Usage: /getGame [ID]");
                }
            }
            case "mousePos" -> {
                HgGame.GUI().toggleMouseDebug();
                HgGame.Chat().addMessage("Toggled mouse position view");
            }
            case "FPS" -> {
                HgGame.GUI().toggleFPS();
                HgGame.Chat().addMessage("Toggled FPS");
            }
            default -> HgGame.Chat().addMessage("[System] Unknown command: " + command);
        }
    }

    // --- Network related code --- //

    // Abandon hope,
    // All ye who enter here.

    // This code could be considered the core of the networking <-> game interaction logic
    // See hg.networking.packets for further implementation

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
            HgGame.Chat().addDebugMessage("Null PlayerView after addition!", DebugLevels.Error);
            return;
        }
        HgGame.Chat().addMessage(newView.name + " connected.");
    }

    /** Called if a client connected to this machine */
    public void onClientConnected(int connectionID) {
        // Empty for now lololololololo
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

            HgGame.Chat().addMessage(deadView.name + " disconnected.");
        }
    }

    /** Called if this machine obtained PlayerViews from the server */
    public void onInitializedByServer() {
        // Empty for now lololololololo
    }

    /** Called if this machine connected to a server */
    public void onConnect() {
        // Unused for now lololololololo
    }

    /** Called if this machine disconnected from a server */
    public void onDisconnect() {
        GameSession match = (GameSession) getDirector(DirectorType.GameSession);
        if (match != null) match.signalStop();

        MainMenu main = (MainMenu) getDirector(DirectorType.MainMenu);
        if (main != null) main.onDisconnectFromServer();

        tryRetireDirector(DirectorType.PauseMenu);

        HgGame.SetNotice("Got disconnected from server!", 120);
    }
}
