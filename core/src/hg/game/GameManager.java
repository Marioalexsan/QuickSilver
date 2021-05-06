package hg.game;


import com.badlogic.gdx.math.Vector2;
import hg.directors.*;
import hg.entities.Entity;
import hg.libraries.ActorLibrary;
import hg.libraries.EnvironmentLibrary;
import hg.networking.PlayerView;
import hg.utils.BadCoderException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/** Holds various stuff relevant to the game, such as entities */
public class GameManager {
    private static final int StartingEntityID = 1;
    private int nextEntityID = StartingEntityID;

    private final HashMap<Integer, Entity> actors = new HashMap<>();
    private final HashMap<Integer, Entity> environments = new HashMap<>();
    private final HashMap<DirectorTypes, Director> directors = new HashMap<>();

    public PlayerView localView;
    public final ArrayList<PlayerView> playerViews = new ArrayList<>();

    public GameManager() {
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
        return addEnvironment(nextEntityID++, envType, position, direction);
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

        Director which = null;
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
        clearEnvironment();
        nextEntityID = StartingEntityID;
    }

    public void clearActors() {
        for (var actor: actors.entrySet()) {
            actor.getValue().signalDestroy();
            actor.getValue().destroy();
        }
        actors.clear();
    }

    public void clearEnvironment() {
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
        boolean isLocalOrServer = HgGame.Network().isLocalOrServer(); // TODO implement network roles

        // Update all current entities based on network status

        for (var director : new ArrayList<>(directors.values())) {
            director.localUpdate();

            if (isLocalOrServer) director.serverUpdate();
            else director.clientUpdate();
        }

        for (var actor : new ArrayList<>(actors.values())) {
            actor.localUpdate();

            if (isLocalOrServer) actor.serverUpdate();
            else actor.clientUpdate();
        }

        for (var env : new ArrayList<>(environments.values())) {
            env.localUpdate();

            if (isLocalOrServer) env.serverUpdate();
            else env.clientUpdate();
        }

        // Check for stuff to dispose of

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
        clearEnvironment();
        clearDirectors();
    }
}
