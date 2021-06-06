package hg.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.drawables.Drawable;
import hg.drawables.gfxeffects.BulletPath;
import hg.engine.AssetEngine;
import hg.game.HgGame;
import hg.gamelogic.ObjectState;
import hg.gamelogic.BaseStats;
import hg.gamelogic.AttackStats;
import hg.physics.*;
import hg.utils.Angle;

/** Bullet is a generic projectile, that hurts an entity on impact. Destroyed on impact based on group properties. */
public class Bullet extends Entity {
    public static class State extends ObjectState.PositionState {
        public float speed;
        public float maxDistance;
        public float currentDistance;
    }

    protected float speed = 38f;
    protected float maxDistance = 1800f;
    protected float currentDistance = 0.0f;

    protected int localOnly_initFrames = 4;

    private final BasicSprite drawable = new BasicSprite();
    private final BoxCollider collider = new BoxCollider(36f, 12f);

    public Bullet() {
        AssetEngine assets = HgGame.Assets(); // Extra dependency

        collider.owner = this;
        collider.group = ColliderGroup.PlayerProjectile;
        for (var value : ColliderGroup.values()) {
            collider.groupProperties.put(value, ColliderProperty.Notify);
        }
        collider.groupProperties.put(ColliderGroup.Environment_ShootThrough, ColliderProperty.DoNothing);
        collider.groupProperties.put(ColliderGroup.Player, ColliderProperty.DoNothing);
        collider.groupProperties.put(ColliderGroup.Pickups, ColliderProperty.DoNothing);
        collider.groupProperties.put(ColliderGroup.PlayerProjectile, ColliderProperty.DoNothing);

        drawable.setTexture(assets.loadTexture("Assets/Sprites/Projectiles/Bullet.png"));
        drawable.setTextureAngle(new Angle(-90f));
        drawable.setLayer(DrawLayer.FloorAir);

        collider.setPosition(position);
        collider.setAngle(angle);

        drawable.setPosition(position);
        drawable.setAngle(angle);
        drawable.centerToRegion();

        collider.registerToEngine(); // Allocation!
        drawable.registerToEngine(); // Allocation!
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public void onCombatCollision(Collider other) {
        toBeDestroyed = true;
    }

    @Override
    public void onGenericCollision(Collider other) {
        toBeDestroyed = true;
    }

    @Override
    public void onAttackHit(BaseStats defender) { }

    @Override
    public void onKill(Entity victim) {}

    @Override
    public void onHitByAttack(AttackStats attacker) { }

    @Override
    public void onDeath(Entity killer) {}

    @Override
    public void destroy() {
        collider.unregisterFromEngine(); // Deallocation!
        drawable.unregisterFromEngine(); // Deallocation!
    }

    @Override
    public void update() {
        if (speed <= 0) {
            toBeDestroyed = true;
            return;
        }

        BulletPath path = new BulletPath(new TextureRegion(HgGame.Assets().loadTexture("Assets/Sprites/Effects/PSTracer.png")), position, angle.getDeg(), speed, 15);
        if (localOnly_initFrames > 0) {
            path.getColor().mul(1f, 1f, 1f, 0.3f + 0.7f / localOnly_initFrames);
            localOnly_initFrames--;
        }
        path.setLayer(drawable.getLayer() - 1);
        HgGame.Manager().addGFXEffectToManage(path);

        moveForward(speed);
        currentDistance += speed;


        if (localOnly_initFrames > 0) localOnly_initFrames--;
        if (currentDistance >= maxDistance) toBeDestroyed = true;
    }

    @Override
    public Drawable[] getDrawableArray() {
        return new Drawable[] { drawable };
    }

    @Override
    public Collider[] getColliderArray() {
        return new Collider[] { collider };
    }

    @Override
    public ObjectState tryGenerateState() {
        State stuff = new State();
        stuff.copyPosition(this);
        stuff.speed = speed;
        stuff.currentDistance = currentDistance;
        stuff.maxDistance = maxDistance;
        return stuff;
    }

    @Override
    public void tryApplyState(ObjectState state) {
        if (state instanceof State) {
            State stuff = (State) state;
            stuff.applyPosition(this);
            this.speed = stuff.speed;
            this.currentDistance = stuff.currentDistance;
            this.maxDistance = stuff.maxDistance;
        }
    }
}
