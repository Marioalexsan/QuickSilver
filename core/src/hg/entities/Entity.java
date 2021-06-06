package hg.entities;

import com.badlogic.gdx.math.Vector2;
import hg.drawables.Drawable;
import hg.gamelogic.ObjectState;
import hg.gamelogic.BaseStats;
import hg.interfaces.IDestroyable;
import hg.interfaces.INetInterface;
import hg.interfaces.IUpdateable;
import hg.interfaces.ICollisionObserver;
import hg.maps.Description;
import hg.physics.Collider;
import hg.utils.Angle;

/**
 * Entities are basic things that exist in the world.
 * (They don't really follow the whole ECS pattern though)
 */
public abstract class Entity implements ICollisionObserver, IUpdateable, IDestroyable, INetInterface {
    protected boolean toBeDestroyed = false;
    protected int type;
    protected int ID;

    protected final Vector2 position = new Vector2();
    protected final Angle angle = new Angle();

    protected BaseStats baseStats = null; // Entities should set this themselves!

    // Getters and Setters
    // These always copy the value given to them

    public Vector2 getPosition() { return position; }
    public Angle getAngle() { return angle; }

    public void move(Vector2 delta) { this.position.add(delta); }
    public void move(float x, float y) { this.position.add(x, y); }
    public void moveForward(float distance) { this.position.add(angle.normalVector().scl(distance)); }


    public BaseStats getStats() { return baseStats; }

    public void onDeath(Entity killer) {}
    public void onKill(Entity victim) {}

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }


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

    public Drawable getDrawable() {
        return null;
    }

    public Collider getCollider() {
        return null;
    }

    public Drawable[] getDrawableArray() {
        return null;
    }

    public Collider[] getColliderArray() {
        return null;
    }

    /** Generates a state that can be sent over network. Used by Servers. */
    public ObjectState tryGenerateState() {
        return null;
    }

    /** Applies a state that was sent over the network. Used by Clients */
    public void tryApplyState(ObjectState state) { }

    /** Applies some conditional state changes. Called by Level director during map actor instantiation. */
    public void tryApplyDescription(Description desc) { }
}
