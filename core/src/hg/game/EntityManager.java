package hg.game;


import com.badlogic.gdx.math.Vector2;
import hg.directors.*;
import hg.entities.Entity;
import hg.entities.Player;
import hg.libraries.ActorLibrary;
import hg.libraries.EnvironmentLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class EntityManager {
    private int nextEntityID = 0;

    private final HashMap<Integer, Entity> actors = new HashMap<>();
    private final HashMap<Integer, Entity> environments = new HashMap<>();
    private final HashMap<DirectorTypes, Director> directors = new HashMap<>();

    private Player localPlayer = null;

    public EntityManager() {}

    public void setLocalPlayer(Player player) {
        localPlayer = player;
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

    public Entity addActor(int entityType, Vector2 position, float direction) {
        Entity newEntity = ActorLibrary.CreateActor(entityType);
        if (newEntity != null) {
            newEntity.setPosition(position);
            newEntity.setAngle(direction);

            int nextID = nextEntityID++;

            actors.put(nextID, newEntity);
            newEntity.setID(nextID);
        }
        return newEntity;
    }

    public Entity addEnvironment(int ID, Vector2 position, float direction) {
        Entity newEntity = EnvironmentLibrary.CreateEnvironment(ID);

        newEntity.setPosition(position);
        newEntity.setAngle(direction);

        int nextID = nextEntityID++;
        environments.put(nextID, newEntity);
        newEntity.setID(nextID);

        return newEntity;
    }

    /** Starts a director if it doesn't exist yet
     * Returns true if a director was created, false otherwise
     */
    public boolean addDirector(DirectorTypes type) {
        if (directors.get(type) != null) return false;

        Director which = null;
        switch (type) { // TODO Replace switch with dictionary
            case InitDirector -> which = new InitDirector();
            case QuitDirector -> which = new QuitDirector();
            case MainMenuDirector -> which = new MainMenuDirector();
            case MatchDirector -> which = new MatchDirector();
            case LevelDirector -> which = new LevelDirector();
            case InGameMenuDirector -> which = new InGameMenuDirector();
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

    public void clearActors() {
        for (var actor: actors.entrySet()) {
            actor.getValue().signalDestruction();
            actor.getValue().destroy();
        }
        actors.clear();
    }

    public void clearEnvironment() {
        for (var environment: environments.entrySet()) {
            environment.getValue().signalDestruction();
            environment.getValue().destroy();
        }
        environments.clear();
    }

    public void clearDirectors() {
        for (var director: directors.entrySet()) {
            director.getValue().signalDestruction();
            director.getValue().destroy();
        }
        directors.clear();
    }

    public void update() {
        boolean isServerOrLocal = true; // TODO implement network roles

        // Update all current entities based on network status

        for (var director : new ArrayList<>(directors.values())) {
            if (isServerOrLocal) director.serverUpdate();
            else director.clientUpdate();
        }

        for (var actor : new ArrayList<>(actors.values())) {
            if (isServerOrLocal) actor.serverUpdate();
            else actor.clientUpdate();
        }

        for (var env : new ArrayList<>(environments.values())) {
            if (isServerOrLocal) env.serverUpdate();
            else env.clientUpdate();
        }

        // Check for stuff to dispose of

        // Directors

        LinkedList<DirectorTypes> directorsToRemove = new LinkedList<>();
        for (var director : directors.entrySet())
            if (director.getValue().isDestructionSignalled())
                directorsToRemove.add(director.getKey());

        for (var key : directorsToRemove) directors.remove(key).destroy();

        // Actors

        LinkedList<Integer> actorsToRemove = new LinkedList<>();
        for (var actor : actors.entrySet())
            if (actor.getValue().isDestructionSignalled())
                actorsToRemove.add(actor.getKey());

        for (var key : actorsToRemove) actors.remove(key).destroy();

        // Environments

        LinkedList<Integer> environmentsToRemove = new LinkedList<>();
        for (var environment : environments.entrySet())
            if (environment.getValue().isDestructionSignalled())
                environmentsToRemove.add(environment.getKey());

        for (var key : environmentsToRemove) environments.remove(key).destroy();
    }

    public void cleanup() {
        clearActors();
        clearEnvironment();
        clearDirectors();
    }
}
