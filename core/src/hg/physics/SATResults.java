package hg.physics;

import com.badlogic.gdx.math.Vector2;

/** Holds results of a SAT collision test */
public class SATResults {
    public boolean collision;
    public Vector2 mtv = new Vector2();
    public Vector2 APositionDelta = new Vector2(); // Sum of MTVs in a collision check with multiple steps
    public Vector2 BPositionDelta = new Vector2();

    public SATResults() {
        this.collision = false;
    }

    public SATResults(Vector2 mtv) {
        this.collision = true;
        this.mtv = new Vector2(mtv);
    }
}