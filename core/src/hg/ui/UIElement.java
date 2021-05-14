package hg.ui;

import com.badlogic.gdx.math.Vector2;
import hg.interfaces.IDestroyable;
import hg.interfaces.IEnable;
import hg.utils.Angle;

public abstract class UIElement implements IDestroyable, IEnable {
    protected final Vector2 position = new Vector2();
    protected final Vector2 center = new Vector2();
    protected final Angle angle = new Angle();

    protected boolean toBeDestroyed;

    protected boolean enabled = true;

    public void onLMBDown(float x, float y) {}
    public void onLMBUp(float x, float y) {}
    public void onLMBDragged(float x, float y) {}
    public void onMouseMove(float dx, float dy) {}
    public void onUpdate() {}

    public Vector2 getPosition() { return position; }
    public Vector2 getCenter() { return center; }
    public Angle getAngle() { return angle; }

    public void setEnabled(boolean enable) { this.enabled = enable; }

    public boolean isActive() { return this.enabled; }

    @Override
    public void signalDestroy() { toBeDestroyed = true; }

    @Override
    public boolean isDestroySignalled() { return toBeDestroyed; }
}
