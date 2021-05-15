package hg.physics;

import com.badlogic.gdx.math.Vector2;
import hg.interfaces.IPolygon;

/**
 * Collider that represents a right triangle.
 * The right triangle represents a box of given width and height,
 * which has been cut in half by the secondary diagonal, and whose
 * resulting bottom half was discarded.
 * The center of (0,0) corresponds to the box's center point.
 */
public class RTriangleCollider extends Collider implements IPolygon {
    private float width;
    private float height;

    public RTriangleCollider(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public Vector2[] trueVertices() {
        Vector2[] vertices = new Vector2[] {
                new Vector2(width / -2, height / -2),
                new Vector2(width / -2, height / 2),
                new Vector2(width / 2, height / 2)
        };

        for (var vertex : vertices) {
            vertex.add(-center.x - cenOffset.x, -center.y - cenOffset.y).rotateDeg(angle.getDeg() + angOffset.getDeg()).add(position).add(posOffset);
        }

        return vertices;
    }
}
