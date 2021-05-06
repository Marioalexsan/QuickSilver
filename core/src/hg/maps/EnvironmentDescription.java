package hg.maps;

import com.badlogic.gdx.math.Vector2;

public class EnvironmentDescription {
    public int type;
    public Vector2 position;
    public float angle;

    public EnvironmentDescription(int type, Vector2 position, float angle) {
        this.type = type;
        this.position = position;
        this.angle = angle;
    }
}