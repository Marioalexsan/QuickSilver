package hg.physics;

import com.badlogic.gdx.math.Vector2;
import hg.game.HgGame;
import hg.gamelogic.AttackStats;
import hg.gamelogic.BaseStats;
import hg.interfaces.ICollisionObserver;
import hg.utils.Angle;

import java.util.HashMap;

public abstract class Collider {

    public static final float StaticMassThreshold = 1000000f; // Colliders which have equal or higher mass than this are Static
    public static final float DefaultMass = 1000f; // Default value for collider mass

    protected Vector2 position = new Vector2();
    protected Vector2 center = new Vector2();
    protected Angle angle = new Angle();

    protected Vector2 posOffset = new Vector2();
    protected Vector2 cenOffset = new Vector2();
    protected Angle angOffset = new Angle();

    protected float mass = DefaultMass;

    public ICollisionObserver owner;
    public AttackStats attackStats;
    public BaseStats baseStats;
    public ColliderGroup group;
    public String specialCategory = "";

    public boolean enabled = true;

    public HashMap<ColliderGroup, ColliderProperty> groupProperties = new HashMap<>();

    public Collider() {
        for (var value : ColliderGroup.values()) groupProperties.put(value, ColliderProperty.CollideNotify);
    }

    // Getters and Setters

    public boolean isHeavy() { return this.mass >= StaticMassThreshold; }
    public void makeHeavy() { this.mass = StaticMassThreshold; }
    public float getMass() { return mass; }
    public void setMass(float mass) { this.mass = mass; }

    public Vector2 getPosition() { return position; }
    public Vector2 getPositionOffset() { return posOffset; }
    public Angle getAngle() { return angle; }
    public Angle getAngleOffset() { return angOffset; }
    public Vector2 getCenter() { return center; }
    public Vector2 getCenterOffset() { return cenOffset; }

    public void setPosition(Vector2 position) { this.position = position; }
    public void setPositionOffset(Vector2 offset) { this.posOffset = offset; }
    public void setAngle(Angle angle) { this.angle = angle; }
    public void setAngleOffset(Angle offset) { this.angOffset = offset; }
    public void setCenter(Vector2 center) { this.center = center; }
    public void setCenterOffset(Vector2 offset) { this.cenOffset = offset; }

    public void setPCA(Vector2 position, Vector2 center, Angle angle) {
        this.position = position;
        this.center = center;
        this.angle = angle;
    }

    public Vector2 trueCenter() {
        return new Vector2(-center.x - cenOffset.x, -center.y - cenOffset.y).rotateDeg(angle.getDeg() + angOffset.getDeg()).add(position).add(posOffset);
    }

    public void registerToEngine() { HgGame.Physics().registerCollider(this); }
    public void unregisterFromEngine() { HgGame.Physics().unregisterCollider(this); }

    public void setEnabled(boolean enable) { this.enabled = enable; }

    public boolean isActive() { return this.enabled; }
}
