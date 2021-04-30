package hg.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.interfaces.callbacks.IButtonCallback;
import hg.physics.BoxCollider;
import hg.physics.CollisionAlgorithms;

public class ClickButton extends UIElement {
    private final BasicSprite drawable;
    private final BoxCollider collider;

    private IButtonCallback callback;

    public boolean toBeDestroyed;

    public ClickButton(Texture tex, int width, int height) {
        drawable = new BasicSprite(tex);
        collider = new BoxCollider(width, height);

        drawable.setLayer(DrawLayer.GUIDefault);
        drawable.setCameraUse(false);
        drawable.centerToRegion();

        drawable.setPosition(position);
        drawable.setCenter(center);
        drawable.setAngle(angle);

        collider.setPosition(position);
        collider.setCenter(center);
        collider.setAngle(angle);

        drawable.registerToEngine();
    }

    public void setCallback(IButtonCallback callback) {
        this.callback = callback;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        collider.setEnabled(enabled);
        drawable.setEnabled(enabled);
    }

    @Override
    public void signalDestruction() { toBeDestroyed = true; }

    @Override
    public boolean isDestructionSignalled() { return toBeDestroyed; }

    @Override
    public void destroy() {
        drawable.unregisterFromEngine();
    }

    @Override
    public void onLMBDown(float x, float y) {
        if (enabled && callback != null && CollisionAlgorithms.PointHit(new Vector2(x, y), collider)) callback.clicked();
    }
}
