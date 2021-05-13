package hg.maps;

import com.badlogic.gdx.math.Vector2;
import hg.libraries.EnvironmentLibrary;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MapPrototype {
    public final LinkedList<EnvironmentDescription> environments = new LinkedList<>();
    public final LinkedList<Vector2> randomSpawnpoints = new LinkedList<>();
    public final LinkedList<LinkedList<Vector2>> teamSpawnpoints = new LinkedList<>();
}
