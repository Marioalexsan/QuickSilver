package hg.entities;

import com.badlogic.gdx.math.Vector2;
import hg.drawables.Drawable;
import hg.game.State;
import hg.gamelogic.BaseStats;
import hg.interfaces.IDestroyable;
import hg.interfaces.IUpdateable;
import hg.interfaces.ICollisionObserver;
import hg.physics.Collider;
import hg.utils.Angle;

/**
 * Entities are basic things that exist in the world.
 * (They don't really follow the whole ECS pattern though)
 */
public abstract class Entity implements ICollisionObserver, IUpdateable, IDestroyable {
    protected boolean toBeDestroyed = false;
    protected int ID;

    protected final Vector2 position = new Vector2();
    protected final Angle angle = new Angle();

    protected BaseStats baseStats = null; // Entities should set this themselves!

    // Getters and Setters
    // These always copy the value given to them

    public Vector2 getPosition() { return position; }
    public void setPosition(Vector2 position) { this.position.set(position); }
    public void setPosition(float x, float y) { this.position.set(x, y); }
    public void move(Vector2 delta) { this.position.add(delta); }
    public void move(float x, float y) { this.position.add(x, y); }
    public void moveForward(float distance) { this.position.add(angle.normalVector().scl(distance)); }

    public Angle getAngle() { return angle; }
    public void setAngle(Angle angle) { this.angle.set(angle); }
    public void setAngle(float angle) { this.angle.set(angle); }

    public BaseStats getStats() { return baseStats; }

    public abstract void onDeath(Entity killer);
    public abstract void onKill(Entity victim);

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    // Some other stuff

    @Override
    public void signalDestroy() {
        toBeDestroyed = true;
    }

    @Override
    public boolean isDestroySignalled() {
        return toBeDestroyed;
    }

    public Drawable getDrawableIfAny() {
        return null;
    }

    public Collider getColliderIfAny() {
        return null;
    }

    public State tryGenerateState() {
        return null;
    }

    public void tryApplyState(State state) { }

}
