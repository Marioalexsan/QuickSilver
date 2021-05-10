package hg.drawables;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import hg.game.HgGame;
import hg.physics.Collider;
import hg.interfaces.IPolygon;
import hg.physics.SphereCollider;
import hg.utils.MathUtils;

import java.util.ArrayList;

/**
 * This drawable is intended for debug purposes only.
 * It can also only draw world colliders for the time being, AKA no GUI colliders.
 *
 * It can draw ISATPolygons and SphereColliders
 * It can also store a list of lines (raycasts) to draw on next draw() call
 */
public class ColliderDrawable extends Drawable {
    public static class VectorLine {
        public final Vector2 start = new Vector2();
        public final Vector2 end = new Vector2();

        public VectorLine(Vector2 start, Vector2 end) {
            this.start.set(start);
            this.end.set(end);
        }
    }

    public final ArrayList<Collider> collidersToDraw = new ArrayList<>();
    public final ArrayList<VectorLine> raycastsToDrawOnce = new ArrayList<>();

    private final float phaseSpeed = 0.01f;
    private float phaseRetain = 0f;

    private int phase = 0;
    private final Color color = new Color(0f, 0f, 0f, 1f);

    private ShapeRenderer renderer;

    public ColliderDrawable() {
        renderer = new ShapeRenderer();
    }

    @Override
    public void draw(SpriteBatch batch) {
        Vector2 cameraOffset = HgGame.Graphics().getCameraOffset().scl(-1f); // Extra dependency

        batch.end(); // End current batch

        // Copy / set batch properties
        renderer.setProjectionMatrix(new Matrix4(batch.getProjectionMatrix()));
        renderer.setTransformMatrix(new Matrix4(batch.getTransformMatrix()));

        renderer.setColor(color);


        // Draw colliders using black lines
        renderer.begin(ShapeRenderer.ShapeType.Line);
        for (var collider : collidersToDraw) {
            if (!collider.isActive()) continue;

            if (collider instanceof IPolygon) {
                Vector2[] vertices = ((IPolygon) collider).trueVertices();
                for (var vertex : vertices) vertex.add(cameraOffset);
                for (int i = 0; i < vertices.length; i++) {
                    renderer.rectLine(vertices[i], vertices[(i + 1) % vertices.length], 1f);
                }
            }
            else if (collider instanceof SphereCollider) {
                Vector2 center = collider.trueCenter();
                renderer.circle(center.x + cameraOffset.x, center.y + cameraOffset.y, ((SphereCollider) collider).getRadius());
            }
        }

        for (var raycast : raycastsToDrawOnce) {
            renderer.rectLine(raycast.start.add(cameraOffset), raycast.end.add(cameraOffset), 1f); // raycast modified then disposed of
        }
        raycastsToDrawOnce.clear();

        renderer.end();

        batch.begin(); // Resume current batch

        if (phaseRetain >= 0f) {
            phaseRetain -= phaseSpeed;
            return;
        }

        switch (phase) {
            case 0 -> {
                color.r += phaseSpeed;
                color.g = MathUtils.ClampValue(color.g - 2 * phaseSpeed, 0.5f, 1f);
                color.b = MathUtils.ClampValue(color.b - 2 * phaseSpeed, 0.5f, 1f);
                if (color.r > 1f) {
                    color.r = 1f;
                    phase = 1;
                    phaseRetain += 1f;
                }
            }
            case 1 -> {
                color.g += phaseSpeed;
                color.r = MathUtils.ClampValue(color.r - 2 * phaseSpeed, 0.5f, 1f);
                color.b = MathUtils.ClampValue(color.b - 2 * phaseSpeed, 0.5f, 1f);
                if (color.g > 1f) {
                    color.g = 1f;
                    phase = 2;
                    phaseRetain += 1f;
                }
            }
            case 2 -> {
                color.b += phaseSpeed;
                color.r = MathUtils.ClampValue(color.r - 2 * phaseSpeed, 0.5f, 1f);
                color.g = MathUtils.ClampValue(color.g - 2 * phaseSpeed, 0.5f, 1f);
                if (color.b > 1f) {
                    color.b = 1f;
                    phase = 0;
                    phaseRetain += 1f;
                }
            }
        }
    }
}
