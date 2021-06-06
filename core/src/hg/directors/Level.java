package hg.directors;

import com.badlogic.gdx.math.Vector2;
import hg.entities.Entity;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.maps.MapPrototype;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/** LevelLoader can load a level from a given MapPrototype. It is also responsible for cleaning up environments created from MapPrototypes.
 * Actors instantiated from loads are handled by GameManager instead. */
public class Level extends Director {

    private final LinkedList<Entity> environments = new LinkedList<>();
    private final ArrayList<Vector2> randomSpawnpoints = new ArrayList<>();
    private final ArrayList<ArrayList<Vector2>> teamSpawnpoints = new ArrayList<>();

    public void loadMap(MapPrototype prototype) {
        unloadMap();

        GameManager manager = HgGame.Manager();

        for (var envProto : prototype.environments)
            environments.add(manager.addEnvironment(envProto.objectType, envProto.position, envProto.angle));

        for (var randomPoint: prototype.randomSpawnpoints)
            randomSpawnpoints.add(new Vector2(randomPoint));

        for (var teamPoints: prototype.teamSpawnpoints) {
            ArrayList<Vector2> points = new ArrayList<>();
            teamSpawnpoints.add(points);
            for (var point: teamPoints)
                points.add(new Vector2(point));
        }

        // Instantiate onLoadActors if server
        if (HgGame.Network().isLocalOrServer()) {
            for (var actorDesc: prototype.onLoadActors) {
                var actor = manager.addActor(actorDesc.objectType, actorDesc.position, actorDesc.angle);

                // A hacky way of instantiating actors with different starting properties
                actor.tryApplyDescription(actorDesc);
            }
        }
    }

    public void unloadMap() {
        environments.forEach(Entity::signalDestroy);
        environments.clear();
        randomSpawnpoints.clear();
        teamSpawnpoints.clear();
    }

    public Vector2 getRandomSpawnpoint() {
        if (randomSpawnpoints.size() > 0)
            return new Vector2(randomSpawnpoints.get(Math.abs(new Random().nextInt()) % randomSpawnpoints.size()));
        return new Vector2(0, 0);
    }

    @Override
    public void destroy() {
        unloadMap();
    }
}
