package hg.physics;

import com.badlogic.gdx.math.Vector2;
import hg.interfaces.IPolygon;

/**
 * Collider that represents a box of given width and height.
 * The center of (0,0) corresponds to the box's center point.
 */
public class BoxCollider extends Collider implements IPolygon {
    private float width;
    private float height;

    public BoxCollider(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Vector2[] trueVertices() {
        Vector2[] vertices = new Vector2[] {
                new Vector2(width / -2, height / -2),
                new Vector2(width / -2, height / 2),
                new Vector2(width / 2, height / 2),
                new Vector2(width / 2, height / -2)
        };

        for (var vertex : vertices) {
            vertex.add(-center.x - cenOffset.x, -center.y - cenOffset.y).rotateDeg(angle.getDeg() + angOffset.getDeg()).add(position).add(posOffset);
        }

        return vertices;
    }
}
