package hg.maps;

import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;

/** A structure that defines a level. Can be instantiated using LevelLoader */
public class MapPrototype {
    /** Environments are static and not sent over the network */
    public final LinkedList<Description> environments = new LinkedList<>();

    /** These actors that are created on map load. Only Server should instantiate them. Clients instead receive EntityAdded messages. */
    public final LinkedList<Description> onLoadActors = new LinkedList<>();

    /** These points are used for spawning in players. */
    public final LinkedList<Vector2> randomSpawnpoints = new LinkedList<>();
    public final LinkedList<LinkedList<Vector2>> teamSpawnpoints = new LinkedList<>();
}
