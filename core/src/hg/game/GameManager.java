package hg.game;


import com.badlogic.gdx.math.Vector2;
import hg.directors.*;
import hg.drawables.BasicText;
import hg.engine.NetworkEngine;
import hg.entities.Entity;
import hg.libraries.ActorLibrary;
import hg.libraries.EnvironmentLibrary;
import hg.networking.NetworkRole;
import hg.networking.NetworkStatus;
import hg.networking.PlayerView;
import hg.networking.packets.*;
import hg.types.EntityType;
import hg.utils.BadCoderException;

import java.util.*;

/** Holds various stuff relevant to the game, such as entities */
public class GameManager {
    private static final int StartingID = 1;
    private int nextEntityID = StartingID;
    private int nextStaticEnvID = StartingID; // Is this needed?

    private final HashMap<Integer, Entity> actors = new HashMap<>();
    private final HashMap<Integer, Entity> environments = new HashMap<>();
    private final HashMap<DirectorTypes, Director> directors = new HashMap<>();

    public PlayerView localView;
    private final ArrayList<PlayerView> playerViews = new ArrayList<>();

    private final ChatSystem chatSystem;

    private final BasicText notice;
    private int noticeTimeLeft = 0;

    // Network Update stuff

    private int actorHeavyUpdateInterval = 8;

    private int actorNextHeavyUpdate = 0;



    public GameManager() {
        chatSystem = new ChatSystem();
        chatSystem.setEnabled(false);
        chatSystem.setPosition(-650, -500);

        // Notice - used for error messages

        notice = new BasicText(HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), "");
        notice.setConstraints(BasicText.HPos.Center, BasicText.VPos.Center, 0f);
        notice.setAlpha(0f);
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

    // TODO Probably called by the server. Either this or adjacent code should also send messages to clients
    /** Creates a player view and adds it to current views */
    public PlayerView createPlayerView(PlayerView.Type type) {
        if (!HgGame.Network().isLocalOrServer()) throw new BadCoderException("Non-server tried to create player views lol");

        PlayerView view = new PlayerView();
        view.uniqueID = generatePlayerViewID();
        view.viewType = type;

        playerViews.add(view);
        return view;
    }

    // TODO Probably called by clients
    public void addPlayerView(PlayerView newView) {
        for (var view : playerViews)
            if (view.uniqueID == newView.uniqueID) throw new BadCoderException("ID conflict when adding player view!");
        playerViews.add(newView);
    }

    // TODO Probably called by the server. Either this or adjacent code should also send messages to clients
    /** Removes a PlayerView by Unique ID and returns it */
    public PlayerView removePlayerView(int uniqueID) {
        PlayerView target = null;
        for (var view : playerViews) {
            if (view.uniqueID == uniqueID) {
                target = view;
                break;
            }
        }

        if (target == null) return null;
        if (target == localView) throw new BadCoderException("Someone tried to remove localview via uniqueID!");

        playerViews.remove(target);
        return target;
    }

    /** The nuclear option. */
    public void removeAllPlayerViews() {
        localView = null;
        playerViews.clear();
    }

    /** Returns an ID that is not in use by any other PlayerView right now */
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

    public PlayerView getUniqueIDPlayerView(int uniqueID) {
        for (var view: playerViews) {
            if (view.uniqueID == uniqueID) return view;
        }
        return null;
    }

    public PlayerView getConnectionIDPlayerView(int connectionID) {
        for (var view: playerViews) {
            if (view.connectionID != -1 && view.connectionID == connectionID) return view;
        }
        return null;
    }

    public PlayerView[] getPlayerViews() {
        return playerViews.toArray(new PlayerView[0]);
    }

    public Entity addActor(int entityType, Vector2 position, float direction) {
        return addActor(nextEntityID++, entityType, position, direction);
    }

