package hg.game;


import com.badlogic.gdx.math.Vector2;
import hg.directors.Director;
import hg.directors.DirectorTypes;
import hg.directors.LevelDirector;
import hg.entities.Entity;
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

    public EntityManager() {}

    public Entity addActor(ActorLibrary.Types entity, Vector2 position, float direction) {
        Entity newEntity = ActorLibrary.CreateActor(entity);
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

    public Director getDirector(DirectorTypes type) {
        Director existing = directors.get(type);

        if (existing != null) return existing;

        switch (type) {
            case LEVEL_DIRECTOR -> {
                LevelDirector director = new LevelDirector();
                directors.put(type, director);
                return director;
            }
            default -> throw new RuntimeException("Coudln't retrieve director of type " + type.toString());
        }
    }

    public void clearDirectorIfAny(DirectorTypes type) {
        Director existing = directors.remove(type);
        if (existing != null) {
            existing.destroy();
        }
    }

    public void clearActors() {
        for (var actor: actors.entrySet()) {
            actor.getValue().destroy();
        }
        actors.clear();
    }

    public void clearEnvironment() {
        for (var environment: environments.entrySet()) {
            environment.getValue().destroy();
        }
        environments.clear();
    }

    public void clearDirectors() {
        for (var director: directors.entrySet()) {
            director.getValue().destroy();
        }
        directors.clear();
    }

    public void update() {

        // Update all current entities based on network status

        for (var director : new ArrayList<>(directors.values()))
            director.serverUpdate(); // Directors are priority

        for (var actor : new ArrayList<>(actors.values()))
            actor.serverUpdate();

        for (var env : new ArrayList<>(environments.values()))
            env.serverUpdate();

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
