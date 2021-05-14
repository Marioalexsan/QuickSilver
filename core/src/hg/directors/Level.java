package hg.directors;

import com.badlogic.gdx.math.Vector2;
import hg.entities.Entity;
import hg.game.HgGame;
import hg.maps.MapPrototype;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Level extends Director {

    private final LinkedList<Entity> environments = new LinkedList<>();
    private final ArrayList<Vector2> randomSpawnpoints = new ArrayList<>();
    private final ArrayList<ArrayList<Vector2>> teamSpawnpoints = new ArrayList<>();

    public void loadMap(MapPrototype prototype) {
        unloadMap();

        for (var envProto : prototype.environments)
            environments.add(HgGame.Manager().addEnvironment(envProto.type, envProto.position, envProto.angle));

        for (var randomPoint: prototype.randomSpawnpoints)
            randomSpawnpoints.add(new Vector2(randomPoint));

        for (var teamPoints: prototype.teamSpawnpoints) {
            ArrayList<Vector2> points = new ArrayList<>();
            teamSpawnpoints.add(points);
            for (var point: teamPoints)
                points.add(new Vector2(point));
        }
    }

    public void unloadMap() {
        environments.forEach(Entity::signalDestroy);
        environments.clear();
        randomSpawnpoints.clear();
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
