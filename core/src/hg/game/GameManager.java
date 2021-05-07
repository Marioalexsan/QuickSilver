package hg.game;


import com.badlogic.gdx.math.Vector2;
import hg.directors.*;
import hg.engine.NetworkEngine;
import hg.entities.Entity;
import hg.libraries.ActorLibrary;
import hg.libraries.EnvironmentLibrary;
import hg.networking.PlayerView;
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
    public final ArrayList<PlayerView> playerViews = new ArrayList<>();

    private ChatSystem chatSystem;

    public GameManager() {
        chatSystem = new ChatSystem();
        chatSystem.setEnabled(false);
        chatSystem.setPosition(-650, -500);
    }

    public void enableChatSystem() {
        chatSystem.setEnabled(true);
    }

    public void disableChatSystem() {
        chatSystem.setEnabled(false);
        chatSystem.clear();
    }

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
    public void removePlayerView(int ID) {
        PlayerView target = null;
        for (var view : playerViews) {
            if (view.uniqueID == ID) {
                target = view;
                break;
            }
        }

        if (target == null) return;
        if (target == localView) throw new BadCoderException("Someone tried to remove localview via ID!");

        playerViews.remove(target);
        target.onRemove();
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

    public Entity addActor(int entityType, Vector2 position, float direction) {
        return addActor(nextEntityID++, entityType, position, direction);
    }

    public Entity addActor(int ID, int entityType, Vector2 position, float direction) {
        Entity newEntity = ActorLibrary.CreateActor(entityType);
        if (newEntity != null) {
            newEntity.setPosition(position);
            newEntity.setAngle(direction);

            if (actors.put(ID, newEntity) != null) throw new RuntimeException("Tried to add an entity with an existing ID");
            newEntity.setID(ID);
        }
        return newEntity;
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

        if (chatSystem != null) {
            if (chatSystem.isDestroySignalled()) {
                chatSystem.destroy();
                chatSystem = null;
            }
            else chatSystem.onUpdate();
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

        // Environments

        LinkedList<Integer> environmentsToRemove = new LinkedList<>();
        for (var environment : environments.entrySet())
            if (environment.getValue().isDestroySignalled())
                environmentsToRemove.add(environment.getKey());

        for (var key : environmentsToRemove) environments.remove(key).destroy();
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
    }

    public void parseCommand(String command, String[] args) {
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

    public void parseNetworkMessages() {
        NetworkEngine network = HgGame.Network();
        var messages = network.dumpMessages();

        if (network.isLocalOrServer()) {
            for (var msg: messages) msg.packet.parseOnServer();
        }
        else {
            for (var msg: messages) msg.packet.parseOnClient();
        }
    }

    /** Called if this machine is a server and a client disconnected */
    public void onClientDisconnect() {

    }

    /** Called if this machine is a client and it disconnected from a server */
    public void onDisconnectFromServer() {
        MatchDirector match = (MatchDirector) getDirector(DirectorTypes.MatchDirector);

        if (match != null) match.receiveStop();
    }
}
