package hg.entities;

import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.drawables.Drawable;
import hg.engine.AssetEngine;
import hg.game.HgGame;
import hg.gamelogic.states.BulletState;
import hg.gamelogic.states.State;
import hg.gamelogic.BaseStats;
import hg.gamelogic.AttackStats;
import hg.physics.*;
import hg.utils.Angle;

/** Bullet is a generic projectile, that hurts an entity on impact. Destroyed on impact based on group properties. */
public class Bullet extends Entity {
    public float speed = 38f;
    public float maxDistance = 1800f;
    public float currentDistance = 0.0f;

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
        drawable.setLayer(DrawLayer.CeilingAir);

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

        moveForward(speed);
        currentDistance += speed;

        if (currentDistance >= maxDistance) toBeDestroyed = true;
    }

    @Override
    public Drawable getDrawableIfAny() {
        return drawable;
    }

    @Override
    public Collider getColliderIfAny() {
        return collider;
    }

    @Override
    public State tryGenerateState() {
        BulletState stuff = new BulletState();
        stuff.copyPosition(this);
        stuff.speed = speed;
        stuff.currentDistance = currentDistance;
        stuff.maxDistance = maxDistance;
        return stuff;
    }

    @Override
    public void tryApplyState(State state) {
        if (state instanceof BulletState) {
            BulletState stuff = (BulletState) state;
            stuff.applyPosition(this);
            this.speed = stuff.speed;
            this.currentDistance = stuff.currentDistance;
            this.maxDistance = stuff.maxDistance;
        }
    }
}
