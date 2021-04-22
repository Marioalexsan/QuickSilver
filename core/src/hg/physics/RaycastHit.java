package hg.physics;

public class RaycastHit {
    public float distance; // Between 0f and 1f
    public Collider target;

    public RaycastHit(float distance, Collider target) {
        this.distance = distance;
        this.target = target;
    }
}
