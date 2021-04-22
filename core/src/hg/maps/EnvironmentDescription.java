package hg.maps;

import com.badlogic.gdx.math.Vector2;
import hg.libraries.EnvironmentLibrary;

public class EnvironmentDescription {
    public EnvironmentLibrary.Types type;
    public Vector2 position;
    public float angle;

    public EnvironmentDescription(EnvironmentLibrary.Types type, Vector2 position, float angle) {
        this.type = type;
        this.position = position;
        this.angle = angle;
    }
}