package hg.physics;

public class RaycastResults {
    public boolean hit;

    // Between 0.0 (start of segment) to 1.0 (end of segment)
    public float hitStart;

    public RaycastResults() {
        this.hit = false;
    }

    public RaycastResults(float hitStart) {
        this.hit = true;
        this.hitStart = hitStart;
    }
}
