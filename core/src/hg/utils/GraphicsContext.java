package hg.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GraphicsContext {
    public final SpriteBatch batch;
    public final OrthographicCamera camera;
    public final Vector2 cameraOffset;

    public GraphicsContext(SpriteBatch batch, OrthographicCamera camera, Vector2 cameraOffset) {
        this.batch = batch;
        this.camera = camera;
        this.cameraOffset = cameraOffset;
    }
}
