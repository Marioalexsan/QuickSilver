package hg.maps;

import com.badlogic.gdx.math.Vector2;

public class Description {
    public int objectType;
    public Vector2 position;
    public float angle;

    public Description(int objectType, Vector2 position, float angle) {
        this.objectType = objectType;
        this.position = position;
        this.angle = angle;
    }
}