package hg.ui;

import com.badlogic.gdx.math.Vector2;
import hg.interfaces.IDestroyable;
import hg.utils.Angle;

public abstract class UIElement implements IDestroyable {
    protected final Vector2 position = new Vector2();
    protected final Vector2 center = new Vector2();
    protected final Angle angle = new Angle();

    protected boolean enabled = true;

    public void onLMBDown(float x, float y) {}
    public void onLMBUp(float x, float y) {}
    public void onLMBDragged(float x, float y) {}
    public void onMouseMove(float dx, float dy) {}
    public void onUpdate() {}

    public Vector2 getPosition() { return position; }
    public void setPosition(Vector2 position) { this.position.set(position); }
    public void setPosition(float x, float y) { this.position.set(x, y); }

    public Vector2 getCenter() { return center; }
    public void setCenter(Vector2 center) { this.center.set(center); }
    public void setCenter(float x, float y) { this.center.set(x, y); }

    public Angle getAngle() { return angle; }
    public void setAngle(Angle angle) { this.angle.set(angle); }
    public void setAngle(float angle) { this.angle.set(angle); }

    public void setEnabled(boolean enable) { this.enabled = enable; }

    public boolean isActive() { return this.enabled; }
}
