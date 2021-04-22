package hg.directors;

import hg.entities.Entity;
import hg.game.HgGame;
import hg.maps.MapPrototype;

import java.util.LinkedList;

public class LevelDirector extends Director {

    private final LinkedList<Entity> environments = new LinkedList<>();

    public void LoadMap(MapPrototype prototype) {
        UnloadMap();

        for (var envProto : prototype.environments) {
            environments.add(HgGame.Entities().addEnvironment(envProto.type, envProto.position, envProto.angle));
        }
    }

    public void UnloadMap() {
        for (var env : environments) {
            env.signalDestruction();
        }
        environments.clear();
    }

    @Override
    public void clientUpdate() {}

    @Override
    public void serverUpdate() {}

    @Override
    public void destroy() {
        UnloadMap();
    }
}
