package hg.utils;

import com.badlogic.gdx.math.Vector2;

import static java.lang.Math.abs;

/**
 * Class that encapsulates a direction in the range [0; 360) degrees.
 * A 0 degree Angle points North. Angle value increases with clockwise rotation.
 * Adding or substracting Angles will keep the resulting value in the [0; 360) range.
 */
public class Angle {
    private float value;

    // Constructors

    public Angle() {
        this.value = 0f; // East
    }

    public Angle(Angle other) {
        this.value = other.value;
    }

    public Angle(float value) {
        this.value = HgMath.ScrollValue(value, 0f, 360f);
    }

    // Instance Methods

    public Angle set(Angle other) {
        this.value = HgMath.ScrollValue(other.value, 0f, 360f);
        return this;
    }

    public Angle set(float value) {
        this.value = HgMath.ScrollValue(value, 0f, 360f);
        return this;
    }

    public float getDeg() {
        return value;
    }

    public float getRad() {
        return HgMath.DegToRad(value);
    }

    public Angle add(Angle rhs) {
        value = HgMath.ScrollValue(value + rhs.value, 0f, 360f);
        return this;
    }

    public Angle add(float rhs) {
        value = HgMath.ScrollValue(value + rhs, 0f, 360f);
        return this;
    }

    public Angle sub(Angle rhs) {
        value = HgMath.ScrollValue(value - rhs.value, 0f, 360f);
        return this;
    }

    public Angle sub(float rhs) {
        value = HgMath.ScrollValue(value - rhs, 0f, 360f);
        return this;
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }

    // Static Methods

    public static Angle plus(Angle lhs, Angle rhs) {
        return new Angle(lhs).add(rhs);
    }

    public static Angle minus(Angle lhs, Angle rhs) {
        return new Angle(lhs).sub(rhs);
    }

    /**
     * Calculates the angle difference between two Angles
     * 0 degrees is North, with increasing values in clockwise rotation.
     * @return Angle difference, as the shortest arc between the two angles.
     * Negative value indicates counterclockwise distance.
     */
    public static double Delta(Angle first, Angle second) {
        double delta = abs(second.value - first.value);
        if (delta > 180.0) {
            if (second.value > 180.0) {
                delta = -delta;
            }
        } else if (second.value < first.value) {
            delta = -delta;
        }
        return delta;
    }

    public Vector2 normalVector() {
        float rads = HgMath.DegToRad(value);
        return new Vector2((float) Math.cos(rads), (float) Math.sin(rads));
    }

    public static Vector2 NormalVector(float value) {
        return new Angle(value).normalVector();
    }
}

