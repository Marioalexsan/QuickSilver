package hg.physics;

import com.badlogic.gdx.math.Vector2;

public class SATResults {
    public boolean collision;
    public Vector2 mtv;

    public SATResults() {
        this.collision = false;
    }

    public SATResults(Vector2 mtv) {
        this.collision = true;
        this.mtv = new Vector2(mtv);
    }
}