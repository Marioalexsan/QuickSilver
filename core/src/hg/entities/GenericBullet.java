package hg.entities;

import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.engine.AssetEngine;
import hg.game.HgGame;
import hg.gamelogic.BaseStats;
import hg.gamelogic.AttackStats;
import hg.physics.*;
import hg.utils.Angle;

public class GenericBullet extends Entity {
    public float speed = 38f;
    public float maxDistance = 1800f;
    public float currentDistance = 0.0f;

    private final BasicSprite drawable = new BasicSprite();
    private final BoxCollider collider = new BoxCollider(32f, 12f);

    public GenericBullet() {
        AssetEngine assets = HgGame.Assets(); // Extra dependency

        collider.owner = this;
        collider.group = ColliderGroup.Player_Projectile;
        for (var value : ColliderGroup.values()) {
            collider.groupProperties.put(value, ColliderProperty.Notify);
        }
        collider.groupProperties.put(ColliderGroup.Environment_ShootThrough, ColliderProperty.DoNothing);
        collider.groupProperties.put(ColliderGroup.Player, ColliderProperty.DoNothing);

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
    public void clientUpdate() {
        serverUpdate();
    }

    @Override
    public void serverUpdate() {
        if (speed <= 0) {
            toBeDestroyed = true;
            return;
        }

        moveForward(speed);
        currentDistance += speed;

        if (currentDistance >= maxDistance) toBeDestroyed = true;
    }

    @Override
    public Collider getColliderIfAny() {
        return collider;
    }
}
