package hg.maps;

import com.badlogic.gdx.math.Vector2;

public class EnvironmentDescription {
    public int ID;
    public Vector2 position;
    public float angle;

    public EnvironmentDescription(int ID, Vector2 position, float angle) {
        this.ID = ID;
        this.position = position;
        this.angle = angle;
    }
}