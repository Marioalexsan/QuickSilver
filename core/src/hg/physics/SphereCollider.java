package hg.physics;

/**
 * Collider that represents a circle of given radius.
 * The center of (0,0) corresponds to the circle's center point.
 */
public class SphereCollider extends Collider {
    private float radius;

    public SphereCollider(float radius) {
        this.radius = radius;
    }

    public float getRadius() { return this.radius; }
    public void setRadius(float radius) { this.radius = radius; }
}