    public Entity addActor(int ID, int entityType, Vector2 position, float direction) {
        Entity newEntity = ActorLibrary.CreateActor(entityType);
        if (newEntity != null) {
            newEntity.setPosition(position);
            newEntity.setAngle(direction);

            if (actors.put(ID, newEntity) != null) {
                chatSystem.addMessage("[Warn] Tried to add an entity with an existing ID");
            }
            newEntity.setID(ID);
        }


        NetworkEngine network = HgGame.Network();
        if (newEntity != null && network.isLocalOrServer()) {
            EntityAdded msg = new EntityAdded();
            msg.entityType = EntityType.Actors;
            msg.entitySubType = entityType;
            msg.entityID = ID;
            msg.posX = position.x;
            msg.posY = position.y;
            msg.angle = direction;

            network.sendPacketToAllClients(msg, true);
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

        newEntity.setPosition(position);
        newEntity.setAngle(direction);

        if (environments.put(ID, newEntity) != null) throw new RuntimeException("Tried to add an entity with an existing ID");
        newEntity.setID(ID);

        return newEntity;
    }

    /** Starts a director if it doesn't exist yet
     * Returns true if a director was created, false otherwise */
    public boolean addDirector(DirectorTypes type) {
        if (directors.get(type) != null) return false;

        Director which;
        switch (type) { // TODO Replace switch with dictionary
            case InitDirector -> which = new InitDirector();
            case QuitDirector -> which = new QuitDirector();
            case MainMenu -> which = new MainMenu();
            case MatchDirector -> which = new MatchDirector();
            case LevelDirector -> which = new LevelDirector();
            case InGameMenu -> which = new InGameMenu();
            case LobbyDirector -> which = new LobbyDirector();
            default -> throw new RuntimeException("Coudln't retrieve director of type " + type.toString());
        }
        directors.put(type, which);
        return true;
    }

    public Director getDirector(DirectorTypes type) {
        return directors.get(type);
    }

    public Director addAndGetDirector(DirectorTypes type) {
        addDirector(type);
        return directors.get(type);
    }

    public void clearDirectorIfAny(DirectorTypes type) {
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
        for (var director: directors.entrySet()) {
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

        for (var director : new ArrayList<>(directors.values()))
            director.update();

        for (var actor : new ArrayList<>(actors.values()))
            actor.update();

        // Directors

        LinkedList<DirectorTypes> directorsToRemove = new LinkedList<>();
        for (var director : directors.entrySet())
            if (director.getValue().isDestroySignalled())
                directorsToRemove.add(director.getKey());

        for (var key : directorsToRemove) directors.remove(key).destroy();

        // Actors

        LinkedList<Integer> actorsToRemove = new LinkedList<>();
        for (var actor : actors.entrySet())
            if (actor.getValue().isDestroySignalled())
                actorsToRemove.add(actor.getKey());

        for (var key : actorsToRemove) actors.remove(key).destroy();
        if (isServer) {
            for (var key : actorsToRemove) {
                EntityDestroyed msg = new EntityDestroyed();
                msg.entityType = EntityType.Actors;
                msg.entityID = key;
                network.sendPacketToAllClients(msg, true);
            }
        }

        // Environments

        LinkedList<Integer> environmentsToRemove = new LinkedList<>();
        for (var environment : environments.entrySet())
            if (environment.getValue().isDestroySignalled())
                environmentsToRemove.add(environment.getKey());

        for (var key : environmentsToRemove) environments.remove(key).destroy();


        // Send network updates

        if (isServer && actorNextHeavyUpdate-- <= 0) {
            actorNextHeavyUpdate = actorHeavyUpdateInterval;
            for (var actor: actors.entrySet()) {
                State stuff = actor.getValue().tryGenerateState();
                if (stuff != null) {
                    StateUpdate update = new StateUpdate();
                    update.targetType = EntityType.Actors;
                    update.payload = stuff;
                    update.targetID = actor.getKey();
                    network.sendPacketToAllClients(update, false); // UDP
                }
            }
        }
    }

    public void cleanup() {
        clearActors();
        clearStaticEnvironments();
        clearDirectors();
        notice.unregisterFromEngine();
    }

    public void onChatMessageEntered(String message) {
        if (message.length() > 0 && message.charAt(0) == '/') {
            String[] parts = message.substring(1).split(" ");
            for (int i = 0; i < parts.length; i++)
                parts[i] = parts[i].trim();
            parseCommand(parts[0], parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0]);
        }
        else {
            ChatMessage broadcast = new ChatMessage();
            broadcast.message = message;
            broadcast.senderUniqueID = localView.uniqueID;

            // Send in network
            if (HgGame.Network().isLocalOrServer()) {
                for (var view : playerViews) {
                    if (view != localView) HgGame.Network().sendPacketToClient(view.connectionID, broadcast, true);
                }
            }
            else {
                HgGame.Network().sendPacketToServer(broadcast, true);
            }
        }
    }

    public void parseCommand(String command, String[] args) {
        if (command.length() == 0) return;

        String casualPrefix = "[" + command + "] ";
        switch (command) {
            case "debugColliders" -> {
                boolean debugDraw = HgGame.Physics().getDebugDraw();
                HgGame.Physics().setDebugDraw(!debugDraw);
                chatSystem.addMessage(casualPrefix + "Debug draw is now " + (debugDraw ? "off" : "on"));
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
            onDisconnectFromServer();
        }

        if (network.isLocalOrServer()) {
            var newConnections = network.dumpConnectedClients();
            var newDisconnects = network.dumpDisconnectedClients();

            // Process disconnects, then connects
            for (var connection: newDisconnects) {
                onClientDisconnect(connection);
            }

            for (var connection: newConnections) {
                onClientConnect(connection);
            }

            for (var msg: newMessages) msg.packet.parseOnServer(msg.connectionID);
        }
        else {
            for (var msg: newMessages) msg.packet.parseOnClient();
        }
    }

    /** Called if this machine is a server and a client connected */
    public void onClientConnect(int connectionID) {
        // This does nothing for now, since a true connection is established only when
        // a client obtains a playerview
    }

    /** Called if this machine is a server and a client disconnected */
    public void onClientDisconnect(int connectionID) {
        // Tell everyone a playerview disconnected if applicable

        PlayerView deadView = getConnectionIDPlayerView(connectionID);

        if (deadView != null) {
            removePlayerView(deadView.uniqueID);

            PlayerViewDisconnected msg = new PlayerViewDisconnected();
            msg.deadUniqueID = deadView.uniqueID;

            for (var view: playerViews) {
                if (view != localView) HgGame.Network().sendPacketToClient(view.connectionID, msg, true);
            }

            HgGame.Manager().getChatSystem().addMessage(deadView.name + " disconnected.");
        }
    }

    /** Called if this machine is a client and it connected to a server */
    public void onConnectToServer() {
    }

    /** Called if this machine is a client and it disconnected from a server */
    public void onDisconnectFromServer() {
        MatchDirector match = (MatchDirector) getDirector(DirectorTypes.MatchDirector);
        if (match != null) match.receiveStop();

        MainMenu main = (MainMenu) getDirector(DirectorTypes.MainMenu);
        if (main != null) main.onDisconnectFromServer();

        setNotice("Got disconnected from server!", 120);
    }
}
