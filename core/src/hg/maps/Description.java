package hg.maps;

import com.badlogic.gdx.math.Vector2;

/** Holds the description of an object in a map. */
public class Description {
    public int objectType;
    public Vector2 position;
    public float angle;

    public int[] intParams;
    public float[] floatParams;
    public String stringParam;

    public Description setFloats(float... params) {
        floatParams = params;
        return this;
    }

    public Description setInts(int... params) {
        intParams = params;
        return this;
    }

    public Description setString(String param) {
        stringParam = param;
        return this;
    }

    public Description(int objectType, Vector2 position, float angle) {
        this.objectType = objectType;
        this.position = position;
        this.angle = angle;
    }
}