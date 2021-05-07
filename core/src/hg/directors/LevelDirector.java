package hg.directors;

import hg.entities.Entity;
import hg.game.HgGame;
import hg.maps.MapPrototype;

import java.util.LinkedList;

public class LevelDirector extends Director {

    private final LinkedList<Entity> environments = new LinkedList<>();

    public void LoadMap(MapPrototype prototype) {
        UnloadMap();

        for (var envProto : prototype.environments)
            environments.add(HgGame.Manager().addEnvironment(envProto.type, envProto.position, envProto.angle));
    }

    public void UnloadMap() {
        environments.forEach(Entity::signalDestroy);
        environments.clear();
    }

    @Override
    public void destroy() {
        UnloadMap();
    }
}
